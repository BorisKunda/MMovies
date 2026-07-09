package com.bk.mmovies.data.source.remote.dto

import com.google.gson.annotations.SerializedName

data class MovieDto(
    val id: Int?,
    val title: String?,
    @SerializedName("overview")
    val desc: String?,
    @SerializedName("poster_path")
    val imageUrl: String?,
    @SerializedName("release_date")
    val releaseDate: String?,
    @SerializedName("backdrop_path")
    val backdropImageUrl: String?
)


