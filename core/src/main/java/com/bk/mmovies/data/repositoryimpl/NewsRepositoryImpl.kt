package com.bk.mmovies.data.repositoryimpl

import android.content.Context
import androidx.core.content.edit
import com.bk.mmovies.core.R
import com.bk.mmovies.data.mapper.NewsMapper
import com.bk.mmovies.data.source.remote.NEWS_API_KEY
import com.bk.mmovies.data.source.remote.NEWS_MAX_RESULTS
import com.bk.mmovies.data.source.remote.NEWS_PAGE_SIZE
import com.bk.mmovies.data.source.remote.NetworkManager
import com.bk.mmovies.data.source.remote.api.NewsApi
import com.bk.mmovies.data.source.remote.dto.NewsResponseDto
import com.bk.mmovies.data.source.remote.result.ApiCallResult
import com.bk.mmovies.data.source.remote.result.NetworkError
import com.bk.mmovies.data.source.remote.result.isConnectivityFailure
import com.bk.mmovies.domain.model.filterByRequiredTitleKeywords
import com.bk.mmovies.domain.model.result.NewsArticleContentResult
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

    // Own, self-contained prefs file rather than piggybacking on
    // AuthCredentialsSharedPrefs - this key has nothing to do with TMDB
    // authentication.
    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val failureMessage: String
        get() = context.getString(R.string.error_news_load_failed)

    override fun saveSharedPrefApiKey(apiKey: String) {
        sharedPreferences.edit { putString(NEWS_API_KEY_PREF, apiKey.trim()) }
    }

    override fun getSharedPrefApiKey(): String? {
        return sharedPreferences.getString(NEWS_API_KEY_PREF, null)
    }

    override fun removeSharedPrefApiKey() {
        sharedPreferences.edit { remove(NEWS_API_KEY_PREF) }
    }

    override fun hasApiKey(): Boolean {
        return !getSharedPrefApiKey().isNullOrBlank() || NEWS_API_KEY.isNotBlank()
    }

    override suspend fun getNews(page: Int): NewsResult {
        val apiKey = getSharedPrefApiKey() ?: NEWS_API_KEY
        val apiCallResult: ApiCallResult<NewsResponseDto> = networkManager.executeApiCall(
                "GetNews_page$page",
                apiCall = { -> api.getTopHeadlines(page = page, apiKey = apiKey) })

        return toNewsResult(apiCallResult, page)
    }

    // NewsAPI has no per-article "full body" endpoint (see NewsMapper's
    // truncated `content` field) - :app's News feature stays WebView-only
    // and never calls this; GuardianNewsRepositoryImpl (:tv) is the only real
    // implementation.
    override suspend fun getNewsArticleContent(articleUrl: String): NewsArticleContentResult =
            NewsArticleContentResult.Failure(failureMessage)

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
        is ApiCallResult.Failure -> {
            val error = apiCallResult.error
            // NewsAPI answers a missing/invalid/disabled key with HTTP 401, so
            // that's a rejected key rather than a transient network/server issue.
            if (error is NetworkError.HttpError && error.responseCode == 401) {
                NewsResult.Failure(context.getString(R.string.toast_invalid_api_key), isInvalidApiKey = true)
            } else {
                NewsResult.Failure(failureMessage, error.isConnectivityFailure)
            }
        }
    }

    private companion object {
        const val PREFERENCES_NAME = "news_api_key_preferences"
        const val NEWS_API_KEY_PREF = "news_api_key"
    }
}
