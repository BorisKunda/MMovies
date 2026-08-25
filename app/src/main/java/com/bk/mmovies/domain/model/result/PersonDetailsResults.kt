package com.bk.mmovies.domain.model.result

import com.bk.mmovies.domain.model.PersonDetailsModel

sealed interface PersonDetailsResult {
    data class Success(val personDetails: PersonDetailsModel) : PersonDetailsResult
    data class Failure(val errorMessage: String) : PersonDetailsResult
}
