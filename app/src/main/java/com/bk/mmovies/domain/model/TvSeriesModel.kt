package com.bk.mmovies.domain.model

data class TvSeriesModel(
        val id: Int,
        val title: String,
        val desc: String,
        val imageUrl: String,
        val firstAirDate: String,
        val rating: Int = 0,
        val isFavorite: Boolean = false
                        )
