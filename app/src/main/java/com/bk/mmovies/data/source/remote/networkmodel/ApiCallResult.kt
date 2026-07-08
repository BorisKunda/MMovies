package com.bk.mmovies.data.source.remote.networkmodel

sealed interface ApiCallResult<out T> {
    data class Success<T>(val data: T) : ApiCallResult<T>
    data class Failure(val error: NetworkError) : ApiCallResult<Nothing>
}