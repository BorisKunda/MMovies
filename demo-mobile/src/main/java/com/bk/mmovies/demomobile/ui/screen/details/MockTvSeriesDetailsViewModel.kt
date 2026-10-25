package com.bk.mmovies.demomobile.ui.screen.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.bk.mmovies.demomobile.R
import com.bk.mmovies.domain.model.TvSeriesDetailsModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

const val MOCK_SERIES_ID_ARG = "seriesId"

data class MockTvSeriesDetailsScreenState(
        val tvSeriesDetails: TvSeriesDetailsModel?,
        val personBiographies: Map<Int, String>
                                         )

class MockTvSeriesDetailsViewModel(
        application: Application,
        savedStateHandle: SavedStateHandle
                                  ) : AndroidViewModel(application) {

    private val seriesId: Int = checkNotNull(savedStateHandle[MOCK_SERIES_ID_ARG])

    private val _screenState = MutableStateFlow(loadScreenState())
    val screenState: StateFlow<MockTvSeriesDetailsScreenState> = _screenState.asStateFlow()

    private val _messageEvent = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val messageEvent: SharedFlow<Int> = _messageEvent.asSharedFlow()

    private val _goToSeasonDetailsNavEvent = MutableSharedFlow<Pair<Int, Int>>(extraBufferCapacity = 1)
    val goToSeasonDetailsNavEvent: SharedFlow<Pair<Int, Int>> = _goToSeasonDetailsNavEvent.asSharedFlow()

    fun refreshDetails() {
        _screenState.value = loadScreenState()
    }

    fun onTrailerClicked() {
        _messageEvent.tryEmit(R.string.demo_trailer_unavailable)
    }

    fun onFavoriteClicked() {
        _messageEvent.tryEmit(R.string.demo_favorites_unavailable)
    }

    fun onSeasonClicked(seasonNumber: Int) {
        _goToSeasonDetailsNavEvent.tryEmit(seriesId to seasonNumber)
    }

    private fun loadScreenState(): MockTvSeriesDetailsScreenState {
        val resources = getApplication<Application>().resources
        return MockTvSeriesDetailsScreenState(
                tvSeriesDetails = mockTvSeriesDetails(resources).firstOrNull { it.id == seriesId },
                personBiographies = mockPeople.associate { it.id to resources.getString(it.biographyRes) }
                                             )
    }
}
