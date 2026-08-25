package com.bk.mmovies.domain.model

enum class SearchResultMediaType { MOVIE, TV_SERIES, PERSON }

data class SearchResultModel(
        val id: Int,
        val mediaType: SearchResultMediaType,
        val title: String,
        val imageUrl: String,
        val subtitle: String,
        val rating: Int = 0
                             )
