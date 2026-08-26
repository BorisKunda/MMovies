package com.bk.mmovies.data.source.remote.interceptor

import com.bk.mmovies.data.source.remote.LOGIN_WITH_CREDENTIALS_ENDPOINT
import com.bk.mmovies.data.source.remote.QUERY_PARAM_API_KEY
import com.bk.mmovies.data.source.remote.QUERY_PARAM_SESSION_ID
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level

// Secrets TMDB carries in the query string. `api_key` is the user's own key;
// `session_id` and `request_token` are bearer-equivalent — anyone holding one
// can act as the account until it is revoked.
private val REDACTED_QUERY_PARAMS = listOf(
        QUERY_PARAM_API_KEY,
        QUERY_PARAM_SESSION_ID,
        "request_token"
                                          )

/**
 * Body-level HTTP logging for debug builds, except on the credential-login
 * call, which is logged without its body.
 *
 * `HttpLoggingInterceptor.redactQueryParams` only covers the query string, so
 * at [Level.BODY] the login request — which posts `username`/`password` as
 * JSON — was writing the user's plaintext password into logcat.
 *
 * The level is fixed per delegate instance rather than toggled per request:
 * `HttpLoggingInterceptor.level` is shared mutable state, and flipping it
 * mid-flight would race concurrent calls into logging the wrong thing.
 */
class DebugLoggingInterceptor(logger: HttpLoggingInterceptor.Logger) : Interceptor {

    private val bodyLogger = buildLogger(logger, Level.BODY)
    private val bodilessLogger = buildLogger(logger, Level.BASIC)

    override fun intercept(chain: Interceptor.Chain): Response {
        val isCredentialLogin = chain.request().url.encodedPath
                .contains(LOGIN_WITH_CREDENTIALS_ENDPOINT)
        val delegate = if (isCredentialLogin) bodilessLogger else bodyLogger
        return delegate.intercept(chain)
    }

    private fun buildLogger(
            logger: HttpLoggingInterceptor.Logger,
            level: Level
                           ): HttpLoggingInterceptor =
            HttpLoggingInterceptor(logger).apply {
                this.level = level
                REDACTED_QUERY_PARAMS.forEach { redactQueryParams(it) }
            }
}
