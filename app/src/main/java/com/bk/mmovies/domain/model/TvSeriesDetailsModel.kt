package com.bk.mmovies.domain.model

data class TvSeriesDetailsModel(
        val id: Int,
        val title: String,
        val posterUrl: String,
        val backdropUrl: String,
        val firstAirDate: String,
        val seasonsLabel: String,
        val userScore: Int,
        val genres: List<String>,
        val overview: String,
        val cast: List<CastMemberModel> = emptyList(),
        val seasons: List<SeasonModel> = emptyList(),
        val isFavorite: Boolean = false
                                )
