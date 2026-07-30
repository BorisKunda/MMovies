package com.bk.mmovies.domain.repository

import com.bk.mmovies.domain.model.result.ApiKeyValidationResult


interface ApiKeyRepository {
    fun getApiKey(): String?
    fun saveApiKey(apiKey: String)
    suspend fun getApiKeyValidationResult(): ApiKeyValidationResult
}