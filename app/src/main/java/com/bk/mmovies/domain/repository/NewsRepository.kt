package com.bk.mmovies.domain.repository

import com.bk.mmovies.domain.model.result.NewsResult

interface NewsRepository {
    suspend fun getNews(page: Int = 1): NewsResult
}
