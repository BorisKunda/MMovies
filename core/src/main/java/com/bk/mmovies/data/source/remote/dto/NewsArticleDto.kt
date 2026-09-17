package com.bk.mmovies.data.source.remote.dto

import com.google.gson.annotations.SerializedName

data class NewsResponseDto(
        val status: String?,
        val totalResults: Int?,
        val articles: List<ArticleDto>?
                          )

data class ArticleDto(
        val source: NewsSourceDto?,
        val author: String?,
        val title: String?,
        val description: String?,
        val url: String?,
        @SerializedName("urlToImage")
        val imageUrl: String?,
        val publishedAt: String?,
        val content: String?
                     )

data class NewsSourceDto(
        val id: String?,
        val name: String?
                        )
