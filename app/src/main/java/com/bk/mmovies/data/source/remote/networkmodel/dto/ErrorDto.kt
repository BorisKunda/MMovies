package com.bk.mmovies.data.source.remote.networkmodel.dto

import com.bk.mmovies.data.source.remote.ERROR_CODE_RESPONSE_FIELD
import com.bk.mmovies.data.source.remote.ERROR_MESSAGE_RESPONSE_FIELD
import com.google.gson.annotations.SerializedName

data class ErrorDto(
    @SerializedName(ERROR_MESSAGE_RESPONSE_FIELD) val message: String?,
    @SerializedName(ERROR_CODE_RESPONSE_FIELD) val statusCode: Int?
)
