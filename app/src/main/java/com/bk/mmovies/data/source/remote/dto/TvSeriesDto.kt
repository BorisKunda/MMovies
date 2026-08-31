package com.bk.mmovies.data.source.remote.dto

import com.google.gson.annotations.SerializedName

data class TvSeriesDto(
        val id: Int?,
        @SerializedName("name")
        val title: String?,
        @SerializedName("overview")
        val desc: String?,
        @SerializedName("poster_path")
        val imageUrl: String?,
        @SerializedName("first_air_date")
        val firstAirDate: String?,
        @SerializedName("vote_average")
        val rating: Double?)
