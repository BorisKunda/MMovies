package com.bk.mmovies.data.source.local.preferences

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyStorage @Inject constructor(
        @ApplicationContext context: Context
                                       ) {
    private val sharedPreferences =
            context.getSharedPreferences(
                    PREFERENCES_NAME,
                    Context.MODE_PRIVATE
                                        )

    fun saveApiKey(apiKey: String) {
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

    fun getApiKey(): String? {
        return sharedPreferences.getString(
                API_KEY,
                null
                                          )
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
    }
}