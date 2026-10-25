package com.bk.mmovies.demomobile.ui.screen.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.bk.mmovies.domain.model.SeasonModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

const val MOCK_SEASON_NUMBER_ARG = "seasonNumber"

data class MockSeasonDetailsScreenState(
        val season: SeasonModel?,
        val personBiographies: Map<Int, String>
                                       )

class MockSeasonDetailsViewModel(
        application: Application,
        savedStateHandle: SavedStateHandle
                                ) : AndroidViewModel(application) {

    private val seriesId: Int = checkNotNull(savedStateHandle[MOCK_SERIES_ID_ARG])
    private val seasonNumber: Int = checkNotNull(savedStateHandle[MOCK_SEASON_NUMBER_ARG])

    private val _screenState = MutableStateFlow(loadScreenState())
    val screenState: StateFlow<MockSeasonDetailsScreenState> = _screenState.asStateFlow()

    fun refreshDetails() {
        _screenState.value = loadScreenState()
    }

    private fun loadScreenState(): MockSeasonDetailsScreenState {
        val resources = getApplication<Application>().resources
        return MockSeasonDetailsScreenState(
                season = mockTvSeriesDetails(resources)
                        .firstOrNull { it.id == seriesId }
                        ?.seasons
                        ?.firstOrNull { it.seasonNumber == seasonNumber },
                personBiographies = mockPeople.associate { it.id to resources.getString(it.biographyRes) }
                                           )
    }
}
