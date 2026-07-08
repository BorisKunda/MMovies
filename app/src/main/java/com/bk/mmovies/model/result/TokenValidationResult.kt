package com.bk.mmovies.model.result


sealed interface TokenValidationResult {
    data object Success : TokenValidationResult
    data class Failure(val message: String) : TokenValidationResult
}

