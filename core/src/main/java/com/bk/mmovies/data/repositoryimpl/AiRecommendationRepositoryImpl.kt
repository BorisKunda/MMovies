package com.bk.mmovies.data.repositoryimpl

import android.content.Context
import androidx.core.content.edit
import androidx.core.os.ConfigurationCompat
import java.util.Locale
import com.bk.mmovies.core.R
import com.bk.mmovies.data.mapper.RecommendationMapper
import com.bk.mmovies.data.source.local.preferences.GeminiModelAvailabilitySharedPrefs
import com.bk.mmovies.data.source.remote.GEMINI_API_KEY
import com.bk.mmovies.data.source.remote.GEMINI_MODELS
import com.bk.mmovies.data.source.remote.NetworkManager
import com.bk.mmovies.data.source.remote.api.GeminiApi
import com.bk.mmovies.data.source.remote.dto.GeminiContentDto
import com.bk.mmovies.data.source.remote.dto.GeminiGenerateContentRequestDto
import com.bk.mmovies.data.source.remote.dto.GeminiGenerateContentResponseDto
import com.bk.mmovies.data.source.remote.dto.GeminiGenerationConfigDto
import com.bk.mmovies.data.source.remote.dto.GeminiPartDto
import com.bk.mmovies.data.source.remote.dto.RecommendationSuggestionDto
import com.bk.mmovies.data.source.remote.result.ApiCallResult
import com.bk.mmovies.data.source.remote.result.NetworkError
import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.domain.model.CatalogMediaType
import com.bk.mmovies.domain.model.result.RecommendationResult
import com.bk.mmovies.domain.repository.AiRecommendationRepository
import com.bk.mmovies.util.logDebug
import com.bk.mmovies.util.nextPacificMidnight
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.UnknownHostException
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject

