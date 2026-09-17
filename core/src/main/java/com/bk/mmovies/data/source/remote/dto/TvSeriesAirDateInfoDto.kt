package com.bk.mmovies.data.source.remote.dto

import com.google.gson.annotations.SerializedName

// A lighter companion to TvSeriesDetailsDto: the catalog row only needs
// these three fields to derive its air-date label, not the full
// details+credits payload.
data class TvSeriesAirDateInfoDto(
        val status: String?,
        @SerializedName("first_air_date")
        val firstAirDate: String?,
        @SerializedName("last_air_date")
        val lastAirDate: String?
                                  )
