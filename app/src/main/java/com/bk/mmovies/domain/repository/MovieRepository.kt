package com.bk.mmovies.domain.repository

import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.result.MovieDetailsResult
import com.bk.mmovies.domain.model.result.MoviesResult
import com.bk.mmovies.domain.model.result.ToggleFavoriteResult


interface MovieRepository {
    suspend fun getMoviesByCategory(category: MovieCategory): MoviesResult
    suspend fun getMovieDetails(movieId: Int, sessionId: String?): MovieDetailsResult
    suspend fun getFavoriteMovies(accountId: Int, sessionId: String): MoviesResult
    suspend fun syncFavoriteIds(accountId: Int, sessionId: String)
    suspend fun getCachedFavoriteIds(): Set<Int>
    suspend fun toggleFavorite(
            accountId: Int,
            sessionId: String,
            movieId: Int,
            isFavorite: Boolean
                              ): ToggleFavoriteResult
}
