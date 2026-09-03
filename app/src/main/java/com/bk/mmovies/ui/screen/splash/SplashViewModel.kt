package com.bk.mmovies.ui.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

// Keeps the logo on screen long enough to read rather than flashing past on a
// warm start with a cached session.
private val SPLASH_MINIMUM_VISIBLE_DURATION = 1000.milliseconds

@HiltViewModel
class SplashViewModel @Inject constructor(
        private val authenticationRepository: AuthenticationRepository
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

    // Latched once a navigation event has been emitted; see loadSplashData().
    private var hasNavigatedAway = false

    init {
        viewModelScope.launch {
            loadSplashData()
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
        delay(SPLASH_MINIMUM_VISIBLE_DURATION)
        if (authenticationRepository.getSharedPrefApiKey() == null) {
            _screenState.value = SplashScreenState.MissingApiKey
            return
        }
        // saveApiKey() can call this a second time after init's call already
        // navigated away (e.g. a slow validation response landing late) -
        // without a latch that would fire a second navigate() and push a
        // duplicate destination onto the back stack.
        if (hasNavigatedAway) return
        hasNavigatedAway = true
        if (authenticationRepository.getSharedPrefLoginSessionId() != null) {
            _goToMoviesEvent.emit(Unit)
        } else {
            _goToAuthEvent.emit(Unit)
        }
    }
}

sealed interface SplashScreenState {
    data object Loading : SplashScreenState
    data object MissingApiKey : SplashScreenState
}