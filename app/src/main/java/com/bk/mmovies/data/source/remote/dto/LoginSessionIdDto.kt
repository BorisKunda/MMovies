package com.bk.mmovies.data.source.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginSessionIdDto(
        @SerializedName("success")
        val success: Boolean?,
        @SerializedName("session_id")
        val sessionId: String?)

