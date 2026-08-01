package com.bk.mmovies.data.repositoryimpl

import com.bk.mmovies.data.source.local.preferences.ApiKeyStorage
import com.bk.mmovies.data.source.remote.NetworkManager
import com.bk.mmovies.data.source.remote.api.TmdbApi
import com.bk.mmovies.data.source.remote.dto.TokenValidityDto
import com.bk.mmovies.data.source.remote.result.ApiCallResult
import com.bk.mmovies.data.source.remote.result.NetworkError
import com.bk.mmovies.domain.model.result.ApiKeyValidationResult
import com.bk.mmovies.domain.repository.ApiKeyRepository
import javax.inject.Inject

class ApiKeyRepositoryImpl @Inject constructor(
        private val api: TmdbApi,
        private val apiKeyStorage: ApiKeyStorage,
        private val networkManager: NetworkManager
                                              ) : ApiKeyRepository {

    override fun getApiKey(): String? {
        return apiKeyStorage.getApiKey()
    }

    override fun saveApiKey(apiKey: String) {
        apiKeyStorage.saveApiKey(apiKey)
    }

    override suspend fun getApiKeyValidationResult(): ApiKeyValidationResult {//will be called after key saved
        val result: ApiCallResult<TokenValidityDto> = networkManager.executeApiCall(
                "GetTokenValidationResult",
                apiCall = { api.getIsApiV3TokenValid() })
        return when (result) {
            is ApiCallResult.Failure -> {
                when (result.error) {
                    is NetworkError.ExceptionError, NetworkError.EmptyBody -> {
                        ApiKeyValidationResult.Failure("Couldn't verify API key, please check your network")
                    }

                    is NetworkError.HttpError                              -> {
                        val httpCode = result.error.responseCode
                        val errorDto = result.error.errorBody
                        val apiStatusCode = errorDto?.statusCode ?: 0
                        if (httpCode == 401 && (apiStatusCode == 7 || apiStatusCode == 10)) {
                            ApiKeyValidationResult.Failure(errorDto?.message ?: "Invalid API key")
                        } else {
                            ApiKeyValidationResult.Failure("Couldn't verify API key")
                        }
                    }
                }
            }
            is ApiCallResult.Success<TokenValidityDto> -> {
                val isTokenValid: Boolean? = result.data.isTokenValid
                if (isTokenValid != null) {
                    if (isTokenValid) {
                        ApiKeyValidationResult.Success
                    } else ApiKeyValidationResult.Failure(
                            "Invalid API key"
                                                         )
                } else {
                    ApiKeyValidationResult.Failure("Couldn't verify API key")
                }
            }
        }
    }
}
