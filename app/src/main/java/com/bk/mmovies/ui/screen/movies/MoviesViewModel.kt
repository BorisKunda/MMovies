package com.bk.mmovies.ui.screen.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bk.mmovies.connectivity.InternetMonitor
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.MovieModel
import com.bk.mmovies.domain.model.result.AccountDetailsResult
import com.bk.mmovies.domain.model.result.MoviesResult
import com.bk.mmovies.domain.model.result.ToggleFavoriteResult
import com.bk.mmovies.domain.repository.AuthenticationRepository
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
        private val authenticationRepository: AuthenticationRepository,
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

    private val _userProfileState = MutableStateFlow(UserProfileUiState())
    val userProfileState: StateFlow<UserProfileUiState> = _userProfileState.asStateFlow()

    private val _goToMovieDetailsNavEvent: MutableSharedFlow<Pair<Int, MovieCategory>> =
            MutableSharedFlow(extraBufferCapacity = 1)
    val goToMovieDetailsNavEvent: SharedFlow<Pair<Int, MovieCategory>> =
            _goToMovieDetailsNavEvent.asSharedFlow()

    private val _goToAuthNavEvent: MutableSharedFlow<Unit> =
            MutableSharedFlow(extraBufferCapacity = 1)
    val goToAuthNavEvent: SharedFlow<Unit> = _goToAuthNavEvent.asSharedFlow()

    private var loadMoviesJob: Job? = null

    init {
        startLoad(_selectedCategory.value)
        loadUserProfile()

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

    private fun loadUserProfile() {
        val loginSessionId = authenticationRepository.getSharedPrefLoginSessionId()
        if (loginSessionId == null) {
            _userProfileState.value = UserProfileUiState(isGuest = true)
            return
        }
        viewModelScope.launch {
            when (val result = authenticationRepository.getAccountDetailsResult(loginSessionId)) {
                is AccountDetailsResult.Success -> {
                    // Cached here (rather than re-fetched) so any screen that
                    // needs it for a favorite call can read it synchronously.
                    authenticationRepository.saveSharedPrefAccountId(result.accountId)
                    _userProfileState.value = UserProfileUiState(
                            name = result.name,
                            imageUrl = result.avatarUrl,
                            isGuest = false
                                                                 )
                    // Refreshes the local favorite-id cache the repository
                    // cross-references when building category lists, then
                    // reloads the current category so it picks up correct stars.
                    movieRepository.syncFavoriteIds(result.accountId, loginSessionId)
                    startLoad(_selectedCategory.value)
                }
                is AccountDetailsResult.Failure -> {
                    _userProfileState.value = UserProfileUiState(isGuest = false)
                }
            }
        }
    }

    fun onLogoutClicked() {
        viewModelScope.launch {
            authenticationRepository.logout()
            _goToAuthNavEvent.tryEmit(Unit)
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
        if (category == MovieCategory.FavoritesMovieCategory) {
            loadFavorites()
            return
        }
        val result = movieRepository.getMoviesByCategory(category)
        applyMoviesResult(result)
    }

    private suspend fun loadFavorites() {
        val sessionId = authenticationRepository.getSharedPrefLoginSessionId()
        val accountId = authenticationRepository.getSharedPrefAccountId()
        // Guests have no login session, and TMDB's favorite endpoints require
        // one — never call out, just prompt for login instead.
        if (sessionId == null || accountId == null) {
            _moviesScreenState.value = MoviesScreenState.FavoritesLoginRequired
            return
        }
        val result = movieRepository.getFavoriteMovies(accountId, sessionId)
        applyMoviesResult(result)
    }

    private fun applyMoviesResult(result: MoviesResult) {
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
        _goToMovieDetailsNavEvent.tryEmit(movieId to _selectedCategory.value)
    }

    fun onFavoriteClicked(movie: MovieModel) {
        val sessionId = authenticationRepository.getSharedPrefLoginSessionId() ?: return
        val accountId = authenticationRepository.getSharedPrefAccountId() ?: return
        val newIsFavorite = !movie.isFavorite

        // Optimistic: flip the star immediately, revert only if the call fails.
        // The repository persists the toggle to the local favorite-id cache too.
        updateMovieFavoriteState(movie.id, newIsFavorite)
        viewModelScope.launch {
            val result = movieRepository.toggleFavorite(accountId, sessionId, movie.id, newIsFavorite)
            if (result is ToggleFavoriteResult.Failure) {
                updateMovieFavoriteState(movie.id, movie.isFavorite)
            }
        }
    }

    /**
     * Re-syncs the displayed list's favorite stars against the local cache.
     * Toggling favorite from the Details screen updates the same Room cache
     * but not this ViewModel's in-memory state, so without this the Movies
     * screen kept showing stale stars after navigating back from Details.
     */
    fun refreshFavoriteMarkers() {
        val currentState = _moviesScreenState.value
        if (currentState !is MoviesScreenState.Content) return
        viewModelScope.launch {
            val favoriteIds = movieRepository.getCachedFavoriteIds()
            val movies = if (_selectedCategory.value == MovieCategory.FavoritesMovieCategory) {
                // A movie unfavorited elsewhere no longer belongs in this list at all.
                currentState.movies.filter { favoriteIds.contains(it.id) }
            } else {
                currentState.movies.map { it.copy(isFavorite = favoriteIds.contains(it.id)) }
            }
            _moviesScreenState.value = if (movies.isEmpty()) {
                MoviesScreenState.Empty
            } else {
                MoviesScreenState.Content(movies)
            }
        }
    }

    private fun updateMovieFavoriteState(movieId: Int, isFavorite: Boolean) {
        val currentState = _moviesScreenState.value
        if (currentState is MoviesScreenState.Content) {
            _moviesScreenState.value = MoviesScreenState.Content(
                    currentState.movies.map { movie ->
                        if (movie.id == movieId) movie.copy(isFavorite = isFavorite) else movie
                    }
                                                                 )
        }
    }

}

sealed interface MoviesScreenState {
    object Loading : MoviesScreenState
    object Empty : MoviesScreenState
    // Favorites tab selected by a guest: no API call is made, this is a
    // distinct state from Empty so the UI can prompt for login instead.
    object FavoritesLoginRequired : MoviesScreenState
    data class Content(val movies: List<MovieModel>) : MoviesScreenState
    data class Error(val errorMessage: String) : MoviesScreenState
}

data class UserProfileUiState(
        val name: String = "",
        val imageUrl: String = "",
        val isGuest: Boolean = true
                              )

