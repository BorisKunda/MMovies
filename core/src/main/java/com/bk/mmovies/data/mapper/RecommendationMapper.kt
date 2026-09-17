package com.bk.mmovies.data.mapper

import com.bk.mmovies.data.source.remote.dto.RecommendationSuggestionDto
import com.bk.mmovies.domain.model.CatalogMediaType
import com.bk.mmovies.domain.model.RecommendationSuggestion
import javax.inject.Inject

class RecommendationMapper @Inject constructor() {
    fun toModels(dtos: List<RecommendationSuggestionDto>): List<RecommendationSuggestion> =
            dtos.mapNotNull { dto ->
                val query = dto.query?.trim()
                if (query.isNullOrEmpty()) return@mapNotNull null
                RecommendationSuggestion(
                        query = query,
                        mediaTypeHint = dto.mediaType?.toCatalogMediaTypeOrNull(),
                        reason = dto.reason?.trim().orEmpty()
                                         )
            }

    private fun String.toCatalogMediaTypeOrNull(): CatalogMediaType? = when (lowercase()) {
        "movie" -> CatalogMediaType.MOVIE
        "tv"    -> CatalogMediaType.TV_SERIES
        else    -> null
    }
}
