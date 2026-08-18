package com.bk.mmovies.data.source.remote.dto

import com.google.gson.annotations.SerializedName


data class LoginValidationTokenDto(
        @SerializedName("success")
        val success: Boolean?,
        @SerializedName("expires_at")
        val expirationDate: String?,
        @SerializedName("request_token")
        val loginValidationToken: String?)

