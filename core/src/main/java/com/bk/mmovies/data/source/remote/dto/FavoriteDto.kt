package com.bk.mmovies.data.source.remote.dto

import com.google.gson.annotations.SerializedName

data class ToggleFavoriteRequestDto(
        @SerializedName("media_type")
        val mediaType: String = "movie",
        @SerializedName("media_id")
        val mediaId: Int,
        @SerializedName("favorite")
        val favorite: Boolean
                                    )

data class ToggleFavoriteResponseDto(
        @SerializedName("status_code")
        val statusCode: Int?,
        @SerializedName("status_message")
        val statusMessage: String?
                                     )
