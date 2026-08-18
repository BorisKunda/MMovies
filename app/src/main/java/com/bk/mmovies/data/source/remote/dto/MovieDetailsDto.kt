package com.bk.mmovies.data.source.remote.dto

import com.google.gson.annotations.SerializedName

data class MovieDetailsDto(
        val id: Int?,
        val title: String?,
        val overview: String?,
        @SerializedName("poster_path")
        val posterPath: String?,
        @SerializedName("backdrop_path")
        val backdropPath: String?,
        @SerializedName("release_date")
        val releaseDate: String?,
        val runtime: Int?,
        val genres: List<GenreDto>?,
        @SerializedName("vote_average")
        val voteAverage: Double?,
        val credits: CreditsDto?
                          )

data class GenreDto(
        val id: Int?,
        val name: String?
                    )

data class CreditsDto(
        val cast: List<CastMemberDto>?
                      )

data class CastMemberDto(
        val id: Int?,
        val name: String?,
        val character: String?,
        @SerializedName("profile_path")
        val profilePath: String?,
        val order: Int?
                         )
