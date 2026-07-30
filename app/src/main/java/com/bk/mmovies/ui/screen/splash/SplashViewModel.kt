package com.bk.mmovies.ui.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bk.mmovies.connectivity.InternetMonitor
import com.bk.mmovies.domain.model.result.ApiKeyValidationResult
import com.bk.mmovies.domain.repository.ApiKeyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class SplashViewModel @Inject constructor(
        private val apiKeyRepository: ApiKeyRepository,
        internetMonitor: InternetMonitor
                                         ) : ViewModel() {

    private val _screenState =
            MutableStateFlow<SplashScreenState>(
                    SplashScreenState.Loading
                                               )

    val screenState: StateFlow<SplashScreenState> =
            _screenState.asStateFlow()

    private val _goToMoviesEvent =
            MutableSharedFlow<Unit>(
                    extraBufferCapacity = 1
                                   )

    val goToMoviesEvent: SharedFlow<Unit> =
            _goToMoviesEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            internetMonitor.isInternetAvailable
                    .collectLatest { isInternetAvailable ->

                        if (!isInternetAvailable) {
                            _screenState.value =
                                    SplashScreenState.Offline
                        } else {
                            loadSplashData()
                        }
                    }
        }
    }

    fun saveApiKey(apiKey: String) {
        apiKeyRepository.saveApiKey(apiKey)
        viewModelScope.launch { loadSplashData() }
    }

    private suspend fun loadSplashData() {
        _screenState.value = SplashScreenState.Loading
        delay(1000.milliseconds)
        if (apiKeyRepository.getApiKey() != null) {
            when (apiKeyRepository.getApiKeyValidationResult()) {
                is ApiKeyValidationResult.Failure -> {
                    _screenState.value = SplashScreenState.InvalidApiKey
                }
                ApiKeyValidationResult.Success    -> {
                    _goToMoviesEvent.emit(Unit)
                }
            }
        } else {
            _screenState.value = SplashScreenState.MissingApiKey
        }
    }
}

sealed interface SplashScreenState {
    data object Loading : SplashScreenState
    data object MissingApiKey : SplashScreenState
    data object InvalidApiKey : SplashScreenState
    data object Offline : SplashScreenState
}