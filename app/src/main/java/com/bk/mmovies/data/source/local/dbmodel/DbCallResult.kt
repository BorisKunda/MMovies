package com.bk.mmovies.data.source.local.dbmodel

sealed interface DbCallResult<out T> {
    data class Success<T>(val data: T) : DbCallResult<T>
    data class Failure(val errorMessage: String) : DbCallResult<Nothing>
}