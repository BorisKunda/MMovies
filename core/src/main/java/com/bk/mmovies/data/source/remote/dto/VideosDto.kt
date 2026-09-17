package com.bk.mmovies.data.source.remote.dto

data class VideosDto(
        val results: List<VideoDto>?
                     )

data class VideoDto(
        val id: String?,
        val key: String?,
        val site: String?,
        val type: String?,
        val official: Boolean?,
        val name: String?
                    )
