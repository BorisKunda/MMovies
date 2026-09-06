package com.bk.mmovies.domain.model

data class NewsItem(
        val title: String,
        val description: String,
        val articleUrl: String,
        val imageUrl: String,
        val sourceName: String,
        val author: String = "",
        val publishedAt: String = "",
        val publishedAtIso: String = "",
        val content: String = ""
                   )
