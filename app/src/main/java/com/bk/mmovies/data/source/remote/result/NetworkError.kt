package com.bk.mmovies.data.source.remote.result

import com.bk.mmovies.data.source.remote.dto.ErrorDto

sealed interface NetworkError {
    data class ExceptionError(val cause: Throwable) : NetworkError
    data class HttpError(
            val responseCode: Int,
            val responseMessage: String?,
            val errorBody: ErrorDto?
                        ) : NetworkError

    data object EmptyBody : NetworkError
}

/**
 * True when the call never got a usable answer out of the server, which in
 * practice means connectivity rather than anything the request did wrong.
 *
 * Callers pick a localized message from this; the raw `errorBody.message`
 * TMDB returns is English-only and used to leak straight to the UI in an app
 * that ships Russian and Hebrew.
 */
val NetworkError.isConnectivityFailure: Boolean
    get() = this is NetworkError.ExceptionError || this is NetworkError.EmptyBody
