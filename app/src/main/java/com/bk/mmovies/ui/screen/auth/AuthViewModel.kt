package com.bk.mmovies.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bk.mmovies.connectivity.InternetMonitor
import com.bk.mmovies.domain.model.result.GuestSessionIdResult
import com.bk.mmovies.domain.model.result.LoginSessionIdResult
import com.bk.mmovies.domain.model.result.LoginValidationTokenResult
import com.bk.mmovies.domain.model.result.LoginWithCredentialsResult
import com.bk.mmovies.domain.repository.AuthenticationRepository
import com.bk.mmovies.locale.LocaleMonitor
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
class AuthViewModel @Inject constructor(
        private val authenticationRepository: AuthenticationRepository,
        localeMonitor: LocaleMonitor,
        internetMonitor: InternetMonitor
                                       ) : ViewModel() {
    private val _authScreenState =
            MutableStateFlow<AuthScreenState>(AuthScreenState.Unauthenticated)
    val authScreenState: StateFlow<AuthScreenState> = _authScreenState.asStateFlow()

    private val _goToMoviesNavEvent: MutableSharedFlow<Unit> =
            MutableSharedFlow(extraBufferCapacity = 1)
    val goToMoviesNavEvent: SharedFlow<Unit> =
            _goToMoviesNavEvent.asSharedFlow()

    fun onLoginClicked(username: String, password: String) {
        viewModelScope.launch {
            _authScreenState.value = AuthScreenState.Loading
            val tokenResult: LoginValidationTokenResult =
                    authenticationRepository.getLoginValidationTokenResult()
            when (tokenResult) {
                is LoginValidationTokenResult.Failure -> {
                    _authScreenState.value = AuthScreenState.Error(tokenResult.errorMessage)
                }
                is LoginValidationTokenResult.Success -> {
                    val credentialsResult: LoginWithCredentialsResult =
                            authenticationRepository.getLoginWithCredentialsResult(
                                    username,
                                    password,
                                    tokenResult.loginValidationToken
                                                                                   )
                    when (credentialsResult) {
                        is LoginWithCredentialsResult.Failure -> {
                            _authScreenState.value =
                                    AuthScreenState.Error(credentialsResult.errorMessage)
                        }
                        is LoginWithCredentialsResult.Success -> {
                            val sessionResult: LoginSessionIdResult =
                                    authenticationRepository.getLoginSessionIdResult(
                                            credentialsResult.loginValidationToken
                                                                                     )
                            when (sessionResult) {
                                is LoginSessionIdResult.Failure -> {
                                    _authScreenState.value =
                                            AuthScreenState.Error(sessionResult.errorMessage)
                                }
                                is LoginSessionIdResult.Success -> {
                                    authenticationRepository.saveSharedPrefLoginSessionId(
                                            sessionResult.loginSessionId
                                                                                          )
                                    _goToMoviesNavEvent.emit(Unit)
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
            _authScreenState.value = AuthScreenState.Loading
            val result: GuestSessionIdResult = authenticationRepository.getGuestSessionIdResult()
            when (result) {
                is GuestSessionIdResult.Failure -> {
                    _authScreenState.value = AuthScreenState.Error(result.errorMessage)
                }
                is GuestSessionIdResult.Success -> {
                    authenticationRepository.saveSharedPrefGuestSessionId(result.guestSessionId)
                    _goToMoviesNavEvent.emit(Unit)
                }
            }
        }
    }

}

sealed interface AuthScreenState {
    object Loading : AuthScreenState
    object Unauthenticated : AuthScreenState
    data class Error(val errorMessage: String) : AuthScreenState
}
