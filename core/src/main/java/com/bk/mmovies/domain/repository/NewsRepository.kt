package com.bk.mmovies.domain.repository

import com.bk.mmovies.domain.model.result.NewsArticleContentResult
import com.bk.mmovies.domain.model.result.NewsResult

interface NewsRepository {
    suspend fun getNews(page: Int = 1): NewsResult
    fun saveSharedPrefApiKey(apiKey: String)
    fun getSharedPrefApiKey(): String?
    fun removeSharedPrefApiKey()

    // True once either a runtime-saved key or the compile-time BuildConfig
    // one is usable - the same key getNews() itself resolves.
    fun hasApiKey(): Boolean

    // The full article body, fetched separately from getNews() since it's
    // only needed once a user actually opens one article, not for every item
    // in a whole page of results. NewsAPI (NewsRepositoryImpl, :app) has no
    // per-article endpoint for this - only GuardianNewsRepositoryImpl (:tv)
    // has a real implementation; :app's News feature never calls this.
    suspend fun getNewsArticleContent(articleUrl: String): NewsArticleContentResult
}
