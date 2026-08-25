package com.bk.mmovies.ui.screen.seasondetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bk.mmovies.connectivity.InternetMonitor
import com.bk.mmovies.domain.model.SeasonModel
import com.bk.mmovies.domain.model.result.SeasonDetailsResult
import com.bk.mmovies.domain.repository.TvSeriesRepository
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
class SeasonDetailsViewModel @Inject constructor(
        private val tvRepository: TvSeriesRepository,
        localeMonitor: LocaleMonitor,
        internetMonitor: InternetMonitor
                                                 ) :
        ViewModel() {

    private val _seasonDetailsScreenState = MutableStateFlow<SeasonDetailsScreenState>(
            SeasonDetailsScreenState.Loading
                                                                                        )
    val seasonDetailsScreenState: StateFlow<SeasonDetailsScreenState> =
            _seasonDetailsScreenState.asStateFlow()

    private var seriesId: Int? = null
    private var seasonNumber: Int? = null
    private var loadSeasonDetailsJob: Job? = null

    init {
        // Reload the currently displayed season in the new language whenever
        // the device/app language changes, including while the app is backgrounded.
        // A locale change often lands right as the OS is mid-reconnect (seen as
        // an immediate UnknownHostException), so wait for connectivity before firing.
        viewModelScope.launch {
            localeMonitor.currentLanguage
                    .drop(1)
                    .collectLatest {
                        val currentSeriesId = seriesId ?: return@collectLatest
                        val currentSeasonNumber = seasonNumber ?: return@collectLatest
                        internetMonitor.isInternetAvailable.first { it }
                        _seasonDetailsScreenState.value = SeasonDetailsScreenState.Loading
                        fetchSeasonDetails(currentSeriesId, currentSeasonNumber)
                    }
        }
    }

    fun loadSeasonDetails(seriesId: Int, seasonNumber: Int) {
        if (this.seriesId == seriesId && this.seasonNumber == seasonNumber) return
        this.seriesId = seriesId
        this.seasonNumber = seasonNumber
        fetchSeasonDetails(seriesId, seasonNumber)
    }

    fun retry() {
        val seriesId = seriesId ?: return
        val seasonNumber = seasonNumber ?: return
        _seasonDetailsScreenState.value = SeasonDetailsScreenState.Loading
        fetchSeasonDetails(seriesId, seasonNumber)
    }

    private fun fetchSeasonDetails(seriesId: Int, seasonNumber: Int) {
        loadSeasonDetailsJob?.cancel()
        loadSeasonDetailsJob = viewModelScope.launch {
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
}

sealed interface SeasonDetailsScreenState {
    object Loading : SeasonDetailsScreenState
    data class Content(val season: SeasonModel) : SeasonDetailsScreenState
    data class Error(val errorMessage: String) : SeasonDetailsScreenState
}
