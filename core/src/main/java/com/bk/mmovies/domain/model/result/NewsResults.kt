package com.bk.mmovies.domain.model.result

import com.bk.mmovies.domain.model.NewsItem

sealed interface NewsResult {
    data class Success(val newsItems: List<NewsItem>, val page: Int = 1, val totalPages: Int = 1) : NewsResult
    // isInvalidApiKey distinguishes a rejected (e.g. HTTP 401) API key from
    // every other failure - callers use it to decide whether to discard a
    // just-entered key rather than leave a bad one saved.
    data class Failure(
            val errorMessage: String,
            val isConnectivityFailure: Boolean = false,
            val isInvalidApiKey: Boolean = false
                       ) : NewsResult
}
