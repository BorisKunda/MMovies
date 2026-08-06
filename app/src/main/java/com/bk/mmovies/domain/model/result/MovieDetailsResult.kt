package com.bk.mmovies.domain.model.result

import com.bk.mmovies.domain.model.MovieDetailsModel

sealed interface MovieDetailsResult {
    data class Success(val movieDetails: MovieDetailsModel) : MovieDetailsResult
    data class Failure(val errorMessage: String) : MovieDetailsResult
}