class AiRecommendationRepositoryImpl @Inject constructor(
        private val api: GeminiApi,
        private val networkManager: NetworkManager,
        private val recommendationMapper: RecommendationMapper,
        private val gson: Gson,
        private val modelAvailability: GeminiModelAvailabilitySharedPrefs,
        @ApplicationContext private val context: Context
                                                        ) : AiRecommendationRepository {

    // Own, self-contained prefs file rather than piggybacking on
    // AuthCredentialsSharedPrefs - this key belongs to AI recommendations,
    // not TMDB authentication, and has nothing to do with a login session.
    private val sharedPreferences = context.getSharedPreferences(
            PREFERENCES_NAME,
            Context.MODE_PRIVATE
                                                                )

    private val failureMessage: String
        get() = context.getString(R.string.error_ai_recommendations_failed)

    override fun saveSharedPrefApiKey(apiKey: String) {
        sharedPreferences.edit {
            putString(
                    GEMINI_API_KEY_PREF,
                    apiKey.trim()
                     )
        }
    }

    override fun getSharedPrefApiKey(): String? {
        return sharedPreferences.getString(
                GEMINI_API_KEY_PREF,
                null
                                          )
    }

    override fun hasApiKey(): Boolean {
        return !getSharedPrefApiKey().isNullOrBlank() || GEMINI_API_KEY.isNotBlank()
    }

    override suspend fun getRecommendations(
            favorites: List<CatalogItem>,
            maxSuggestions: Int
                                           ): RecommendationResult {
        // Nothing to reason over yet - asking the model with no signal at
        // all would just invite it to invent generic/hallucinated titles.
        if (favorites.isEmpty()) {
            return RecommendationResult.Success(emptyList())
        }

        val request = GeminiGenerateContentRequestDto(
                contents = listOf(
                        GeminiContentDto(
                                parts = listOf(
                                        GeminiPartDto(
                                                buildPrompt(
                                                        favorites,
                                                        maxSuggestions
                                                           )
                                                     )
                                              )
                                        )
                                 ),
                generationConfig = GeminiGenerationConfigDto()
                                                     )

        val apiKey = getSharedPrefApiKey() ?: GEMINI_API_KEY

        // The models not used up today, in preference order. isAvailable also
        // forgets any daily limit recorded on an earlier Pacific day (Google
        // resets daily quotas at Pacific midnight), so a model comes back on
        // its own the first time it is asked about on a new day.
        val models = GEMINI_MODELS.filter { modelAvailability.isAvailable(it) }

        // Each model is tried once, never repeated within a load; the next one
        // is only tried after a failure that is not the caller's fault. Every
        // attempt has its own 10s cap (see GeminiNetworkModule).
        var dailyLimitFailures = 0
        var otherFailures = 0
        for (model in models) {
            when (val attempt = attemptModel(model, apiKey, request)) {
                is Attempt.Done       -> return attempt.result
                is Attempt.DailyLimit -> dailyLimitFailures++
                is Attempt.Skip       -> otherFailures++
                is Attempt.Stop       -> return attempt.failure
            }
        }

        // Nothing answered. Only when EVERY model was refused for its daily
        // quota (or there was none left to try) is it "come back after the
        // reset"; if even one failed for another reason it is "busy, retry".
        logDebug("Gemini", "no model answered: $dailyLimitFailures daily-limited, $otherFailures other failures, ${models.size} tried")
        return if (otherFailures == 0) {
            RecommendationResult.Failure(
                    context.getString(R.string.error_ai_recommendations_daily_limit, nextResetTimeText())
                                        )
        } else {
            RecommendationResult.Failure(context.getString(R.string.error_ai_recommendations_overloaded))
        }
    }

    // How one model's attempt ended, as far as the loop above cares.
    private sealed interface Attempt {
        data class Done(val result: RecommendationResult) : Attempt
        data object DailyLimit : Attempt
        data object Skip : Attempt
        data class Stop(val failure: RecommendationResult.Failure) : Attempt
    }

    private suspend fun attemptModel(
            model: String,
            apiKey: String,
            request: GeminiGenerateContentRequestDto
                                    ): Attempt {
        val apiCallResult: ApiCallResult<GeminiGenerateContentResponseDto> =
                networkManager.executeApiCall(
                        "GeminiGenerateContent",
                        apiCall = { ->
                            api.generateContent(
                                    model = model,
                                    apiKey = apiKey,
                                    request = request
                                               )
                        })

        return when (apiCallResult) {
            is ApiCallResult.Success -> {
                val parsed = parseSuggestions(apiCallResult.data)
                // An answer that can't be read counts like any other failure of
                // this model: move on to the next one.
                if (parsed is RecommendationResult.Failure) Attempt.Skip else Attempt.Done(parsed)
            }
            is ApiCallResult.Failure -> classifyFailure(model, apiCallResult.error)
        }
    }

    private fun classifyFailure(model: String, error: NetworkError): Attempt {
        return when (error) {
            is NetworkError.ExceptionError -> {
                // No way to reach Google at all: every other model would fail
                // the same way, so don't spend seven attempts finding out. A
                // timeout is different - that model was just too slow.
                if (error.cause.isNoConnection()) {
                    Attempt.Stop(
                            RecommendationResult.Failure(
                                    context.getString(R.string.error_no_network_generic),
                                    isConnectivityFailure = true
                                                        )
                                )
                } else {
                    logDebug("Gemini", "$model: ${error.cause.javaClass.simpleName} -> next model")
                    Attempt.Skip
                }
            }
            is NetworkError.HttpError      -> when {
                error.isDailyLimit()                 -> {
                    // Only this exact quota is recorded, until the next Pacific
                    // day; per-minute limits and overloads clear on their own.
                    modelAvailability.markDailyLimitReached(model)
                    logDebug("Gemini", "$model: daily limit reached -> marked, next model")
                    Attempt.DailyLimit
                }
                // Bad request / key: the same for every model.
                error.responseCode in STOP_HTTP_CODES -> Attempt.Stop(RecommendationResult.Failure(failureMessage))
                else                                 -> {
                    logDebug("Gemini", "$model: HTTP ${error.responseCode} -> next model")
                    Attempt.Skip
                }
            }
            NetworkError.EmptyBody         -> Attempt.Skip
        }
    }

    // A 429 whose body names EXACTLY the free-tier requests-per-day quota
    // (error.details[].violations[].quotaId). Looked up in the raw body because
    // the shared error parsing only understands TMDB's error shape.
    private fun NetworkError.HttpError.isDailyLimit(): Boolean {
        if (responseCode != 429) return false
        return runCatching {
            JsonParser.parseString(rawErrorBody)
                    .asJsonObject
                    .getAsJsonObject("error")
                    .getAsJsonArray("details")
                    .any { detail ->
                        detail.asJsonObject.getAsJsonArray("violations")?.any { violation ->
                            violation.asJsonObject.get("quotaId")?.asString == DAILY_LIMIT_QUOTA_ID
                        } == true
                    }
        }.getOrDefault(false)
    }

    // True for failures that mean the device cannot reach the server (DNS,
    // refused/unreachable), as opposed to a slow or dropped response.
    private fun Throwable.isNoConnection(): Boolean =
            this is UnknownHostException || this is ConnectException || this is NoRouteToHostException

    // The next Pacific midnight (when Google resets daily quotas) as a clock
    // time in the device's own zone and locale, e.g. "10:00".
    private fun nextResetTimeText(): String =
            nextPacificMidnight()
                    .withZoneSameInstant(ZoneId.systemDefault())
                    .format(
                            DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
                                    .withLocale(ConfigurationCompat.getLocales(context.resources.configuration)[0] ?: Locale.getDefault())
                           )

    private fun parseSuggestions(response: GeminiGenerateContentResponseDto): RecommendationResult {
        val responseText = response.candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.firstOrNull()
                ?.text
        if (responseText.isNullOrBlank()) {
            return RecommendationResult.Failure(failureMessage)
        }

        return try {
            val suggestionDtos: List<RecommendationSuggestionDto> = gson.fromJson(
                    responseText,
                    object : TypeToken<List<RecommendationSuggestionDto>>() {}.type
                                                                                 )
            RecommendationResult.Success(recommendationMapper.toModels(suggestionDtos))
        } catch (e: JsonSyntaxException) {
            RecommendationResult.Failure(failureMessage)
        }
    }

    // Deliberately asks for a search query + media type hint per suggestion,
    // not a trusted final result - see AiRecommendationRepository's doc.
    // Favorites are capped to keep the prompt (and token cost) small.
    private fun buildPrompt(
            favorites: List<CatalogItem>,
            maxSuggestions: Int
                           ): String {
        // releaseDateIso (not the locale-formatted releaseDate) so the model
        // always sees a plain yyyy-MM-dd: a movie's original release date, or
        // a series' first air date. Omitted when TMDB has no date for the item.
        val favoritesList = favorites.take(MAX_PROMPT_FAVORITES)
                .joinToString("\n") { item ->
                    val releaseDate = item.releaseDateIso.takeIf { it.isNotBlank() }
                            ?.let { ", ${item.mediaType.toReleaseLabel()} $it" }
                            .orEmpty()
                    "- ${item.title} (${item.mediaType.toPromptLabel()}$releaseDate)"
                }

        return """
    Recommend up to $maxSuggestions real movies or TV series matching the genres, themes, and tone of these favorites. Exclude favorites and duplicates; omit uncertain titles.

    Favorites:
    $favoritesList

    Return only a JSON array with exactly these fields per item:
    {"query":"Title","mediaType":"movie"}
    Use "movie" or "tv" for mediaType. No reasons, markdown, or extra text.
""".trimIndent()
    }

    private fun CatalogMediaType.toPromptLabel(): String = when (this) {
        CatalogMediaType.MOVIE     -> "movie"
        CatalogMediaType.TV_SERIES -> "tv series"
    }

    private fun CatalogMediaType.toReleaseLabel(): String = when (this) {
        CatalogMediaType.MOVIE     -> "released"
        CatalogMediaType.TV_SERIES -> "first aired"
    }

    private companion object {
        const val PREFERENCES_NAME = "gemini_api_key_preferences"
        const val GEMINI_API_KEY_PREF = "gemini_api_key"

        // The one quota id that means "this model's free-tier daily request
        // limit is used up" (seen in a real 429 body).
        const val DAILY_LIMIT_QUOTA_ID = "GenerateRequestsPerDayPerProjectPerModel-FreeTier"

        // Client errors that don't depend on the model (malformed request,
        // bad or unauthorized key): trying other models can't help.
        val STOP_HTTP_CODES = setOf(400, 401, 403)
        const val MAX_PROMPT_FAVORITES = 20
    }
}
