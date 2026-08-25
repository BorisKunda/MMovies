package com.bk.mmovies.data.repositoryimpl

import android.content.Context
import com.bk.mmovies.R
import com.bk.mmovies.data.mapper.SearchResultMapper
import com.bk.mmovies.data.source.local.dao.RecentSearchDao
import com.bk.mmovies.data.source.local.entity.RecentSearchEntity
import com.bk.mmovies.data.source.remote.NetworkManager
import com.bk.mmovies.data.source.remote.api.TmdbApi
import com.bk.mmovies.data.source.remote.dto.MultiSearchListDto
import com.bk.mmovies.data.source.remote.result.ApiCallResult
import com.bk.mmovies.domain.model.result.SearchResult
import com.bk.mmovies.domain.repository.SearchRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val MAX_RECENT_SEARCHES = 10

class SearchRepositoryImpl @Inject constructor(
        private val api: TmdbApi,
        private val networkManager: NetworkManager,
        private val searchResultMapper: SearchResultMapper,
        private val recentSearchDao: RecentSearchDao,
        @ApplicationContext private val context: Context
                                               ) : SearchRepository {
    private val failureMessage: String
        get() = context.getString(R.string.error_search_failed)

    override suspend fun search(query: String): SearchResult {
        val apiCallResult: ApiCallResult<MultiSearchListDto> = networkManager.executeApiCall(
                "SearchMulti",
                apiCall = { -> api.searchMulti(query) })

        return when (apiCallResult) {
            is ApiCallResult.Success<MultiSearchListDto> -> {
                SearchResult.Success(searchResultMapper.toModels(apiCallResult.data.results.orEmpty()))
            }
            is ApiCallResult.Failure                      -> {
                SearchResult.Failure(failureMessage)
            }
        }
    }

    override suspend fun getRecentSearches(): List<String> {
        return recentSearchDao.getRecentSearches(MAX_RECENT_SEARCHES)
    }

    override suspend fun addRecentSearch(query: String) {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isEmpty()) return
        recentSearchDao.insert(RecentSearchEntity(trimmedQuery, System.currentTimeMillis()))
    }

    override suspend fun removeRecentSearch(query: String) {
        recentSearchDao.deleteByQuery(query)
    }

    override suspend fun clearRecentSearches() {
        recentSearchDao.clearAll()
    }
}
