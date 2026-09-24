package com.bk.mmovies.domain.repository

import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.domain.model.result.RecommendationResult

/**
 * Framework-level only for now: nothing in :app or :tv calls this yet. Once
 * wired up, a caller should resolve every returned suggestion's `query`
 * against a real catalog search (see [RecommendationSuggestion]'s doc)
 * rather than trusting it as a final result.
 */
interface AiRecommendationRepository {
    suspend fun getRecommendations(
            favorites: List<CatalogItem>,
            maxSuggestions: Int = 10
                                   ): RecommendationResult

    fun saveSharedPrefApiKey(apiKey: String)
    fun getSharedPrefApiKey(): String?

    // True once either a runtime-saved key or the compile-time BuildConfig
    // one is usable - the same key getRecommendations() itself resolves.
    fun hasApiKey(): Boolean
}
