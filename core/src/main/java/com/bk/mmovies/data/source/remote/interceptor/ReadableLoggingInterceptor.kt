package com.bk.mmovies.data.source.remote.interceptor

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Debug HTTP logging that writes each call as ONE block instead of the stock
 * `HttpLoggingInterceptor`'s dozens of interleaved lines (one per header):
 *
 * ```
 * ┌─ POST https://host/path?key=***
 * │ { pretty-printed request body }
 * ├─ 200 OK · 8132 ms
 * │ { pretty-printed response body }
 * └─
 * ```
 *
 * - No header noise; the block says whether the response came from OkHttp's
 *   cache (TMDB has one) rather than the network.
 * - Because a call is logged as a single unit after it finishes, concurrent
 *   calls (the catalog fires several TMDB requests at once) can't interleave.
 * - Values of [redactedQueryParams] are masked in the logged URL.
 * - Calls whose path contains [skipBodiesForPathContaining] are logged without
 *   either body (e.g. the credential login, which posts a plaintext password).
 * - Bodies are capped at [maxBodyChars] so a big TMDB list doesn't bury
 *   everything else.
 */
class ReadableLoggingInterceptor(
        private val log: (String) -> Unit,
        private val redactedQueryParams: Collection<String> = emptyList(),
        private val skipBodiesForPathContaining: String? = null,
        private val maxBodyChars: Int = DEFAULT_MAX_BODY_CHARS
                                ) : Interceptor {

    private val prettyGson = GsonBuilder().setPrettyPrinting().create()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val logBodies = skipBodiesForPathContaining?.let { !request.url.encodedPath.contains(it) } ?: true
        val startNanos = System.nanoTime()

        val response = try {
            chain.proceed(request)
        } catch (e: IOException) {
            emit(
                    header = "┌─ ${request.method} ${redact(request.url.toString())}",
                    requestBody = if (logBodies) request.bodyText() else OMITTED,
                    resultLine = "├─ FAILED after ${elapsedMs(startNanos)} ms · ${e.javaClass.simpleName}: ${e.message}",
                    responseBody = null
                )
            throw e
        }

        val fromCache = response.networkResponse == null && response.cacheResponse != null
        emit(
                header = "┌─ ${request.method} ${redact(request.url.toString())}",
                requestBody = if (logBodies) request.bodyText() else OMITTED,
                resultLine = "├─ ${response.statusText()} · ${elapsedMs(startNanos)} ms" +
                        if (fromCache) " · from cache" else "",
                responseBody = if (logBodies) response.bodyText() else OMITTED
            )
        return response
    }

    private fun emit(header: String, requestBody: String?, resultLine: String, responseBody: String?) {
        val message = buildString {
            appendLine(header)
            requestBody?.let { appendBody(it) }
            appendLine(resultLine)
            responseBody?.let { appendBody(it) }
            append("└─")
        }
        // Log.d silently truncates around 4000 bytes - split on line breaks
        // so a long body is still shown in full (up to maxBodyChars).
        chunk(message).forEach(log)
    }

    private fun StringBuilder.appendBody(body: String) {
        body.lineSequence().forEach { appendLine("│ $it") }
    }

    private fun chunk(message: String): List<String> {
        val chunks = mutableListOf<String>()
        val current = StringBuilder()
        message.lineSequence().forEach { line ->
            if (current.isNotEmpty() && current.length + line.length + 1 > MAX_LOG_CHUNK_CHARS) {
                chunks += current.toString()
                current.clear()
            }
            if (current.isNotEmpty()) current.append('\n')
            current.append(line)
        }
        if (current.isNotEmpty()) chunks += current.toString()
        return chunks
    }

    private fun redact(url: String): String =
            redactedQueryParams.fold(url) { acc, param ->
                acc.replace(Regex("([?&])${Regex.escape(param)}=[^&]*"), "$1$param=***")
            }

    private fun elapsedMs(startNanos: Long): Long = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos)

    private fun Request.bodyText(): String? {
        val body = body ?: return null
        if (body.isDuplex() || body.isOneShot() || !body.contentType().isTextual()) return "(${body.contentType()} body)"
        val buffer = Buffer().also { body.writeTo(it) }
        return format(buffer.readUtf8())
    }

    private fun Response.bodyText(): String? {
        val body = body ?: return null
        if (!body.contentType().isTextual()) return "(${body.contentType()}, ${body.contentLength()} bytes)"
        // peekBody leaves the real body intact for the caller.
        val text = peekBody(MAX_PEEK_BYTES).string()
        return if (text.isEmpty()) "(empty body)" else format(text)
    }

    // Small JSON bodies (a Gemini answer, an error, a login token) are
    // pretty-printed; big ones (a page of 20 TMDB movies) stay compact,
    // because expanding them puts every genre id on its own line and buries
    // everything else. Parsing happens before truncating, so a cut-off body
    // can't break it; non-JSON falls back to the raw text.
    private fun format(text: String): String {
        val element = runCatching { JsonParser.parseString(text) }.getOrNull()
        val formatted = when {
            element == null                        -> text
            text.length <= PRETTY_MAX_CHARS        -> prettyGson.toJson(element)
            else                                   -> element.toString()
        }
        return if (formatted.length > maxBodyChars) {
            formatted.take(maxBodyChars) + "\n… (${formatted.length - maxBodyChars} more chars)"
        } else {
            formatted
        }
    }

    // HTTP/2 responses carry no reason phrase, so response.message is blank.
    private fun Response.statusText(): String = message.ifBlank {
        when (code) {
            200  -> "OK"
            201  -> "Created"
            204  -> "No Content"
            304  -> "Not Modified"
            400  -> "Bad Request"
            401  -> "Unauthorized"
            403  -> "Forbidden"
            404  -> "Not Found"
            429  -> "Too Many Requests"
            500  -> "Internal Server Error"
            502  -> "Bad Gateway"
            503  -> "Service Unavailable"
            504  -> "Gateway Timeout"
            else -> ""
        }
    }.let { if (it.isEmpty()) "$code" else "$code $it" }

    private fun MediaType?.isTextual(): Boolean =
            this != null && (type == "text" || subtype.contains("json") || subtype.contains("xml") || subtype.contains("x-www-form-urlencoded"))

    private companion object {
        const val DEFAULT_MAX_BODY_CHARS = 3_000
        const val PRETTY_MAX_CHARS = 1_500
        const val MAX_LOG_CHUNK_CHARS = 3_500
        const val MAX_PEEK_BYTES = 256L * 1024
        const val OMITTED = "(body not logged)"
    }
}
