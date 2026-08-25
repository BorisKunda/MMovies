package com.bk.mmovies.data.source.remote.dto

import com.google.gson.annotations.SerializedName

data class MultiSearchListDto(
        @SerializedName("results")
        val results: List<MultiSearchResultDto>?
                              )

// One shape for TMDB's mixed movie/tv/person search response: the fields that
// only apply to one media type are simply null on the other two, and
// mediaType is the discriminator that says which ones to read.
data class MultiSearchResultDto(
        val id: Int?,
        @SerializedName("media_type")
        val mediaType: String?,
        val title: String?,
        val name: String?,
        @SerializedName("poster_path")
        val posterPath: String?,
        @SerializedName("profile_path")
        val profilePath: String?,
        @SerializedName("release_date")
        val releaseDate: String?,
        @SerializedName("first_air_date")
        val firstAirDate: String?,
        @SerializedName("vote_average")
        val rating: Double?
                                )
