package com.bk.mmovies.domain.model.result

import com.bk.mmovies.domain.model.MovieModel

sealed interface MoviesResult {
    data class Success(val movies: List<MovieModel>):MoviesResult
    data class Failure(val errorMessage: String):MoviesResult
}
