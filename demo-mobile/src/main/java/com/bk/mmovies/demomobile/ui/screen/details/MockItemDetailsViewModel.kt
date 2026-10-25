package com.bk.mmovies.demomobile.ui.screen.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.bk.mmovies.demomobile.R
import com.bk.mmovies.domain.model.MovieDetailsModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

const val MOCK_MOVIE_ID_ARG = "movieId"

data class MockItemDetailsScreenState(
        val movieDetails: MovieDetailsModel?,
        val personBiographies: Map<Int, String>
                                     )

class MockItemDetailsViewModel(
        application: Application,
        savedStateHandle: SavedStateHandle
                              ) : AndroidViewModel(application) {

    private val movieId: Int = checkNotNull(savedStateHandle[MOCK_MOVIE_ID_ARG])

    private val _screenState = MutableStateFlow(loadScreenState())
    val screenState: StateFlow<MockItemDetailsScreenState> = _screenState.asStateFlow()

    private val _messageEvent = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val messageEvent: SharedFlow<Int> = _messageEvent.asSharedFlow()

    fun refreshDetails() {
        _screenState.value = loadScreenState()
    }

    fun onTrailerClicked() {
        _messageEvent.tryEmit(R.string.demo_trailer_unavailable)
    }

    fun onFavoriteClicked() {
        _messageEvent.tryEmit(R.string.demo_favorites_unavailable)
    }

    private fun loadScreenState(): MockItemDetailsScreenState {
        val resources = getApplication<Application>().resources
        return MockItemDetailsScreenState(
                movieDetails = mockCatalogItemsDetails(resources).firstOrNull { it.id == movieId },
                personBiographies = mockPeople.associate { it.id to resources.getString(it.biographyRes) }
                                         )
    }
}
