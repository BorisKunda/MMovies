package com.bk.mmovies.data.source.remote.dto

import com.google.gson.annotations.SerializedName

data class TvSeriesDetailsDto(
        val id: Int?,
        val name: String?,
        val overview: String?,
        @SerializedName("poster_path")
        val posterPath: String?,
        @SerializedName("backdrop_path")
        val backdropPath: String?,
        @SerializedName("first_air_date")
        val firstAirDate: String?,
        @SerializedName("number_of_seasons")
        val numberOfSeasons: Int?,
        val genres: List<GenreDto>?,
        @SerializedName("vote_average")
        val voteAverage: Double?,
        val credits: CreditsDto?,
        @SerializedName("account_states")
        val accountStates: AccountStatesDto?,
        val seasons: List<SeasonSummaryDto>?
                             )

data class SeasonSummaryDto(
        val id: Int?,
        val name: String?,
        val overview: String?,
        @SerializedName("poster_path")
        val posterPath: String?,
        @SerializedName("air_date")
        val airDate: String?,
        @SerializedName("season_number")
        val seasonNumber: Int?,
        @SerializedName("episode_count")
        val episodeCount: Int?,
        @SerializedName("vote_average")
        val voteAverage: Double?
                            )
