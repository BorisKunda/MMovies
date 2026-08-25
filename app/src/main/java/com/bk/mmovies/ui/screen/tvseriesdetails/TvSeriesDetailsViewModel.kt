package com.bk.mmovies.ui.screen.tvseriesdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bk.mmovies.connectivity.InternetMonitor
import com.bk.mmovies.domain.model.TvSeriesDetailsModel
import com.bk.mmovies.domain.model.result.ToggleFavoriteResult
import com.bk.mmovies.domain.model.result.TvSeriesDetailsResult
import com.bk.mmovies.domain.repository.AuthenticationRepository
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
class TvSeriesDetailsViewModel @Inject constructor(
        private val tvRepository: TvSeriesRepository,
        private val authenticationRepository: AuthenticationRepository,
        localeMonitor: LocaleMonitor,
        internetMonitor: InternetMonitor
                                                   ) :
        ViewModel() {

    private val _tvSeriesDetailsScreenState = MutableStateFlow<TvSeriesDetailsScreenState>(
            TvSeriesDetailsScreenState.Loading
                                                                                            )
    val tvSeriesDetailsScreenState: StateFlow<TvSeriesDetailsScreenState> =
            _tvSeriesDetailsScreenState.asStateFlow()

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

    private var seriesId: Int? = null
    private var loadTvSeriesDetailsJob: Job? = null

    init {
        // Reload the currently displayed series in the new language whenever
        // the device/app language changes, including while the app is backgrounded.
        // A locale change often lands right as the OS is mid-reconnect (seen as
        // an immediate UnknownHostException), so wait for connectivity before firing.
        viewModelScope.launch {
            localeMonitor.currentLanguage
                    .drop(1)
                    .collectLatest {
                        val currentSeriesId = seriesId ?: return@collectLatest
                        internetMonitor.isInternetAvailable.first { it }
                        _tvSeriesDetailsScreenState.value = TvSeriesDetailsScreenState.Loading
                        fetchTvSeriesDetails(currentSeriesId)
                    }
        }
    }

    fun loadTvSeriesDetails(seriesId: Int) {
        if (this.seriesId == seriesId) return
        this.seriesId = seriesId
        fetchTvSeriesDetails(seriesId)
    }

    fun retry() {
        val seriesId = seriesId ?: return
        _tvSeriesDetailsScreenState.value = TvSeriesDetailsScreenState.Loading
        fetchTvSeriesDetails(seriesId)
    }

    private fun fetchTvSeriesDetails(seriesId: Int) {
        val sessionId = authenticationRepository.getSharedPrefLoginSessionId()
        loadTvSeriesDetailsJob?.cancel()
        loadTvSeriesDetailsJob = viewModelScope.launch {
            when (val result = tvRepository.getTvSeriesDetails(seriesId, sessionId)) {
                is TvSeriesDetailsResult.Success -> {
                    _tvSeriesDetailsScreenState.value = TvSeriesDetailsScreenState.Content(
                            result.tvSeriesDetails
                                                                                           )
                }
                is TvSeriesDetailsResult.Failure -> {
                    _tvSeriesDetailsScreenState.value = TvSeriesDetailsScreenState.Error(
                            result.errorMessage
                                                                                        )
                }
            }
        }
    }

    fun onFavoriteClicked() {
        val currentState = _tvSeriesDetailsScreenState.value
        if (currentState !is TvSeriesDetailsScreenState.Content) return
        val sessionId = authenticationRepository.getSharedPrefLoginSessionId() ?: return
        val accountId = authenticationRepository.getSharedPrefAccountId() ?: return
        val tvSeriesDetails = currentState.tvSeriesDetails
        val newIsFavorite = !tvSeriesDetails.isFavorite

        // Optimistic: flip the star immediately, revert only if the call fails.
        _tvSeriesDetailsScreenState.value = TvSeriesDetailsScreenState.Content(
                tvSeriesDetails.copy(isFavorite = newIsFavorite)
                                                                               )
        viewModelScope.launch {
            val result = tvRepository.toggleFavorite(
                    accountId,
                    sessionId,
                    tvSeriesDetails.id,
                    newIsFavorite
                                                      )
            if (result is ToggleFavoriteResult.Failure) {
                _tvSeriesDetailsScreenState.value = TvSeriesDetailsScreenState.Content(tvSeriesDetails)
            }
        }
    }
}

sealed interface TvSeriesDetailsScreenState {
    object Loading : TvSeriesDetailsScreenState
    data class Content(val tvSeriesDetails: TvSeriesDetailsModel) : TvSeriesDetailsScreenState
    data class Error(val errorMessage: String) : TvSeriesDetailsScreenState
}
