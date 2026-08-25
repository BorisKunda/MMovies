package com.bk.mmovies.domain.model

import androidx.annotation.StringRes
import com.bk.mmovies.R

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
        val subtitle: String,
        // Only meaningful for MOVIE results; used to route to details with the
        // right category so unreleased titles get the "coming soon" treatment.
        val isUpcoming: Boolean = false
                             )
