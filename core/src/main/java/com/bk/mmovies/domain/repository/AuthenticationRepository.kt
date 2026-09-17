package com.bk.mmovies.domain.repository

import com.bk.mmovies.domain.model.result.AccountDetailsResult
import com.bk.mmovies.domain.model.result.ApiKeyValidationResult
import com.bk.mmovies.domain.model.result.GuestSessionIdResult
import com.bk.mmovies.domain.model.result.LoginSessionIdResult
import com.bk.mmovies.domain.model.result.LoginValidationTokenResult
import com.bk.mmovies.domain.model.result.LoginWithCredentialsResult
import com.bk.mmovies.domain.model.result.LogoutResult


interface AuthenticationRepository {
    suspend fun getApiKeyValidationResult(): ApiKeyValidationResult
    suspend fun getLoginValidationTokenResult(): LoginValidationTokenResult
    suspend fun getLoginWithCredentialsResult(
            username: String,
            password: String,
            loginValidationToken: String
                                              ): LoginWithCredentialsResult
    suspend fun getLoginSessionIdResult(loginValidationToken: String): LoginSessionIdResult
    suspend fun getGuestSessionIdResult(): GuestSessionIdResult
    fun saveSharedPrefLoginValidationToken(loginValidationToken: String)
    fun getSharedPrefLoginValidationToken(): String?
    fun saveSharedPrefApiKey(apiKey: String)
    fun getSharedPrefApiKey(): String?
    fun removeSharedPrefApiKey()
    fun saveSharedPrefLoginSessionId(id: String)
    fun getSharedPrefLoginSessionId(): String?
    fun saveSharedPrefGuestSessionId(id: String)
    fun getSharedPrefGuestSessionId(): String?
    fun saveSharedPrefAccountId(accountId: Int)
    fun getSharedPrefAccountId(): Int?
    suspend fun logout(): LogoutResult
    suspend fun getAccountDetailsResult(sessionId: String): AccountDetailsResult
}

