package com.bk.mmovies.ui.screen.details.moviedetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bk.mmovies.connectivity.InternetMonitor
import com.bk.mmovies.domain.model.MovieDetailsModel
import com.bk.mmovies.domain.model.result.MovieDetailsResult
import com.bk.mmovies.domain.model.result.ToggleFavoriteResult
import com.bk.mmovies.domain.repository.AuthenticationRepository
import com.bk.mmovies.domain.repository.MovieRepository
import com.bk.mmovies.locale.LocaleMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
        private val movieRepository: MovieRepository,
        private val authenticationRepository: AuthenticationRepository,
        localeMonitor: LocaleMonitor,
        internetMonitor: InternetMonitor
                                                ) :
        ViewModel() {

    private val _movieDetailsScreenState = MutableStateFlow<MovieDetailsScreenState>(
            MovieDetailsScreenState.Loading
                                                                                     )
    val movieDetailsScreenState: StateFlow<MovieDetailsScreenState> =
            _movieDetailsScreenState.asStateFlow()

    // Doesn't change while this screen is open, so a plain property is
    // enough — no need for a StateFlow the way CatalogViewModel needs one.
    //
    // Both ids are required: onFavoriteClicked needs the accountId too, and
    // that is only persisted once account details resolve. Keying the star on
    // the session alone rendered a control that silently did nothing whenever
    // that fetch had failed.
    val canToggleFavorite: Boolean
        get() = authenticationRepository.getSharedPrefLoginSessionId() != null &&
                authenticationRepository.getSharedPrefAccountId() != null

    private var movieId: Int? = null
    private var loadMovieDetailsJob: Job? = null

    init {
        // Reload the currently displayed movie in the new language whenever
        // the device/app language changes, including while the app is backgrounded.
        // A locale change often lands right as the OS is mid-reconnect (seen as
        // an immediate UnknownHostException), so wait for connectivity before firing.
        viewModelScope.launch {
            localeMonitor.currentLanguage
                    .drop(1)
                    .collectLatest {
                        val currentMovieId = movieId ?: return@collectLatest
                        internetMonitor.isInternetAvailable.first { it }
                        _movieDetailsScreenState.value = MovieDetailsScreenState.Loading
                        fetchMovieDetails(currentMovieId)
                    }
        }
    }

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
        val sessionId = authenticationRepository.getSharedPrefLoginSessionId()
        loadMovieDetailsJob?.cancel()
        loadMovieDetailsJob = viewModelScope.launch {
            when (val result = movieRepository.getMovieDetails(movieId, sessionId)) {
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

    fun onFavoriteClicked() {
        val currentState = _movieDetailsScreenState.value
        if (currentState !is MovieDetailsScreenState.Content) return
        val sessionId = authenticationRepository.getSharedPrefLoginSessionId() ?: return
        val accountId = authenticationRepository.getSharedPrefAccountId() ?: return
        val movieDetails = currentState.movieDetails
        val newIsFavorite = !movieDetails.isFavorite

        // Optimistic: flip the star immediately, revert only if the call fails.
        _movieDetailsScreenState.value = MovieDetailsScreenState.Content(
                movieDetails.copy(isFavorite = newIsFavorite)
                                                                         )
        viewModelScope.launch {
            val result = movieRepository.toggleFavorite(
                    accountId,
                    sessionId,
                    movieDetails.id,
                    newIsFavorite
                                                         )
            if (result is ToggleFavoriteResult.Failure) {
                _movieDetailsScreenState.value = MovieDetailsScreenState.Content(movieDetails)
            }
        }
    }
}

sealed interface MovieDetailsScreenState {
    object Loading : MovieDetailsScreenState
    data class Content(val movieDetails: MovieDetailsModel) : MovieDetailsScreenState
    data class Error(val errorMessage: String) : MovieDetailsScreenState
}
