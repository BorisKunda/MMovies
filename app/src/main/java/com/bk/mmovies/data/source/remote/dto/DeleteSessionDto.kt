package com.bk.mmovies.data.source.remote.dto

import com.google.gson.annotations.SerializedName

data class DeleteSessionDto(
        @SerializedName("success")
        val success: Boolean?
                            )
