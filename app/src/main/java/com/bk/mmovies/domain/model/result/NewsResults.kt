package com.bk.mmovies.domain.model.result

import com.bk.mmovies.domain.model.NewsItem

sealed interface NewsResult {
    data class Success(val newsItems: List<NewsItem>, val page: Int = 1, val totalPages: Int = 1) : NewsResult
    data class Failure(val errorMessage: String, val isConnectivityFailure: Boolean = false) : NewsResult
}
