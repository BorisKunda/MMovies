package com.bk.mmovies.ui.screen.details.tvseriesdetails

import androidx.lifecycle.viewModelScope
import com.bk.mmovies.connectivity.InternetMonitor
import com.bk.mmovies.domain.model.TvSeriesDetailsModel
import com.bk.mmovies.domain.model.result.ToggleFavoriteResult
import com.bk.mmovies.domain.model.result.TvSeriesDetailsResult
import com.bk.mmovies.domain.repository.AuthenticationRepository
import com.bk.mmovies.domain.repository.TvSeriesRepository
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
class TvSeriesDetailsViewModel @Inject constructor(
        private val tvRepository: TvSeriesRepository,
        private val authenticationRepository: AuthenticationRepository,
        localeMonitor: LocaleMonitor,
        internetMonitor: InternetMonitor
                                                   ) : DetailsViewModel<Int>(localeMonitor, internetMonitor) {

    private val _tvSeriesDetailsScreenState = MutableStateFlow<TvSeriesDetailsScreenState>(
            TvSeriesDetailsScreenState.Loading
                                                                                            )
    val tvSeriesDetailsScreenState: StateFlow<TvSeriesDetailsScreenState> =
            _tvSeriesDetailsScreenState.asStateFlow()

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

    fun loadTvSeriesDetails(seriesId: Int) = load(seriesId)

    override fun onReload() {
        _tvSeriesDetailsScreenState.value = TvSeriesDetailsScreenState.Loading
    }

    override suspend fun fetchDetails(id: Int) {
        val sessionId = authenticationRepository.getSharedPrefLoginSessionId()
        when (val result = tvRepository.getTvSeriesDetails(id, sessionId)) {
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

    fun onFavoriteClicked() {
        val currentState = _tvSeriesDetailsScreenState.value
        if (currentState !is TvSeriesDetailsScreenState.Content) return
        val sessionId = authenticationRepository.getSharedPrefLoginSessionId() ?: return
        val accountId = authenticationRepository.getSharedPrefAccountId() ?: return
        val tvSeriesDetailsModel = currentState.tvSeriesDetailsModel
        val newIsFavorite = !tvSeriesDetailsModel.isFavorite

        // Optimistic: flip the star immediately, revert only if the call fails.
        _tvSeriesDetailsScreenState.value = TvSeriesDetailsScreenState.Content(
                tvSeriesDetailsModel.copy(isFavorite = newIsFavorite)
                                                                               )
        viewModelScope.launch {
            val result = tvRepository.toggleFavorite(
                    accountId,
                    sessionId,
                    tvSeriesDetailsModel.id,
                    newIsFavorite
                                                      )
            revertFavoriteOnFailure(
                    content = tvSeriesDetailsModel,
                    previousIsFavorite = tvSeriesDetailsModel.isFavorite,
                    contentId = { it.id },
                    withFavorite = { details, isFavorite -> details.copy(isFavorite = isFavorite) },
                    currentContent = {
                        (_tvSeriesDetailsScreenState.value as? TvSeriesDetailsScreenState.Content)?.tvSeriesDetailsModel
                    },
                    applyContent = { _tvSeriesDetailsScreenState.value = TvSeriesDetailsScreenState.Content(it) },
                    onFailureMessage = { _messageEvent.tryEmit(it) },
                    result = result
                                    )
        }
    }
}

sealed interface TvSeriesDetailsScreenState {
    object Loading : TvSeriesDetailsScreenState
    data class Content(val tvSeriesDetailsModel: TvSeriesDetailsModel) : TvSeriesDetailsScreenState
    data class Error(val errorMessage: String) : TvSeriesDetailsScreenState
}
