package com.bk.mmovies.domain.model

data class EpisodeModel(
        val id: Int,
        val name: String,
        val overview: String,
        val airDate: String,
        val episodeNumber: Int,
        val seasonNumber: Int,
        val stillUrl: String,
        val rating: Int = 0,
        val runtime: String
                       )
