package com.bk.mmovies.domain.model.result

import com.bk.mmovies.domain.model.TvSeriesDetailsModel

sealed interface TvSeriesDetailsResult {
    data class Success(val tvSeriesDetails: TvSeriesDetailsModel) : TvSeriesDetailsResult
    data class Failure(val errorMessage: String) : TvSeriesDetailsResult
}
