package com.bk.mmovies.data.source.remote.interceptor

import com.bk.mmovies.data.source.remote.LOGIN_WITH_CREDENTIALS_ENDPOINT
import com.bk.mmovies.data.source.remote.QUERY_PARAM_API_KEY
import com.bk.mmovies.data.source.remote.QUERY_PARAM_SESSION_ID
import okhttp3.Interceptor
import okhttp3.Response

// Secrets TMDB carries in the query string. `api_key` is the user's own key;
// `session_id` and `request_token` are bearer-equivalent — anyone holding one
// can act as the account until it is revoked.
private val REDACTED_QUERY_PARAMS = listOf(
        QUERY_PARAM_API_KEY,
        QUERY_PARAM_SESSION_ID,
        "request_token"
                                          )

/**
 * Body-level HTTP logging of the TMDB calls for debug builds (see
 * [ReadableLoggingInterceptor] for the format), except on the credential-login
 * call, which is logged without its body: that request posts
 * `username`/`password` as JSON, and a body log would write the user's
 * plaintext password into logcat.
 */
class DebugLoggingInterceptor(log: (String) -> Unit) : Interceptor {

    private val delegate = ReadableLoggingInterceptor(
            log = log,
            redactedQueryParams = REDACTED_QUERY_PARAMS,
            skipBodiesForPathContaining = LOGIN_WITH_CREDENTIALS_ENDPOINT
                                                     )

    override fun intercept(chain: Interceptor.Chain): Response = delegate.intercept(chain)
}
