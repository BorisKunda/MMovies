package com.bk.mmovies.data.source.remote.dto

import com.google.gson.annotations.SerializedName

data class AccountDetailsDto(
        @SerializedName("username")
        val username: String?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("avatar")
        val avatar: AvatarDto?)

data class AvatarDto(
        @SerializedName("tmdb")
        val tmdb: TmdbAvatarDto?)

data class TmdbAvatarDto(
        @SerializedName("avatar_path")
        val avatarPath: String?)
