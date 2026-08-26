package com.bk.mmovies.data.repositoryimpl

import android.content.Context
import com.bk.mmovies.R
import com.bk.mmovies.data.source.local.LocalSessionDataCleaner
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
import com.bk.mmovies.data.source.remote.result.isConnectivityFailure
import com.bk.mmovies.domain.model.result.AccountDetailsResult
import com.bk.mmovies.domain.model.result.ApiKeyValidationResult
import com.bk.mmovies.domain.model.result.GuestSessionIdResult
import com.bk.mmovies.domain.model.result.LoginSessionIdResult
import com.bk.mmovies.domain.model.result.LoginValidationTokenResult
import com.bk.mmovies.domain.model.result.LoginWithCredentialsResult
import com.bk.mmovies.domain.model.result.LogoutResult
import com.bk.mmovies.domain.repository.AuthenticationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
        private val api: TmdbApi,
        private val authCredentialsSharedPrefs: AuthCredentialsSharedPrefs,
        private val networkManager: NetworkManager,
        private val localSessionDataCleaner: LocalSessionDataCleaner,
        @ApplicationContext private val context: Context
                                                      ) : AuthenticationRepository {

    private fun message(resId: Int): String = context.getString(resId)

    // Connectivity failures get one shared message; everything else gets the
    // caller's operation-specific one. Both are localized, unlike the raw
    // English text TMDB puts in an error body.
    private fun NetworkError.toLocalizedMessage(fallbackResId: Int): String =
            if (isConnectivityFailure) message(R.string.error_no_network_generic)
            else message(fallbackResId)

    override suspend fun getApiKeyValidationResult(): ApiKeyValidationResult {
        val result: ApiCallResult<V3TokenValidityDto> = networkManager.executeApiCall(
                "GetTokenValidationResult",
                apiCall = { api.getIsApiV3TokenValid() })
        return when (result) {
            is ApiCallResult.Failure                     -> {
                when (result.error) {
                    is NetworkError.ExceptionError, NetworkError.EmptyBody -> {
                        ApiKeyValidationResult.Failure(message(R.string.error_api_key_verify_no_network))
                    }

                    is NetworkError.HttpError                              -> {
                        val httpCode = result.error.responseCode
                        val errorDto = result.error.errorBody
                        val apiStatusCode = errorDto?.statusCode ?: 0
                        if (httpCode == 401 && (apiStatusCode == TMDB_ERROR_CODE_INVALID_API_KEY || apiStatusCode == TMDB_ERROR_CODE_SUSPENDED_API_KEY)) {
                            ApiKeyValidationResult.Failure(message(R.string.error_api_key_invalid))
                        } else {
                            ApiKeyValidationResult.Failure(message(R.string.error_api_key_verify_failed))
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
                            message(R.string.error_api_key_invalid)
                                                         )
                } else {
                    ApiKeyValidationResult.Failure(message(R.string.error_api_key_verify_failed))
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
                LoginValidationTokenResult.Failure(
                        result.error.toLocalizedMessage(R.string.error_login_start_failed)
                                                   )
            }
            is ApiCallResult.Success<LoginValidationTokenDto> -> {
                val loginValidationToken = result.data.loginValidationToken
                if (result.data.success == true && loginValidationToken != null) {
                    LoginValidationTokenResult.Success(loginValidationToken)
                } else {
                    LoginValidationTokenResult.Failure(message(R.string.error_login_start_failed))
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
                        result.error.toLocalizedMessage(R.string.error_login_invalid_credentials)
                                                   )
            }
            is ApiCallResult.Success<LoginValidationTokenDto> -> {
                val validatedToken = result.data.loginValidationToken
                if (result.data.success == true && validatedToken != null) {
                    LoginWithCredentialsResult.Success(validatedToken)
                } else {
                    LoginWithCredentialsResult.Failure(
                            message(R.string.error_login_invalid_credentials)
                                                       )
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
                LoginSessionIdResult.Failure(
                        result.error.toLocalizedMessage(R.string.error_login_failed)
                                             )
            }
            is ApiCallResult.Success<LoginSessionIdDto> -> {
                val sessionId = result.data.sessionId
                if (result.data.success == true && sessionId != null) {
                    LoginSessionIdResult.Success(sessionId)
                } else {
                    LoginSessionIdResult.Failure(message(R.string.error_login_failed))
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
                GuestSessionIdResult.Failure(
                        result.error.toLocalizedMessage(R.string.error_guest_session_failed)
                                             )
            }
            is ApiCallResult.Success<GuestSessionIdDto> -> {
                val guestSessionId = result.data.guestSessionId
                if (result.data.success == true && guestSessionId != null) {
                    GuestSessionIdResult.Success(guestSessionId)
                } else {
                    GuestSessionIdResult.Failure(message(R.string.error_guest_session_failed))
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
            // Clearing prefs alone left the favorite-id caches and the search
            // history on the device, so whoever signed in next inherited the
            // previous account's stars and saw all of their past searches.
            localSessionDataCleaner.clearAccountScopedData()
            LogoutResult.Success
        } catch (exception: Exception) {
            LogoutResult.Failure(message(R.string.error_logout_failed))
        }
    }

    override suspend fun getAccountDetailsResult(sessionId: String): AccountDetailsResult {
        val result: ApiCallResult<AccountDetailsDto> = networkManager.executeApiCall(
                "GetAccountDetails",
                apiCall = { api.getAccountDetails(sessionId) })
        return when (result) {
            is ApiCallResult.Failure                  -> {
                AccountDetailsResult.Failure(
                        result.error.toLocalizedMessage(R.string.error_account_details_failed)
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
