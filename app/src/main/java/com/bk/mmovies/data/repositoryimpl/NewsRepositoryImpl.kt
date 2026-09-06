package com.bk.mmovies.data.repositoryimpl

import android.content.Context
import com.bk.mmovies.R
import com.bk.mmovies.data.mapper.NewsMapper
import com.bk.mmovies.data.source.remote.NEWS_MAX_RESULTS
import com.bk.mmovies.data.source.remote.NEWS_PAGE_SIZE
import com.bk.mmovies.data.source.remote.NetworkManager
import com.bk.mmovies.data.source.remote.api.NewsApi
import com.bk.mmovies.data.source.remote.dto.NewsResponseDto
import com.bk.mmovies.data.source.remote.result.ApiCallResult
import com.bk.mmovies.data.source.remote.result.isConnectivityFailure
import com.bk.mmovies.domain.model.filterByRequiredTitleKeywords
import com.bk.mmovies.domain.model.result.NewsResult
import com.bk.mmovies.domain.repository.NewsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
        private val api: NewsApi,
        private val networkManager: NetworkManager,
        private val newsMapper: NewsMapper,
        @ApplicationContext private val context: Context
                                             ) : NewsRepository {

    private val failureMessage: String
        get() = context.getString(R.string.error_news_load_failed)

    override suspend fun getNews(page: Int): NewsResult {
        val apiCallResult: ApiCallResult<NewsResponseDto> = networkManager.executeApiCall(
                "GetNews_page$page",
                apiCall = { -> api.getTopHeadlines(page = page) })

        return toNewsResult(apiCallResult, page)
    }

    private fun toNewsResult(apiCallResult: ApiCallResult<NewsResponseDto>, page: Int): NewsResult = when (apiCallResult) {
        is ApiCallResult.Success -> {
            val totalResults = apiCallResult.data.totalResults ?: 0
            // ceil(totalResults / NEWS_PAGE_SIZE) without floating-point math.
            val rawTotalPages = ((totalResults + NEWS_PAGE_SIZE - 1) / NEWS_PAGE_SIZE).coerceAtLeast(1)
            // The free-plan cap applies regardless of what totalResults
            // claims, so never report more pages than the plan actually lets
            // us reach - otherwise loadNextPage would eventually request a
            // page NewsAPI rejects with "maximumResultsReached" instead of
            // just stopping cleanly at the real last page.
            val maxReachablePages = (NEWS_MAX_RESULTS - 1) / NEWS_PAGE_SIZE + 1
            val totalPages = rawTotalPages.coerceAtMost(maxReachablePages)
            NewsResult.Success(
                    newsItems = newsMapper.toModels(apiCallResult.data.articles.orEmpty())
                            .filterByRequiredTitleKeywords(),
                    page = page,
                    totalPages = totalPages
                              )
        }
        is ApiCallResult.Failure -> NewsResult.Failure(failureMessage, apiCallResult.error.isConnectivityFailure)
    }
}
