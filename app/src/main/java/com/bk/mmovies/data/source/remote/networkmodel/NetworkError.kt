package com.bk.mmovies.data.source.remote.networkmodel

import com.bk.mmovies.data.source.remote.networkmodel.dto.ErrorDto

sealed interface NetworkError {
    data class ExceptionError(val cause: Throwable) : NetworkError
    data class HttpError(
        val responseCode: Int,
        val responseMessage: String?,
        val errorBody: ErrorDto?
    ) : NetworkError
    data object EmptyBody : NetworkError
}


