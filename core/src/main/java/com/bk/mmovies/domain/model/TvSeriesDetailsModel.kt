package com.bk.mmovies.domain.model

data class TvSeriesDetailsModel(
        val id: Int,
        val title: String,
        val posterUrl: String,
        val backdropUrl: String,
        val airDateLabel: SeriesAirDateLabel,
        val seasonsLabel: String,
        val userScore: Int,
        val genres: List<String>,
        val overview: String,
        val cast: List<CastMemberModel> = emptyList(),
        // TMDB models a TV show as having (possibly multiple) creators at
        // the series level; director/writer credits are per-episode instead
        // (see EpisodeModel).
        val creators: List<CastMemberModel> = emptyList(),
        val seasons: List<SeasonModel> = emptyList(),
        val isFavorite: Boolean = false,
        val trailerUrl: String? = null
                                )
