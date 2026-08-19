package com.bk.mmovies.data.source.local.preferences

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthCredentialsSharedPrefs @Inject constructor(
        @ApplicationContext context: Context
                                       ) {
    private val sharedPreferences =
            context.getSharedPreferences(
                    PREFERENCES_NAME,
                    Context.MODE_PRIVATE
                                        )

    fun saveSharedPrefApiKey(apiKey: String) {
        val trimmedApiKey = apiKey.trim()
        sharedPreferences.edit {
            putString(
                    API_KEY,
                    trimmedApiKey
                     )
        }
    }

    fun removeApiKey() {
        sharedPreferences.edit {
            remove(API_KEY)
        }
    }

    fun getSharedPrefApiKey(): String? {
        return sharedPreferences.getString(
                API_KEY,
                null
                                          )
    }

    fun saveSharedPrefLoginValidationToken(loginValidationToken: String) {
        sharedPreferences.edit {
            putString(
                    LOGIN_VALIDATION_TOKEN,
                    loginValidationToken
                     )
        }
    }

    fun getSharedPrefLoginValidationToken(): String? {
        return sharedPreferences.getString(
                LOGIN_VALIDATION_TOKEN,
                null
                                          )
    }

    fun saveSharedPrefLoginSessionId(loginSessionId: String) {
        sharedPreferences.edit {
            putString(
                    LOGIN_SESSION_ID,
                    loginSessionId
                     )
        }
    }

    fun getSharedPrefLoginSessionId(): String? {
        return sharedPreferences.getString(
                LOGIN_SESSION_ID,
                null
                                          )
    }

    fun saveSharedPrefGuestSessionId(guestSessionId: String) {
        sharedPreferences.edit {
            putString(
                    GUEST_SESSION_ID,
                    guestSessionId
                     )
        }
    }

    fun getSharedPrefGuestSessionId(): String? {
        return sharedPreferences.getString(
                GUEST_SESSION_ID,
                null
                                          )
    }

    fun saveSharedPrefAccountId(accountId: Int) {
        sharedPreferences.edit {
            putInt(
                    ACCOUNT_ID,
                    accountId
                  )
        }
    }

    fun getSharedPrefAccountId(): Int? {
        val accountId = sharedPreferences.getInt(
                ACCOUNT_ID,
                NO_ACCOUNT_ID
                                                 )
        return accountId.takeIf { it != NO_ACCOUNT_ID }
    }

    fun clearSession() {
        sharedPreferences.edit {
            remove(LOGIN_VALIDATION_TOKEN)
            remove(LOGIN_SESSION_ID)
            remove(GUEST_SESSION_ID)
            remove(ACCOUNT_ID)
        }
    }

    fun clearStorage() {
        sharedPreferences.edit {
            clear()
        }
    }

    private companion object {
        const val PREFERENCES_NAME =
                "api_key_preferences"

        const val API_KEY =
                "tmdb_api_key"

        const val LOGIN_VALIDATION_TOKEN =
                "tmdb_login_validation_token"

        const val LOGIN_SESSION_ID =
                "tmdb_login_session_id"

        const val GUEST_SESSION_ID =
                "tmdb_guest_session_id"

        const val ACCOUNT_ID =
                "tmdb_account_id"

        const val NO_ACCOUNT_ID = -1
    }
}