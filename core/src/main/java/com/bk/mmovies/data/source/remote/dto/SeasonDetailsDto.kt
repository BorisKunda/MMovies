package com.bk.mmovies.data.source.remote.dto

import com.google.gson.annotations.SerializedName

data class SeasonDetailsDto(
        val id: Int?,
        val name: String?,
        val overview: String?,
        @SerializedName("poster_path")
        val posterPath: String?,
        @SerializedName("air_date")
        val airDate: String?,
        @SerializedName("season_number")
        val seasonNumber: Int?,
        @SerializedName("vote_average")
        val voteAverage: Double?,
        val episodes: List<EpisodeDto>?
                           )
