package com.bk.mmovies.ui.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bk.mmovies.connectivity.InternetMonitor
import com.bk.mmovies.domain.model.result.ApiKeyValidationResult
import com.bk.mmovies.domain.repository.AuthenticationRepository
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
        private val authenticationRepository: AuthenticationRepository,
        internetMonitor: InternetMonitor
                                         ) : ViewModel() {

    private val _screenState =
            MutableStateFlow<SplashScreenState>(
                    SplashScreenState.Loading
                                               )

    val screenState: StateFlow<SplashScreenState> =
            _screenState.asStateFlow()

    private val _goToAuthEvent =
            MutableSharedFlow<Unit>(
                    extraBufferCapacity = 1
                                   )

    val goToAuthEvent: SharedFlow<Unit> =
            _goToAuthEvent.asSharedFlow()

    private val _goToMoviesEvent =
            MutableSharedFlow<Unit>(
                    extraBufferCapacity = 1
                                   )

    val goToMoviesEvent: SharedFlow<Unit> =
            _goToMoviesEvent.asSharedFlow()

    private val _invalidApiKeyToastEvent =
            MutableSharedFlow<Unit>(
                    extraBufferCapacity = 1
                                   )

    val invalidApiKeyToastEvent: SharedFlow<Unit> =
            _invalidApiKeyToastEvent.asSharedFlow()

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
        viewModelScope.launch {
            _screenState.value = SplashScreenState.Loading
            authenticationRepository.saveSharedPrefApiKey(apiKey)
            when (authenticationRepository.getApiKeyValidationResult()) {
                is ApiKeyValidationResult.Failure -> {
                    authenticationRepository.removeSharedPrefApiKey()
                    _invalidApiKeyToastEvent.emit(Unit)
                    _screenState.value = SplashScreenState.MissingApiKey
                }
                ApiKeyValidationResult.Success    -> {
                    loadSplashData()
                }
            }
        }
    }

    private suspend fun loadSplashData() {
        _screenState.value = SplashScreenState.Loading
        delay(1000.milliseconds)
        if (authenticationRepository.getSharedPrefApiKey() != null) {
            if (authenticationRepository.getSharedPrefLoginSessionId() != null) {
                _goToMoviesEvent.emit(Unit)
            } else {
                _goToAuthEvent.emit(Unit)
            }
        } else {
            _screenState.value = SplashScreenState.MissingApiKey
        }
    }
}

sealed interface SplashScreenState {
    data object Loading : SplashScreenState
    data object MissingApiKey : SplashScreenState
    data object Offline : SplashScreenState
}