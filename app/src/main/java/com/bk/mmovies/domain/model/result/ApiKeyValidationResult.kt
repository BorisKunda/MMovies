package com.bk.mmovies.domain.model.result


sealed interface ApiKeyValidationResult {
    data object Success : ApiKeyValidationResult
    data class Failure(val message: String) : ApiKeyValidationResult
}

