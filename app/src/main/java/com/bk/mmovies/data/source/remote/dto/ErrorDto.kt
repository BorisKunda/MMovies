package com.bk.mmovies.data.source.remote.dto

import com.google.gson.annotations.SerializedName

data class ErrorDto(
        @SerializedName("status_message") val message: String?,
        @SerializedName("status_code") val statusCode: Int?
                   )
