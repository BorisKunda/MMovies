package com.bk.mmovies.data.source.remote.dto

import com.google.gson.annotations.SerializedName

data class GuestSessionIdDto(
        @SerializedName("success")
        val success: Boolean?,
        @SerializedName("guest_session_id")
        val guestSessionId: String?,
        @SerializedName("expires_at")
        val expirationDate: String?
                            )