package com.bk.mmovies.model.result

import com.bk.mmovies.model.MovieModel

sealed interface MoviesResult {
    data class Success(val movies: List<MovieModel>):MoviesResult
    data class Failure(val errorMessage: String):MoviesResult
}
