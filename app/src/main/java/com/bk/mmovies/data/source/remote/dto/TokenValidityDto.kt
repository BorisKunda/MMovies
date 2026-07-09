package com.bk.mmovies.data.source.remote.dto

import com.bk.mmovies.data.source.remote.TOKEN_VALIDITY_RESPONSE_FIELD
import com.google.gson.annotations.SerializedName

data class TokenValidityDto(
    @SerializedName(TOKEN_VALIDITY_RESPONSE_FIELD)
    val isTokenValid: Boolean?
)