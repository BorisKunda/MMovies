package com.bk.mmovies.domain.model.result

sealed interface NewsArticleContentResult {
    // imageUrl is the largest available hero image for the article, if the
    // implementation can offer one beyond whatever small thumbnail the list
    // view already had (see GuardianNewsRepositoryImpl) - blank if not.
    data class Success(val content: String, val imageUrl: String = "") : NewsArticleContentResult
    data class Failure(val errorMessage: String, val isConnectivityFailure: Boolean = false) : NewsArticleContentResult
}
