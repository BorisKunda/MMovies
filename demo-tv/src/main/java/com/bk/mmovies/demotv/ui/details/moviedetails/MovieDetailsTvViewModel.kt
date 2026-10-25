package com.bk.mmovies.demotv.ui.details.moviedetails

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.bk.mmovies.demotv.R
import com.bk.mmovies.demotv.mock.mockCatalogItemsDetails
import com.bk.mmovies.domain.model.MovieDetailsModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class MovieDetailsTvViewModel(application: Application) : AndroidViewModel(application) {

    private val _screenState = MutableStateFlow<MovieDetailsTvScreenState>(MovieDetailsTvScreenState.Loading)
    val screenState: StateFlow<MovieDetailsTvScreenState> = _screenState.asStateFlow()

    private val _messageEvent: MutableSharedFlow<String> = MutableSharedFlow(extraBufferCapacity = 1)
    val messageEvent: SharedFlow<String> = _messageEvent.asSharedFlow()

    private var movieId: Int? = null

    fun loadMovieDetails(movieId: Int) {
        this.movieId = movieId
        val details = mockCatalogItemsDetails(getApplication<Application>().resources).firstOrNull { it.id == movieId }
        _screenState.value = if (details != null) {
            MovieDetailsTvScreenState.Content(details)
        } else {
            MovieDetailsTvScreenState.Error(getApplication<Application>().getString(R.string.demo_details_unavailable))
        }
    }

    fun retry() {
        movieId?.let(::loadMovieDetails)
    }

    fun onFavoriteClicked() {
        _messageEvent.tryEmit(getApplication<Application>().getString(R.string.demo_favorites_unavailable))
    }

    fun onTrailerClicked() {
        _messageEvent.tryEmit(getApplication<Application>().getString(R.string.demo_trailer_unavailable))
    }
}

sealed interface MovieDetailsTvScreenState {
    object Loading : MovieDetailsTvScreenState
    data class Content(val movieDetails: MovieDetailsModel) : MovieDetailsTvScreenState
    data class Error(val errorMessage: String) : MovieDetailsTvScreenState
}
