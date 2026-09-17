package com.bk.mmovies.tv.ui.details.moviedetails

import androidx.lifecycle.viewModelScope
import com.bk.mmovies.domain.model.MovieDetailsModel
import com.bk.mmovies.domain.model.result.MovieDetailsResult
import com.bk.mmovies.domain.repository.AuthenticationRepository
import com.bk.mmovies.domain.repository.MovieRepository
import com.bk.mmovies.locale.LocaleMonitor
import com.bk.mmovies.tv.ui.details.DetailsTvViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsTvViewModel @Inject constructor(
        private val movieRepository: MovieRepository,
        private val authenticationRepository: AuthenticationRepository,
        localeMonitor: LocaleMonitor
                                                   ) : DetailsTvViewModel<Int>(localeMonitor) {

    private val _screenState = MutableStateFlow<MovieDetailsTvScreenState>(
            MovieDetailsTvScreenState.Loading
                                                                           )
    val screenState: StateFlow<MovieDetailsTvScreenState> = _screenState.asStateFlow()

    private val _messageEvent: MutableSharedFlow<String> = MutableSharedFlow(extraBufferCapacity = 1)
    val messageEvent: SharedFlow<String> = _messageEvent.asSharedFlow()

    val canToggleFavorite: Boolean
        get() = authenticationRepository.getSharedPrefLoginSessionId() != null &&
                authenticationRepository.getSharedPrefAccountId() != null

    fun loadMovieDetails(movieId: Int) = load(movieId)

    override fun onReload() {
        _screenState.value = MovieDetailsTvScreenState.Loading
    }

    override suspend fun fetchDetails(id: Int) {
        val sessionId = authenticationRepository.getSharedPrefLoginSessionId()
        when (val result = movieRepository.getMovieDetails(id, sessionId)) {
            is MovieDetailsResult.Success -> {
                _screenState.value = MovieDetailsTvScreenState.Content(result.movieDetails)
            }
            is MovieDetailsResult.Failure -> {
                _screenState.value = MovieDetailsTvScreenState.Error(result.errorMessage)
            }
        }
    }

    fun onFavoriteClicked() {
        val currentState = _screenState.value
        if (currentState !is MovieDetailsTvScreenState.Content) return
        val sessionId = authenticationRepository.getSharedPrefLoginSessionId() ?: return
        val accountId = authenticationRepository.getSharedPrefAccountId() ?: return
        val movieDetails = currentState.movieDetails
        val newIsFavorite = !movieDetails.isFavorite

        _screenState.value = MovieDetailsTvScreenState.Content(movieDetails.copy(isFavorite = newIsFavorite))
        viewModelScope.launch {
            val result = movieRepository.toggleFavorite(accountId, sessionId, movieDetails.id, newIsFavorite)
            revertFavoriteOnFailure(
                    content = movieDetails,
                    previousIsFavorite = movieDetails.isFavorite,
                    contentId = { it.id },
                    withFavorite = { details, isFavorite -> details.copy(isFavorite = isFavorite) },
                    currentContent = {
                        (_screenState.value as? MovieDetailsTvScreenState.Content)?.movieDetails
                    },
                    applyContent = { _screenState.value = MovieDetailsTvScreenState.Content(it) },
                    onFailureMessage = { _messageEvent.tryEmit(it) },
                    result = result
                                    )
        }
    }
}

sealed interface MovieDetailsTvScreenState {
    object Loading : MovieDetailsTvScreenState
    data class Content(val movieDetails: MovieDetailsModel) : MovieDetailsTvScreenState
    data class Error(val errorMessage: String) : MovieDetailsTvScreenState
}
