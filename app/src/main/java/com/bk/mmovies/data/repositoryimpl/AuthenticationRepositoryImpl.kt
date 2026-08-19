package com.bk.mmovies.data.repositoryimpl

import com.bk.mmovies.data.source.local.preferences.AuthCredentialsSharedPrefs
import com.bk.mmovies.data.source.remote.AVATAR_PATH_SIZE_SEGMENT
import com.bk.mmovies.data.source.remote.NetworkManager
import com.bk.mmovies.data.source.remote.TMDB_ERROR_CODE_INVALID_API_KEY
import com.bk.mmovies.data.source.remote.TMDB_ERROR_CODE_SUSPENDED_API_KEY
import com.bk.mmovies.data.source.remote.TMDB_IMAGE_BASE_URL
import com.bk.mmovies.data.source.remote.api.TmdbApi
import com.bk.mmovies.data.source.remote.dto.AccountDetailsDto
import com.bk.mmovies.data.source.remote.dto.AvatarDto
import com.bk.mmovies.data.source.remote.dto.DeleteSessionRequestDto
import com.bk.mmovies.data.source.remote.dto.GuestSessionIdDto
import com.bk.mmovies.data.source.remote.dto.LoginSessionIdDto
import com.bk.mmovies.data.source.remote.dto.LoginValidationTokenDto
import com.bk.mmovies.data.source.remote.dto.LoginWithCredentialsRequestDto
import com.bk.mmovies.data.source.remote.dto.V3TokenValidityDto
import com.bk.mmovies.data.source.remote.result.ApiCallResult
import com.bk.mmovies.data.source.remote.result.NetworkError
import com.bk.mmovies.data.source.remote.result.toErrorMessage
import com.bk.mmovies.domain.model.result.AccountDetailsResult
import com.bk.mmovies.domain.model.result.ApiKeyValidationResult
import com.bk.mmovies.domain.model.result.GuestSessionIdResult
import com.bk.mmovies.domain.model.result.LoginSessionIdResult
import com.bk.mmovies.domain.model.result.LoginValidationTokenResult
import com.bk.mmovies.domain.model.result.LoginWithCredentialsResult
import com.bk.mmovies.domain.model.result.LogoutResult
import com.bk.mmovies.domain.repository.AuthenticationRepository
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
        private val api: TmdbApi,
        private val authCredentialsSharedPrefs: AuthCredentialsSharedPrefs,
        private val networkManager: NetworkManager
                                                      ) : AuthenticationRepository {

    override suspend fun getApiKeyValidationResult(): ApiKeyValidationResult {
        val result: ApiCallResult<V3TokenValidityDto> = networkManager.executeApiCall(
                "GetTokenValidationResult",
                apiCall = { api.getIsApiV3TokenValid() })
        return when (result) {
            is ApiCallResult.Failure                     -> {
                when (result.error) {
                    is NetworkError.ExceptionError, NetworkError.EmptyBody -> {
                        ApiKeyValidationResult.Failure("Couldn't verify API key, please check your network")
                    }

                    is NetworkError.HttpError                              -> {
                        val httpCode = result.error.responseCode
                        val errorDto = result.error.errorBody
                        val apiStatusCode = errorDto?.statusCode ?: 0
                        if (httpCode == 401 && (apiStatusCode == TMDB_ERROR_CODE_INVALID_API_KEY || apiStatusCode == TMDB_ERROR_CODE_SUSPENDED_API_KEY)) {
                            ApiKeyValidationResult.Failure(errorDto?.message ?: "Invalid API key")
                        } else {
                            ApiKeyValidationResult.Failure("Couldn't verify API key")
                        }
                    }
                }
            }
            is ApiCallResult.Success<V3TokenValidityDto> -> {
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

    override suspend fun getLoginValidationTokenResult(): LoginValidationTokenResult {
        val result: ApiCallResult<LoginValidationTokenDto> = networkManager.executeApiCall(
                "GetLoginValidationToken",
                apiCall = { api.loginStep1GetValidationToken() })
        return when (result) {
            is ApiCallResult.Failure                          -> {
                LoginValidationTokenResult.Failure(result.error.toErrorMessage("Couldn't start login"))
            }
            is ApiCallResult.Success<LoginValidationTokenDto> -> {
                val loginValidationToken = result.data.loginValidationToken
                if (result.data.success == true && loginValidationToken != null) {
                    LoginValidationTokenResult.Success(loginValidationToken)
                } else {
                    LoginValidationTokenResult.Failure("Couldn't start login")
                }
            }
        }
    }

    override suspend fun getLoginWithCredentialsResult(
            username: String,
            password: String,
            loginValidationToken: String
                                                       ): LoginWithCredentialsResult {
        val result: ApiCallResult<LoginValidationTokenDto> = networkManager.executeApiCall(
                "GetLoginWithCredentials",
                apiCall = {
                    api.loginStep2WithUsernameAndPassword(
                            LoginWithCredentialsRequestDto(
                                    username,
                                    password,
                                    loginValidationToken
                                                     )
                                                     )
                })
        return when (result) {
            is ApiCallResult.Failure                          -> {
                LoginWithCredentialsResult.Failure(
                        result.error.toErrorMessage("Invalid username or password")
                                                   )
            }
            is ApiCallResult.Success<LoginValidationTokenDto> -> {
                val validatedToken = result.data.loginValidationToken
                if (result.data.success == true && validatedToken != null) {
                    LoginWithCredentialsResult.Success(validatedToken)
                } else {
                    LoginWithCredentialsResult.Failure("Invalid username or password")
                }
            }
        }
    }

    override suspend fun getLoginSessionIdResult(loginValidationToken: String): LoginSessionIdResult {
        val result: ApiCallResult<LoginSessionIdDto> = networkManager.executeApiCall(
                "GetLoginSessionId",
                apiCall = { api.loginStep3GetSessionId(loginValidationToken) })
        return when (result) {
            is ApiCallResult.Failure                    -> {
                LoginSessionIdResult.Failure(result.error.toErrorMessage("Couldn't log in"))
            }
            is ApiCallResult.Success<LoginSessionIdDto> -> {
                val sessionId = result.data.sessionId
                if (result.data.success == true && sessionId != null) {
                    LoginSessionIdResult.Success(sessionId)
                } else {
                    LoginSessionIdResult.Failure("Couldn't log in")
                }
            }
        }
    }

    override suspend fun getGuestSessionIdResult(): GuestSessionIdResult {
        val result: ApiCallResult<GuestSessionIdDto> = networkManager.executeApiCall(
                "GetGuestSessionId",
                apiCall = { api.getGuestSessionId() })
        return when (result) {
            is ApiCallResult.Failure                    -> {
                GuestSessionIdResult.Failure(result.error.toErrorMessage("Couldn't continue as guest"))
            }
            is ApiCallResult.Success<GuestSessionIdDto> -> {
                val guestSessionId = result.data.guestSessionId
                if (result.data.success == true && guestSessionId != null) {
                    GuestSessionIdResult.Success(guestSessionId)
                } else {
                    GuestSessionIdResult.Failure("Couldn't continue as guest")
                }
            }
        }
    }


    override fun saveSharedPrefApiKey(apiKey: String) {
        authCredentialsSharedPrefs.saveSharedPrefApiKey(apiKey)
    }

    override fun getSharedPrefApiKey(): String? {
        return authCredentialsSharedPrefs.getSharedPrefApiKey()
    }

    override fun removeSharedPrefApiKey() {
        authCredentialsSharedPrefs.removeApiKey()
    }

    override fun saveSharedPrefLoginValidationToken(loginValidationToken: String) {
        authCredentialsSharedPrefs.saveSharedPrefLoginValidationToken(loginValidationToken)
    }

    override fun getSharedPrefLoginValidationToken(): String? {
        return authCredentialsSharedPrefs.getSharedPrefLoginValidationToken()
    }

    override fun saveSharedPrefLoginSessionId(id: String) {
        authCredentialsSharedPrefs.saveSharedPrefLoginSessionId(id)
    }

    override fun getSharedPrefLoginSessionId(): String? {
        return authCredentialsSharedPrefs.getSharedPrefLoginSessionId()
    }

    override fun saveSharedPrefGuestSessionId(id: String) {
        authCredentialsSharedPrefs.saveSharedPrefGuestSessionId(id)
    }

    override fun getSharedPrefGuestSessionId(): String? {
        return authCredentialsSharedPrefs.getSharedPrefGuestSessionId()
    }

    override fun saveSharedPrefAccountId(accountId: Int) {
        authCredentialsSharedPrefs.saveSharedPrefAccountId(accountId)
    }

    override fun getSharedPrefAccountId(): Int? {
        return authCredentialsSharedPrefs.getSharedPrefAccountId()
    }

    override suspend fun logout(): LogoutResult {
        val loginSessionId = authCredentialsSharedPrefs.getSharedPrefLoginSessionId()
        if (loginSessionId != null) {
            networkManager.executeApiCall(
                    "DeleteSession",
                    apiCall = { api.deleteSession(DeleteSessionRequestDto(loginSessionId)) })
        }
        return try {
            authCredentialsSharedPrefs.clearSession()
            LogoutResult.Success
        } catch (exception: Exception) {
            LogoutResult.Failure(exception.message ?: "Couldn't log out")
        }
    }

    override suspend fun getAccountDetailsResult(sessionId: String): AccountDetailsResult {
        val result: ApiCallResult<AccountDetailsDto> = networkManager.executeApiCall(
                "GetAccountDetails",
                apiCall = { api.getAccountDetails(sessionId) })
        return when (result) {
            is ApiCallResult.Failure                  -> {
                AccountDetailsResult.Failure(
                        result.error.toErrorMessage("Couldn't load account details")
                                             )
            }
            is ApiCallResult.Success<AccountDetailsDto> -> {
                val dto = result.data
                val name = dto.name?.takeIf { it.isNotBlank() } ?: dto.username ?: ""
                AccountDetailsResult.Success(
                        accountId = dto.id ?: 0,
                        name = name,
                        avatarUrl = dto.avatar.toAvatarUrl()
                                             )
            }
        }
    }

    private fun AvatarDto?.toAvatarUrl(): String {
        val tmdbAvatarPath = this?.tmdb?.avatarPath ?: return ""
        return "$TMDB_IMAGE_BASE_URL$AVATAR_PATH_SIZE_SEGMENT$tmdbAvatarPath"
    }
}
