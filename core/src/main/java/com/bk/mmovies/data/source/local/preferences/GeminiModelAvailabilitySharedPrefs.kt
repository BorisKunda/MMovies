package com.bk.mmovies.data.source.local.preferences

import android.content.Context
import androidx.core.content.edit
import com.bk.mmovies.data.source.remote.GEMINI_MODELS
import com.bk.mmovies.util.currentPacificEpochDay
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tracks which Gemini models have hit their DAILY quota, one Long per model.
 *
 * Each entry is keyed by the plain model name (e.g. "gemini-3.6-flash") and
 * holds the Pacific epoch day (days since 1970-01-01, see
 * [currentPacificEpochDay]) on which that model reported its daily limit as
 * reached. The default is 0 - which can never be a real failure day - and
 * means "no limit hit, available".
 *
 * There is no timer and no reset job: [isAvailable] compares the saved day
 * with today's Pacific day when asked. Google resets daily quotas at Pacific
 * midnight, so a saved day earlier than today means the quota has reset - the
 * entry is deleted and the model is available again.
 *
 * Own prefs file, separate from the Gemini API key's, since this is per model
 * and independent of which key is configured.
 */
@Singleton
class GeminiModelAvailabilitySharedPrefs @Inject constructor(
        @ApplicationContext context: Context
                                                            ) {

    private val sharedPreferences =
            context.getSharedPreferences(
                    PREFERENCES_NAME,
                    Context.MODE_PRIVATE
                                        )

    // The Pacific epoch day the daily limit was hit on, or 0 if it hasn't been.
    fun getLimitReachedDay(model: String): Long =
            sharedPreferences.getLong(
                    model,
                    NO_LIMIT_DAY
                                     )

    // Call when a model's daily quota is reported as used up. [today] is a
    // parameter only so a test can pass a fixed day.
    fun markDailyLimitReached(model: String, today: Long = currentPacificEpochDay()) {
        sharedPreferences.edit {
            putLong(
                    model,
                    today
                   )
        }
    }

    fun isAvailable(model: String, today: Long = currentPacificEpochDay()): Boolean {
        val limitDay = getLimitReachedDay(model)
        if (limitDay == NO_LIMIT_DAY) return true
        if (limitDay == today) return false

        // An earlier day: the quota has reset since. A LATER day can only mean
        // the device clock was moved backwards - equally stale, so it must not
        // block the model for weeks. Either way, forget the entry.
        clear(model)
        return true
    }

    // Every known model with whether it can be used today, in GEMINI_MODELS order.
    fun getAll(today: Long = currentPacificEpochDay()): Map<String, Boolean> =
            GEMINI_MODELS.associateWith { isAvailable(it, today) }

    // The known models usable today, in GEMINI_MODELS order - the order to try them in.
    fun getAvailableModels(today: Long = currentPacificEpochDay()): List<String> =
            GEMINI_MODELS.filter { isAvailable(it, today) }

    // Forgets every recorded limit (all models available again).
    fun resetAll() {
        sharedPreferences.edit {
            GEMINI_MODELS.forEach { remove(it) }
        }
    }

    private fun clear(model: String) {
        sharedPreferences.edit { remove(model) }
    }

    private companion object {
        const val PREFERENCES_NAME = "gemini_model_availability_preferences"
        const val NO_LIMIT_DAY = 0L
    }
}
