package com.bk.mmovies.domain.repository

import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.result.MoviesResult


interface MovieRepository {
    suspend fun getMoviesByCategory(category: MovieCategory): MoviesResult
}
