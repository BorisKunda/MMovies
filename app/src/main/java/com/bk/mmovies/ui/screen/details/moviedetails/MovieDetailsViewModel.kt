package com.bk.mmovies.ui.screen.details.moviedetails

import androidx.lifecycle.viewModelScope
import com.bk.mmovies.connectivity.InternetMonitor
import com.bk.mmovies.domain.model.MovieDetailsModel
import com.bk.mmovies.domain.model.result.MovieDetailsResult
import com.bk.mmovies.domain.model.result.ToggleFavoriteResult
import com.bk.mmovies.domain.repository.AuthenticationRepository
import com.bk.mmovies.domain.repository.MovieRepository
import com.bk.mmovies.locale.LocaleMonitor
import com.bk.mmovies.ui.screen.details.DetailsViewModel
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
class MovieDetailsViewModel @Inject constructor(
        private val movieRepository: MovieRepository,
        private val authenticationRepository: AuthenticationRepository,
        localeMonitor: LocaleMonitor,
        internetMonitor: InternetMonitor
                                                ) : DetailsViewModel<Int>(localeMonitor, internetMonitor) {

    private val _movieDetailsScreenState = MutableStateFlow<MovieDetailsScreenState>(
            MovieDetailsScreenState.Loading
                                                                                     )
    val movieDetailsScreenState: StateFlow<MovieDetailsScreenState> =
            _movieDetailsScreenState.asStateFlow()

    /** One-shot user-facing messages (favorite toggle failures). */
    private val _messageEvent: MutableSharedFlow<String> =
            MutableSharedFlow(extraBufferCapacity = 1)
    val messageEvent: SharedFlow<String> = _messageEvent.asSharedFlow()

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

    fun loadMovieDetails(movieId: Int) = load(movieId)

    override fun onReload() {
        _movieDetailsScreenState.value = MovieDetailsScreenState.Loading
    }

    override suspend fun fetchDetails(id: Int) {
        val sessionId = authenticationRepository.getSharedPrefLoginSessionId()
        when (val result = movieRepository.getMovieDetails(id, sessionId)) {
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
            revertFavoriteOnFailure(
                    content = movieDetails,
                    previousIsFavorite = movieDetails.isFavorite,
                    contentId = { it.id },
                    withFavorite = { details, isFavorite -> details.copy(isFavorite = isFavorite) },
                    currentContent = {
                        (_movieDetailsScreenState.value as? MovieDetailsScreenState.Content)?.movieDetails
                    },
                    applyContent = { _movieDetailsScreenState.value = MovieDetailsScreenState.Content(it) },
                    onFailureMessage = { _messageEvent.tryEmit(it) },
                    result = result
                                    )
        }
    }
}

sealed interface MovieDetailsScreenState {
    object Loading : MovieDetailsScreenState
    data class Content(val movieDetails: MovieDetailsModel) : MovieDetailsScreenState
    data class Error(val errorMessage: String) : MovieDetailsScreenState
}
