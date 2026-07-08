package com.bk.mmovies.data.source.remote.networkmodel.dto

import com.bk.mmovies.data.source.remote.MOVIES_RESPONSE_FIELD
import com.google.gson.annotations.SerializedName

data class MovieListDto(
    @SerializedName(MOVIES_RESPONSE_FIELD)
    val movies: List<MovieDto>?
)