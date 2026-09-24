package com.bk.mmovies.domain.model

import androidx.annotation.StringRes
import com.bk.mmovies.core.R

enum class SearchResultMediaType(@get:StringRes val labelResId: Int) {
    MOVIE(R.string.search_result_type_movie),
    TV_SERIES(R.string.search_result_type_tv_series),
    PERSON(R.string.search_result_type_person)
}

data class SearchResultModel(
        val id: Int,
        val mediaType: SearchResultMediaType,
        val title: String,
        val imageUrl: String,
        val backdropUrl: String = "",
        val subtitle: String,
        // TMDB user score as a percentage (0 = none). Only movies and series
        // have one; persons stay 0.
        val rating: Int = 0,
        // Only meaningful for MOVIE results; used to route to details with the
        // right category so unreleased titles get the "coming soon" treatment.
        val isUpcoming: Boolean = false
                             )
