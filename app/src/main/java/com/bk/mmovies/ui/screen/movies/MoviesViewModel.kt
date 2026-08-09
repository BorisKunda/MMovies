package com.bk.mmovies.ui.screen.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bk.mmovies.connectivity.InternetMonitor
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.MovieModel
import com.bk.mmovies.domain.model.result.MoviesResult
import com.bk.mmovies.domain.repository.MovieRepository
import com.bk.mmovies.locale.LocaleMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
        private val movieRepository: MovieRepository,
        localeMonitor: LocaleMonitor,
        internetMonitor: InternetMonitor
                                         ) :
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

    private val _goToMovieDetailsWithIdNavEvent: MutableSharedFlow<Int> =
            MutableSharedFlow(extraBufferCapacity = 1)
    val goToMovieDetailsWithIdNavEvent: SharedFlow<Int> =
            _goToMovieDetailsWithIdNavEvent.asSharedFlow()

    private var loadMoviesJob: Job? = null

    init {
        startLoad(_selectedCategory.value)

        // Reload the current category in the new language whenever the
        // device/app language changes, including while the app is backgrounded.
        // A locale change often lands right as the OS is mid-reconnect (seen as
        // an immediate UnknownHostException), so wait for connectivity before firing.
        viewModelScope.launch {
            localeMonitor.currentLanguage
                    .drop(1)
                    .collectLatest {
                        internetMonitor.isInternetAvailable.first { it }
                        startLoad(_selectedCategory.value)
                    }
        }
    }

    fun handleCategorySelected(category: MovieCategory) {
        if (category == _selectedCategory.value) return
        _selectedCategory.value = category
        startLoad(category)
    }

    /**
     * Re-runs the current category. Without this the error screen's "Try again"
     * button had nothing to call and the only way out of a failure was to
     * switch category and switch back.
     */
    fun retry() {
        startLoad(_selectedCategory.value)
    }

    private fun startLoad(category: MovieCategory) {
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
                // "Nothing here" and "the load failed" are different things and
                // deserve different screens.
                _moviesScreenState.value = if (result.movies.isEmpty()) {
                    MoviesScreenState.Empty
                } else {
                    MoviesScreenState.Content(result.movies)
                }
            }
            is MoviesResult.Failure -> {
                _moviesScreenState.value = MoviesScreenState.Error(
                        result.errorMessage
                                                                  )
            }
        }
    }

    fun handleMovieClick(movieId: Int) {
        _goToMovieDetailsWithIdNavEvent.tryEmit(movieId)
    }

}

sealed interface MoviesScreenState {
    object Loading : MoviesScreenState
    object Empty : MoviesScreenState
    data class Content(val movies: List<MovieModel>) : MoviesScreenState
    data class Error(val errorMessage: String) : MoviesScreenState
}


