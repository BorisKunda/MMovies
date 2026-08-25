package com.bk.mmovies.data.source.remote.dto

import com.google.gson.annotations.SerializedName

data class TvSeriesListDto(
        @SerializedName("results")
        val tvSeries: List<TvSeriesDto>?
                          )
