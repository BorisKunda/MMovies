package com.bk.mmovies.domain.model.result

import com.bk.mmovies.domain.model.SeasonModel

sealed interface SeasonDetailsResult {
    data class Success(val season: SeasonModel) : SeasonDetailsResult
    data class Failure(val errorMessage: String) : SeasonDetailsResult
}
