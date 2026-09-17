package com.bk.mmovies.data.repositoryimpl

import android.content.Context
import androidx.core.content.edit
import com.bk.mmovies.core.R
import com.bk.mmovies.data.mapper.RecommendationMapper
import com.bk.mmovies.data.source.remote.GEMINI_API_KEY
import com.bk.mmovies.data.source.remote.NetworkManager
import com.bk.mmovies.data.source.remote.api.GeminiApi
import com.bk.mmovies.data.source.remote.dto.GeminiContentDto
import com.bk.mmovies.data.source.remote.dto.GeminiGenerateContentRequestDto
import com.bk.mmovies.data.source.remote.dto.GeminiGenerateContentResponseDto
import com.bk.mmovies.data.source.remote.dto.GeminiGenerationConfigDto
import com.bk.mmovies.data.source.remote.dto.GeminiPartDto
import com.bk.mmovies.data.source.remote.dto.RecommendationSuggestionDto
import com.bk.mmovies.data.source.remote.result.ApiCallResult
import com.bk.mmovies.data.source.remote.result.isConnectivityFailure
import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.domain.model.CatalogMediaType
import com.bk.mmovies.domain.model.result.RecommendationResult
import com.bk.mmovies.domain.repository.AiRecommendationRepository
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AiRecommendationRepositoryImpl @Inject constructor(
        private val api: GeminiApi,
        private val networkManager: NetworkManager,
        private val recommendationMapper: RecommendationMapper,
        private val gson: Gson,
        @ApplicationContext private val context: Context
                                                          ) : AiRecommendationRepository {

    // Own, self-contained prefs file rather than piggybacking on
    // AuthCredentialsSharedPrefs - this key belongs to AI recommendations,
    // not TMDB authentication, and has nothing to do with a login session.
    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val failureMessage: String
        get() = context.getString(R.string.error_ai_recommendations_failed)

    override fun saveSharedPrefApiKey(apiKey: String) {
        sharedPreferences.edit { putString(GEMINI_API_KEY_PREF, apiKey.trim()) }
    }

    override fun getSharedPrefApiKey(): String? {
        return sharedPreferences.getString(GEMINI_API_KEY_PREF, null)
    }

    override fun hasApiKey(): Boolean {
        return !getSharedPrefApiKey().isNullOrBlank() || GEMINI_API_KEY.isNotBlank()
    }

    override suspend fun getRecommendations(
            favorites: List<CatalogItem>,
            recentSearches: List<String>,
            maxSuggestions: Int
                                            ): RecommendationResult {
        // Nothing to reason over yet - asking the model with no signal at
        // all would just invite it to invent generic/hallucinated titles.
        if (favorites.isEmpty() && recentSearches.isEmpty()) {
            return RecommendationResult.Success(emptyList())
        }

        val request = GeminiGenerateContentRequestDto(
                contents = listOf(GeminiContentDto(parts = listOf(GeminiPartDto(buildPrompt(favorites, recentSearches, maxSuggestions))))),
                generationConfig = GeminiGenerationConfigDto()
                                                       )

        val apiKey = getSharedPrefApiKey() ?: GEMINI_API_KEY
        val apiCallResult: ApiCallResult<GeminiGenerateContentResponseDto> = networkManager.executeApiCall(
                "GeminiGenerateContent",
                apiCall = { -> api.generateContent(apiKey = apiKey, request = request) })

        return when (apiCallResult) {
            is ApiCallResult.Success -> parseSuggestions(apiCallResult.data)
            is ApiCallResult.Failure -> RecommendationResult.Failure(failureMessage, apiCallResult.error.isConnectivityFailure)
        }
    }

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
    // Favorites/searches are capped to keep the prompt (and token cost)
    // small; the most recent signal is the most relevant one anyway.
    private fun buildPrompt(favorites: List<CatalogItem>, recentSearches: List<String>, maxSuggestions: Int): String {
        val favoritesList = favorites.take(MAX_PROMPT_FAVORITES).joinToString("\n") { item ->
            "- ${item.title} (${item.mediaType.toPromptLabel()})"
        }
        val searchesList = recentSearches.take(MAX_PROMPT_SEARCHES).joinToString("\n") { "- $it" }

        return """
            You are a movie and TV series recommendation assistant for a TMDB-backed catalog app.
            Based on the user's favorites and recent searches below, suggest up to $maxSuggestions
            real, existing movie or TV series titles they have not already favorited that they would
            likely enjoy. Never invent a title - only suggest titles you are confident actually exist.

            ${if (favoritesList.isNotEmpty()) "Favorites:\n$favoritesList" else ""}
            ${if (searchesList.isNotEmpty()) "Recent searches:\n$searchesList" else ""}

            Respond with ONLY a JSON array (no markdown, no commentary) where each element has this
            exact shape: {"query": "<title to search for>", "mediaType": "movie" | "tv", "reason": "<one short sentence>"}.
        """.trimIndent()
    }

    private fun CatalogMediaType.toPromptLabel(): String = when (this) {
        CatalogMediaType.MOVIE      -> "movie"
        CatalogMediaType.TV_SERIES  -> "tv series"
    }

    private companion object {
        const val PREFERENCES_NAME = "gemini_api_key_preferences"
        const val GEMINI_API_KEY_PREF = "gemini_api_key"
        const val MAX_PROMPT_FAVORITES = 20
        const val MAX_PROMPT_SEARCHES = 10
    }
}
