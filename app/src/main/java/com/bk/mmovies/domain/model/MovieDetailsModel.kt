package com.bk.mmovies.domain.model

data class MovieDetailsModel(
        val id: Int,
        val title: String,
        val posterUrl: String,
        val backdropUrl: String,
        val releaseDate: String,
        val runtime: String,
        val userScore: Int,
        val genres: List<String>,
        val overview: String,
        val cast: List<CastMemberModel> = emptyList(),
        val isFavorite: Boolean = false
                            )
