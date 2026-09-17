package com.bk.mmovies.data.source.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginWithCredentialsRequestDto(
        @SerializedName("username")
        val username: String,
        @SerializedName("password")
        val password: String,
        @SerializedName("request_token")
        val loginValidationToken: String)
