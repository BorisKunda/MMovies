package com.bk.mmovies.domain.model.result

import com.bk.mmovies.domain.model.EpisodeModel

sealed interface EpisodeDetailsResult {
    data class Success(val episode: EpisodeModel) : EpisodeDetailsResult
    data class Failure(val errorMessage: String) : EpisodeDetailsResult
}
