package com.bk.mmovies.domain.model

data class SeasonModel(
        val id: Int,
        val name: String,
        val overview: String,
        val posterUrl: String,
        val airDate: String,
        val seasonNumber: Int,
        val rating: Int = 0,
        val episodeCountLabel: String = "",
        val episodes: List<EpisodeModel> = emptyList()
                      )
