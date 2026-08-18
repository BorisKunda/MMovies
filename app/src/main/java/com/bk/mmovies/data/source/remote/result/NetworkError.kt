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

fun NetworkError.toErrorMessage(fallback: String): String {
    return when (this) {
        is NetworkError.ExceptionError, NetworkError.EmptyBody -> "$fallback, please check your network"
        is NetworkError.HttpError                               -> errorBody?.message ?: fallback
    }
}
