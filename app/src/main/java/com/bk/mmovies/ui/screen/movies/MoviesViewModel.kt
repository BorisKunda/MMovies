package com.bk.mmovies.ui.screen.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.MovieModel
import com.bk.mmovies.domain.model.result.MoviesResult
import com.bk.mmovies.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(private val movieRepository: MovieRepository) :
        ViewModel() {

    private val _moviesScreenState = MutableStateFlow<MoviesScreenState>(
            MoviesScreenState.Loading
                                                                        )
    val moviesScreenState: StateFlow<MoviesScreenState> =
            _moviesScreenState.asStateFlow()

    private val _selectedCategory = MutableStateFlow<MovieCategory>(
            MovieCategory.PopularMovieCategory
                                                                    )
    val selectedCategory: StateFlow<MovieCategory> = _selectedCategory.asStateFlow()

    private var loadMoviesJob: Job? = null

    init {
        loadMoviesJob = viewModelScope.launch {
            loadMovies(_selectedCategory.value)
        }
    }

    fun handleCategorySelected(category: MovieCategory) {
        if (category == _selectedCategory.value) return
        _selectedCategory.value = category
        _moviesScreenState.value = MoviesScreenState.Loading
        loadMoviesJob?.cancel()
        loadMoviesJob = viewModelScope.launch {
            loadMovies(category)
        }
    }

    private suspend fun loadMovies(category: MovieCategory) {
        val result = movieRepository.getMoviesByCategory(category)
        when (result) {
            is MoviesResult.Success -> {
                _moviesScreenState.value = MoviesScreenState.Content(
                        result.movies
                                                                    )
            }
            is MoviesResult.Failure -> {
                _moviesScreenState.value = MoviesScreenState.Error(
                        result.errorMessage
                                                                  )
            }
        }
    }

    fun handleMovieClick(movieId: Int) {

    }

}

sealed interface MoviesScreenState {
    object Loading : MoviesScreenState
    data class Content(val movies: List<MovieModel>) : MoviesScreenState
    data class Error(val errorMessage: String) : MoviesScreenState
}


