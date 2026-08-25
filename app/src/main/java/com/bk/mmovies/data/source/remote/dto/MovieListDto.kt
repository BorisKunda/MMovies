package com.bk.mmovies.data.source.remote.dto

import com.google.gson.annotations.SerializedName

data class MovieListDto(
        @SerializedName("results")
        val movies: List<MovieDto>?,
        @SerializedName("total_pages")
        val totalPages: Int?
                       )