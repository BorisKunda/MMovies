package com.bk.mmovies.ui.screen.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bk.mmovies.domain.model.MovieDetailsModel
import com.bk.mmovies.domain.model.result.MovieDetailsResult
import com.bk.mmovies.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(private val movieRepository: MovieRepository) :
        ViewModel() {

    private val _movieDetailsScreenState = MutableStateFlow<MovieDetailsScreenState>(
            MovieDetailsScreenState.Loading
                                                                                     )
    val movieDetailsScreenState: StateFlow<MovieDetailsScreenState> =
            _movieDetailsScreenState.asStateFlow()

    private var movieId: Int? = null
    private var loadMovieDetailsJob: Job? = null

    fun loadMovieDetails(movieId: Int) {
        if (this.movieId == movieId) return
        this.movieId = movieId
        fetchMovieDetails(movieId)
    }

    fun retry() {
        val movieId = movieId ?: return
        _movieDetailsScreenState.value = MovieDetailsScreenState.Loading
        fetchMovieDetails(movieId)
    }

    private fun fetchMovieDetails(movieId: Int) {
        loadMovieDetailsJob?.cancel()
        loadMovieDetailsJob = viewModelScope.launch {
            when (val result = movieRepository.getMovieDetails(movieId)) {
                is MovieDetailsResult.Success -> {
                    _movieDetailsScreenState.value = MovieDetailsScreenState.Content(
                            result.movieDetails
                                                                                     )
                }
                is MovieDetailsResult.Failure -> {
                    _movieDetailsScreenState.value = MovieDetailsScreenState.Error(
                            result.errorMessage
                                                                                  )
                }
            }
        }
    }
}

sealed interface MovieDetailsScreenState {
    object Loading : MovieDetailsScreenState
    data class Content(val movieDetails: MovieDetailsModel) : MovieDetailsScreenState
    data class Error(val errorMessage: String) : MovieDetailsScreenState
}
