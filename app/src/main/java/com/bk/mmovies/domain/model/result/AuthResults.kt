package com.bk.mmovies.domain.model.result

sealed interface ApiKeyValidationResult {
    data object Success : ApiKeyValidationResult
    data class Failure(val message: String) : ApiKeyValidationResult
}

sealed interface LoginValidationTokenResult {
    data class Success(val loginValidationToken: String) : LoginValidationTokenResult
    data class Failure(val errorMessage: String) : LoginValidationTokenResult
}

sealed interface LoginWithCredentialsResult {
    data class Success(val loginValidationToken: String) : LoginWithCredentialsResult
    data class Failure(val errorMessage: String) : LoginWithCredentialsResult
}

sealed interface LoginSessionIdResult {
    data class Success(val loginSessionId: String) : LoginSessionIdResult
    data class Failure(val errorMessage: String) : LoginSessionIdResult
}

sealed interface GuestSessionIdResult {
    data class Success(val guestSessionId: String) : GuestSessionIdResult
    data class Failure(val errorMessage: String) : GuestSessionIdResult
}

sealed interface LogoutResult {
    data object Success : LogoutResult
    data class Failure(val errorMessage: String) : LogoutResult
}
