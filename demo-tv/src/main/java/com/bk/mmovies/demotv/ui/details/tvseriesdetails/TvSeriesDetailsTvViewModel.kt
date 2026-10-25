package com.bk.mmovies.demotv.ui.details.tvseriesdetails

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.bk.mmovies.demotv.R
import com.bk.mmovies.demotv.mock.mockTvSeriesDetails
import com.bk.mmovies.domain.model.SeasonModel
import com.bk.mmovies.domain.model.TvSeriesDetailsModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class TvSeriesDetailsTvViewModel(application: Application) : AndroidViewModel(application) {

    private val _screenState = MutableStateFlow<TvSeriesDetailsTvScreenState>(TvSeriesDetailsTvScreenState.Loading)
    val screenState: StateFlow<TvSeriesDetailsTvScreenState> = _screenState.asStateFlow()

    private val _messageEvent: MutableSharedFlow<String> = MutableSharedFlow(extraBufferCapacity = 1)
    val messageEvent: SharedFlow<String> = _messageEvent.asSharedFlow()

    private var seriesId: Int? = null

    fun loadTvSeriesDetails(seriesId: Int) {
        this.seriesId = seriesId
        val details = mockTvSeriesDetails(getApplication<Application>().resources).firstOrNull { it.id == seriesId }
        _screenState.value = if (details != null) {
            TvSeriesDetailsTvScreenState.Content(tvSeriesDetailsModel = details, seasons = details.seasons)
        } else {
            TvSeriesDetailsTvScreenState.Error(getApplication<Application>().getString(R.string.demo_details_unavailable))
        }
    }

    fun retry() {
        seriesId?.let(::loadTvSeriesDetails)
    }

    fun onFavoriteClicked() {
        _messageEvent.tryEmit(getApplication<Application>().getString(R.string.demo_favorites_unavailable))
    }

    fun onTrailerClicked() {
        _messageEvent.tryEmit(getApplication<Application>().getString(R.string.demo_trailer_unavailable))
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
