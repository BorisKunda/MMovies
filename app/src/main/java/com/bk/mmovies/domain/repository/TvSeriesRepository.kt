package com.bk.mmovies.domain.repository

import com.bk.mmovies.domain.model.TvSeriesCategory
import com.bk.mmovies.domain.model.result.EpisodeDetailsResult
import com.bk.mmovies.domain.model.result.SeasonDetailsResult
import com.bk.mmovies.domain.model.result.ToggleFavoriteResult
import com.bk.mmovies.domain.model.result.TvSeriesDetailsResult
import com.bk.mmovies.domain.model.result.TvSeriesResult

interface TvSeriesRepository {
    suspend fun getTvSeriesByCategory(category: TvSeriesCategory, page: Int = 1): TvSeriesResult
    suspend fun getTvSeriesDetails(seriesId: Int, sessionId: String?): TvSeriesDetailsResult
    suspend fun getSeasonDetails(seriesId: Int, seasonNumber: Int): SeasonDetailsResult
    suspend fun getEpisodeDetails(seriesId: Int, seasonNumber: Int, episodeNumber: Int): EpisodeDetailsResult
    suspend fun getFavoriteTvSeries(accountId: Int, sessionId: String, page: Int = 1): TvSeriesResult
    suspend fun syncFavoriteIds(accountId: Int, sessionId: String)
    suspend fun getCachedFavoriteIds(): Set<Int>
    suspend fun toggleFavorite(
            accountId: Int,
            sessionId: String,
            tvSeriesId: Int,
            isFavorite: Boolean
                              ): ToggleFavoriteResult
}
