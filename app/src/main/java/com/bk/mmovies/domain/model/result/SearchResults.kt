package com.bk.mmovies.domain.model.result

import com.bk.mmovies.domain.model.SearchResultModel

sealed interface SearchResult {
    data class Success(val results: List<SearchResultModel>) : SearchResult
    data class Failure(val errorMessage: String) : SearchResult
}
