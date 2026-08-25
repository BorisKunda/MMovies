package com.bk.mmovies.domain.repository

import com.bk.mmovies.domain.model.result.SearchResult

interface SearchRepository {
    suspend fun search(query: String): SearchResult
    suspend fun getRecentSearches(): List<String>
    suspend fun addRecentSearch(query: String)
    suspend fun removeRecentSearch(query: String)
    suspend fun clearRecentSearches()
}
