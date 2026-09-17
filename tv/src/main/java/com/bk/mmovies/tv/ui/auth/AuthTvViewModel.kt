package com.bk.mmovies.tv.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bk.mmovies.domain.model.result.GuestSessionIdResult
import com.bk.mmovies.domain.model.result.LoginSessionIdResult
import com.bk.mmovies.domain.model.result.LoginValidationTokenResult
import com.bk.mmovies.domain.model.result.LoginWithCredentialsResult
import com.bk.mmovies.domain.repository.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Mirrors app's AuthViewModel exactly - :tv can't depend on :app, so the same
// login/guest-session flow against the shared core AuthenticationRepository
// is duplicated here rather than shared.
@HiltViewModel
class AuthTvViewModel @Inject constructor(
        private val authenticationRepository: AuthenticationRepository
                                          ) : ViewModel() {
    private val _authScreenState =
            MutableStateFlow<AuthTvScreenState>(AuthTvScreenState.Unauthenticated)
    val authScreenState: StateFlow<AuthTvScreenState> = _authScreenState.asStateFlow()

    private val _goToMoviesNavEvent: MutableSharedFlow<Unit> =
            MutableSharedFlow(extraBufferCapacity = 1)
    val goToMoviesNavEvent: SharedFlow<Unit> = _goToMoviesNavEvent.asSharedFlow()

    fun onLoginClicked(username: String, password: String) {
        viewModelScope.launch {
            _authScreenState.value = AuthTvScreenState.Loading
            when (val tokenResult = authenticationRepository.getLoginValidationTokenResult()) {
                is LoginValidationTokenResult.Failure -> {
                    _authScreenState.value = AuthTvScreenState.Error(tokenResult.errorMessage)
                }
                is LoginValidationTokenResult.Success -> {
                    when (val credentialsResult = authenticationRepository.getLoginWithCredentialsResult(
                            username,
                            password,
                            tokenResult.loginValidationToken
                                                                                                          )) {
                        is LoginWithCredentialsResult.Failure -> {
                            _authScreenState.value = AuthTvScreenState.Error(credentialsResult.errorMessage)
                        }
                        is LoginWithCredentialsResult.Success -> {
                            when (val sessionResult = authenticationRepository.getLoginSessionIdResult(
                                    credentialsResult.loginValidationToken
                                                                                                        )) {
                                is LoginSessionIdResult.Failure -> {
                                    _authScreenState.value = AuthTvScreenState.Error(sessionResult.errorMessage)
                                }
                                is LoginSessionIdResult.Success -> {
                                    authenticationRepository.saveSharedPrefLoginSessionId(
                                            sessionResult.loginSessionId
                                                                                          )
                                    _goToMoviesNavEvent.emit(Unit)
                                    _authScreenState.value = AuthTvScreenState.Unauthenticated
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun onContinueAsGuestClicked() {
        viewModelScope.launch {
            _authScreenState.value = AuthTvScreenState.Loading
            when (val result: GuestSessionIdResult = authenticationRepository.getGuestSessionIdResult()) {
                is GuestSessionIdResult.Failure -> {
                    _authScreenState.value = AuthTvScreenState.Error(result.errorMessage)
                }
                is GuestSessionIdResult.Success -> {
                    authenticationRepository.saveSharedPrefGuestSessionId(result.guestSessionId)
                    _goToMoviesNavEvent.emit(Unit)
                    _authScreenState.value = AuthTvScreenState.Unauthenticated
                }
            }
        }
    }
}

sealed interface AuthTvScreenState {
    object Loading : AuthTvScreenState
    object Unauthenticated : AuthTvScreenState
    data class Error(val errorMessage: String) : AuthTvScreenState
}
