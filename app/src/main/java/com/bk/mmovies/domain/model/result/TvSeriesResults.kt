package com.bk.mmovies.domain.model.result

import com.bk.mmovies.domain.model.TvSeriesModel

sealed interface TvSeriesResult {
    data class Success(val tvSeries: List<TvSeriesModel>) : TvSeriesResult
    data class Failure(val errorMessage: String) : TvSeriesResult
}
