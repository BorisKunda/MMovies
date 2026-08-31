package com.bk.mmovies.ui.screen.details.tvseriesdetails.seasondetails

import com.bk.mmovies.connectivity.InternetMonitor
import com.bk.mmovies.domain.model.SeasonModel
import com.bk.mmovies.domain.model.result.SeasonDetailsResult
import com.bk.mmovies.domain.repository.TvSeriesRepository
import com.bk.mmovies.locale.LocaleMonitor
import com.bk.mmovies.ui.screen.details.DetailsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SeasonDetailsViewModel @Inject constructor(
        private val tvRepository: TvSeriesRepository,
        localeMonitor: LocaleMonitor,
        internetMonitor: InternetMonitor
                                                 ) : DetailsViewModel<Pair<Int, Int>>(localeMonitor, internetMonitor) {

    private val _seasonDetailsScreenState = MutableStateFlow<SeasonDetailsScreenState>(
            SeasonDetailsScreenState.Loading
                                                                                        )
    val seasonDetailsScreenState: StateFlow<SeasonDetailsScreenState> =
            _seasonDetailsScreenState.asStateFlow()

    fun loadSeasonDetails(seriesId: Int, seasonNumber: Int) = load(seriesId to seasonNumber)

    override fun onReload() {
        _seasonDetailsScreenState.value = SeasonDetailsScreenState.Loading
    }

    override suspend fun fetchDetails(id: Pair<Int, Int>) {
        val (seriesId, seasonNumber) = id
        when (val result = tvRepository.getSeasonDetails(seriesId, seasonNumber)) {
            is SeasonDetailsResult.Success -> {
                _seasonDetailsScreenState.value = SeasonDetailsScreenState.Content(result.season)
            }
            is SeasonDetailsResult.Failure -> {
                _seasonDetailsScreenState.value = SeasonDetailsScreenState.Error(result.errorMessage)
            }
        }
    }
}

sealed interface SeasonDetailsScreenState {
    object Loading : SeasonDetailsScreenState
    data class Content(val season: SeasonModel) : SeasonDetailsScreenState
    data class Error(val errorMessage: String) : SeasonDetailsScreenState
}
