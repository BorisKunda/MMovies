package com.bk.mmovies.tv.ui.details.tvseriesdetails

import androidx.lifecycle.viewModelScope
import com.bk.mmovies.domain.model.SeasonModel
import com.bk.mmovies.domain.model.TvSeriesDetailsModel
import com.bk.mmovies.domain.model.result.SeasonDetailsResult
import com.bk.mmovies.domain.model.result.TvSeriesDetailsResult
import com.bk.mmovies.domain.repository.AuthenticationRepository
import com.bk.mmovies.domain.repository.TvSeriesRepository
import com.bk.mmovies.locale.LocaleMonitor
import com.bk.mmovies.tv.ui.details.DetailsTvViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// The TV series details screen shows every season's episodes inline (a
// mobile-style tap-through to a separate season screen doesn't fit a D-pad
// leanback layout as well as scrolling straight down through them) - so
// unlike the mobile flow, this fetches each season's full episode list up
// front rather than lazily on navigation. tvSeriesDetailsModel.seasons only
// carries poster/name/count metadata; loadAllSeasonEpisodes hydrates each
// entry with its real episodes once fetched, one season at a time, so
// earlier seasons don't wait on the slowest one.
@HiltViewModel
class TvSeriesDetailsTvViewModel @Inject constructor(
        private val tvRepository: TvSeriesRepository,
        private val authenticationRepository: AuthenticationRepository,
        localeMonitor: LocaleMonitor
                                                      ) : DetailsTvViewModel<Int>(localeMonitor) {

    private val _screenState = MutableStateFlow<TvSeriesDetailsTvScreenState>(
            TvSeriesDetailsTvScreenState.Loading
                                                                              )
    val screenState: StateFlow<TvSeriesDetailsTvScreenState> = _screenState.asStateFlow()

    private val _messageEvent: MutableSharedFlow<String> = MutableSharedFlow(extraBufferCapacity = 1)
    val messageEvent: SharedFlow<String> = _messageEvent.asSharedFlow()

    val canToggleFavorite: Boolean
        get() = authenticationRepository.getSharedPrefLoginSessionId() != null &&
                authenticationRepository.getSharedPrefAccountId() != null

    fun loadTvSeriesDetails(seriesId: Int) = load(seriesId)

    override fun onReload() {
        _screenState.value = TvSeriesDetailsTvScreenState.Loading
    }

    override suspend fun fetchDetails(id: Int) {
        val sessionId = authenticationRepository.getSharedPrefLoginSessionId()
        when (val result = tvRepository.getTvSeriesDetails(id, sessionId)) {
            is TvSeriesDetailsResult.Success -> {
                _screenState.value = TvSeriesDetailsTvScreenState.Content(
                        tvSeriesDetailsModel = result.tvSeriesDetails,
                        seasons = result.tvSeriesDetails.seasons
                                                                         )
                loadAllSeasonEpisodes(id, result.tvSeriesDetails.seasons)
            }
            is TvSeriesDetailsResult.Failure -> {
                _screenState.value = TvSeriesDetailsTvScreenState.Error(result.errorMessage)
            }
        }
    }

    private suspend fun loadAllSeasonEpisodes(seriesId: Int, seasons: List<SeasonModel>) = coroutineScope {
        seasons.map { season ->
            async {
                when (val result = tvRepository.getSeasonDetails(seriesId, season.seasonNumber)) {
                    is SeasonDetailsResult.Success -> replaceSeason(result.season)
                    is SeasonDetailsResult.Failure -> _messageEvent.tryEmit(result.errorMessage)
                }
            }
        }.awaitAll()
    }

    private fun replaceSeason(hydratedSeason: SeasonModel) {
        val current = _screenState.value as? TvSeriesDetailsTvScreenState.Content ?: return
        _screenState.value = current.copy(
                seasons = current.seasons.map {
                    if (it.seasonNumber == hydratedSeason.seasonNumber) hydratedSeason else it
                }
                                          )
    }

    fun onFavoriteClicked() {
        val currentState = _screenState.value
        if (currentState !is TvSeriesDetailsTvScreenState.Content) return
        val sessionId = authenticationRepository.getSharedPrefLoginSessionId() ?: return
        val accountId = authenticationRepository.getSharedPrefAccountId() ?: return
        val details = currentState.tvSeriesDetailsModel
        val newIsFavorite = !details.isFavorite

        _screenState.value = currentState.copy(tvSeriesDetailsModel = details.copy(isFavorite = newIsFavorite))
        viewModelScope.launch {
            val result = tvRepository.toggleFavorite(accountId, sessionId, details.id, newIsFavorite)
            revertFavoriteOnFailure(
                    content = details,
                    previousIsFavorite = details.isFavorite,
                    contentId = { it.id },
                    withFavorite = { d, isFavorite -> d.copy(isFavorite = isFavorite) },
                    currentContent = {
                        (_screenState.value as? TvSeriesDetailsTvScreenState.Content)?.tvSeriesDetailsModel
                    },
                    applyContent = { updated ->
                        val latest = _screenState.value as? TvSeriesDetailsTvScreenState.Content
                        if (latest != null) {
                            _screenState.value = latest.copy(tvSeriesDetailsModel = updated)
                        }
                    },
                    onFailureMessage = { _messageEvent.tryEmit(it) },
                    result = result
                                    )
        }
    }
}

sealed interface TvSeriesDetailsTvScreenState {
    object Loading : TvSeriesDetailsTvScreenState
    data class Content(
            val tvSeriesDetailsModel: TvSeriesDetailsModel,
            val seasons: List<SeasonModel> = emptyList()
                       ) : TvSeriesDetailsTvScreenState
    data class Error(val errorMessage: String) : TvSeriesDetailsTvScreenState
}
