package com.bk.mmovies.data.source.remote.dto

import com.google.gson.annotations.SerializedName

data class PersonDetailsDto(
        val id: Int?,
        val name: String?,
        val biography: String?,
        val birthday: String?,
        val deathday: String?,
        @SerializedName("place_of_birth")
        val placeOfBirth: String?,
        @SerializedName("profile_path")
        val profilePath: String?
                            )
