package com.bk.mmovies.domain.model.result

import com.bk.mmovies.domain.model.RecommendationSuggestion

sealed interface RecommendationResult {
    data class Success(val suggestions: List<RecommendationSuggestion>) : RecommendationResult
    data class Failure(val errorMessage: String, val isConnectivityFailure: Boolean = false) : RecommendationResult
}
