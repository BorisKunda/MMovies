package com.bk.mmovies.data.repositoryimpl

import android.content.Context
import com.bk.mmovies.R
import com.bk.mmovies.data.mapper.SeasonMapper
import com.bk.mmovies.data.mapper.TvSeriesDetailsMapper
import com.bk.mmovies.data.mapper.TvSeriesMapper
import com.bk.mmovies.data.source.local.dao.TvFavoriteDao
import com.bk.mmovies.data.source.local.entity.TvFavoriteEntity
import com.bk.mmovies.data.source.remote.APPEND_TO_RESPONSE_CREDITS
import com.bk.mmovies.data.source.remote.APPEND_TO_RESPONSE_CREDITS_AND_ACCOUNT_STATES
import com.bk.mmovies.data.source.remote.NetworkManager
import com.bk.mmovies.data.source.remote.api.TmdbApi
import com.bk.mmovies.data.source.remote.dto.EpisodeDto
import com.bk.mmovies.data.source.remote.dto.SeasonDetailsDto
import com.bk.mmovies.data.source.remote.dto.ToggleFavoriteRequestDto
import com.bk.mmovies.data.source.remote.dto.ToggleFavoriteResponseDto
import com.bk.mmovies.data.source.remote.dto.TvSeriesDetailsDto
import com.bk.mmovies.data.source.remote.dto.TvSeriesListDto
import com.bk.mmovies.data.source.remote.result.ApiCallResult
import com.bk.mmovies.domain.model.TvSeriesCategory
import com.bk.mmovies.domain.model.result.EpisodeDetailsResult
import com.bk.mmovies.domain.model.result.SeasonDetailsResult
import com.bk.mmovies.domain.model.result.ToggleFavoriteResult
import com.bk.mmovies.domain.model.result.TvSeriesDetailsResult
import com.bk.mmovies.domain.model.result.TvSeriesResult
import com.bk.mmovies.domain.model.result.TvSeriesResult.*
import com.bk.mmovies.domain.repository.TvSeriesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class TvSeriesRepositoryImpl @Inject constructor(
        private val api: TmdbApi,
        private val networkManager: NetworkManager,
        private val tvSeriesMapper: TvSeriesMapper,
        private val seasonMapper: SeasonMapper,
        private val tvSeriesDetailsMapper: TvSeriesDetailsMapper,
        private val tvFavoriteDao: TvFavoriteDao,
        @ApplicationContext private val context: Context
                                                 ) : TvSeriesRepository {
    private val failureMessage: String
        get() = context.getString(R.string.error_tv_series_load_failed)
    private val seasonFailureMessage: String
        get() = context.getString(R.string.error_season_load_failed)
    private val episodeFailureMessage: String
        get() = context.getString(R.string.error_episode_load_failed)
    private val detailsFailureMessage: String
        get() = context.getString(R.string.error_tv_series_details_load_failed)
    private val favoriteToggleFailureMessage: String
        get() = context.getString(R.string.error_favorite_toggle_failed)

    private fun tomorrowDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        return SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(calendar.time)
    }

    override suspend fun getTvSeriesByCategory(category: TvSeriesCategory, page: Int): TvSeriesResult {
        val apiCallResult: ApiCallResult<TvSeriesListDto> = when (category) {
            TvSeriesCategory.PopularTvSeriesCategory      -> {
                networkManager.executeApiCall(
                        "GetPopularTvSeries",
                        apiCall = { -> api.getPopularTvSeries(page = page) })
            }

            TvSeriesCategory.AiringTodayTvSeriesCategory  -> {
                networkManager.executeApiCall(
                        "GetAiringTodayTvSeries",
                        apiCall = { -> api.getAiringTodayTvSeries(page = page) })
            }

            TvSeriesCategory.OnTVTvSeriesCategory         -> {
                networkManager.executeApiCall(
                        "GetOnTvTvSeries",
                        apiCall = { -> api.getOnTvTvSeries(page = page) })
            }

            TvSeriesCategory.TopRatedTvSeriesCategory     -> {
                networkManager.executeApiCall(
                        "GetTopRatedTvSeries",
                        apiCall = { -> api.getTopRatedTvSeries(page = page) })
            }

            TvSeriesCategory.FavoritesTvSeriesCategory    -> {
                // Favorites need an accountId/sessionId the category-only
                // signature doesn't carry, so a future favorites-aware caller
                // would route this elsewhere. Kept only so the `when` stays
                // exhaustive, mirroring MovieRepositoryImpl.
                return Success(emptyList())
            }
            TvSeriesCategory.UpcomingTvSeriesCategory -> {
                // TMDB has no dedicated "upcoming" TV endpoint (unlike movies);
                // discover/tv with a first_air_date floor of tomorrow is the
                // equivalent query.
                networkManager.executeApiCall(
                        "GetUpcomingTvSeries",
                        apiCall = { -> api.getUpcomingTvSeries(firstAirDateGte = tomorrowDate(), page = page) })
            }
        }

        return when (apiCallResult) {
            is ApiCallResult.Success<TvSeriesListDto> -> {
                val tvSeriesDtos = apiCallResult.data.tvSeries.orEmpty()
                val sortedTvSeriesDtos = if (category == TvSeriesCategory.UpcomingTvSeriesCategory) {
                    tvSeriesDtos.sortedBy { it.firstAirDate }
                } else {
                    tvSeriesDtos
                }
                // The category endpoints don't return per-item favorite status,
                // so cross-reference against the ids synced into the local
                // cache (see syncFavoriteIds) to mark stars correctly.
                val favoriteIds = tvFavoriteDao.getAllFavoriteIds().toSet()
                val models = tvSeriesMapper.toModels(sortedTvSeriesDtos).map {
                    it.copy(isFavorite = favoriteIds.contains(it.id))
                }
                TvSeriesResult.Success(models, page, apiCallResult.data.totalPages ?: page)
            }
            is ApiCallResult.Failure                  -> {
                TvSeriesResult.Failure(failureMessage)
            }
        }
    }

    override suspend fun getTvSeriesDetails(seriesId: Int, sessionId: String?): TvSeriesDetailsResult {
        // account_states (and thus favorite status) is only returned by TMDB
        // when a session_id accompanies the request.
        val appendToResponse = if (sessionId != null) {
            APPEND_TO_RESPONSE_CREDITS_AND_ACCOUNT_STATES
        } else {
            APPEND_TO_RESPONSE_CREDITS
        }
        val apiCallResult: ApiCallResult<TvSeriesDetailsDto> = networkManager.executeApiCall(
                "GetTvSeriesDetails",
                apiCall = { -> api.getTvSeriesDetails(seriesId, appendToResponse, sessionId) })

        return when (apiCallResult) {
            is ApiCallResult.Success<TvSeriesDetailsDto> -> {
                TvSeriesDetailsResult.Success(tvSeriesDetailsMapper.toModel(apiCallResult.data))
            }
            is ApiCallResult.Failure                     -> {
                TvSeriesDetailsResult.Failure(detailsFailureMessage)
            }
        }
    }

    override suspend fun getFavoriteTvSeries(accountId: Int, sessionId: String, page: Int): TvSeriesResult {
        val apiCallResult: ApiCallResult<TvSeriesListDto> = networkManager.executeApiCall(
                "GetFavoriteTvSeries",
                apiCall = { -> api.getFavoriteTvSeries(accountId, sessionId, page = page) })

        return when (apiCallResult) {
            is ApiCallResult.Success<TvSeriesListDto> -> {
                // These results are the favorites list itself, so every entry
                // is a favorite by definition regardless of what the mapper's
                // default would otherwise produce.
                val tvSeries = tvSeriesMapper.toModels(apiCallResult.data.tvSeries.orEmpty())
                        .map { it.copy(isFavorite = true) }
                TvSeriesResult.Success(tvSeries, page, apiCallResult.data.totalPages ?: page)
            }
            is ApiCallResult.Failure                  -> {
                TvSeriesResult.Failure(failureMessage)
            }
        }
    }

    override suspend fun syncFavoriteIds(accountId: Int, sessionId: String) {
        val apiCallResult: ApiCallResult<TvSeriesListDto> = networkManager.executeApiCall(
                "SyncTvFavoriteIds",
                apiCall = { -> api.getFavoriteTvSeries(accountId, sessionId) })

        if (apiCallResult is ApiCallResult.Success<TvSeriesListDto>) {
            val ids = apiCallResult.data.tvSeries.orEmpty().mapNotNull { it.id }
            tvFavoriteDao.clearAll()
            tvFavoriteDao.insertAll(ids.map { TvFavoriteEntity(it) })
        }
    }

    override suspend fun getCachedFavoriteIds(): Set<Int> {
        return tvFavoriteDao.getAllFavoriteIds().toSet()
    }

    override suspend fun toggleFavorite(
            accountId: Int,
            sessionId: String,
            tvSeriesId: Int,
            isFavorite: Boolean
                                        ): ToggleFavoriteResult {
        val apiCallResult: ApiCallResult<ToggleFavoriteResponseDto> = networkManager.executeApiCall(
                "ToggleTvFavorite",
                apiCall = {
                    ->
                    api.toggleFavorite(
                            accountId,
                            sessionId,
                            ToggleFavoriteRequestDto(mediaType = "tv", mediaId = tvSeriesId, favorite = isFavorite)
                                       )
                })

        return when (apiCallResult) {
            is ApiCallResult.Success<ToggleFavoriteResponseDto> -> {
                if (isFavorite) {
                    tvFavoriteDao.insert(TvFavoriteEntity(tvSeriesId))
                } else {
                    tvFavoriteDao.deleteById(tvSeriesId)
                }
                ToggleFavoriteResult.Success
            }
            is ApiCallResult.Failure                            -> {
                ToggleFavoriteResult.Failure(favoriteToggleFailureMessage)
            }
        }
    }

    override suspend fun getSeasonDetails(seriesId: Int, seasonNumber: Int): SeasonDetailsResult {
        val apiCallResult: ApiCallResult<SeasonDetailsDto> = networkManager.executeApiCall(
                "GetSeasonDetails",
                apiCall = { -> api.getSeasonDetails(seriesId, seasonNumber) })

        return when (apiCallResult) {
            is ApiCallResult.Success<SeasonDetailsDto> -> {
                SeasonDetailsResult.Success(seasonMapper.toModel(apiCallResult.data))
            }
            is ApiCallResult.Failure                   -> {
                SeasonDetailsResult.Failure(seasonFailureMessage)
            }
        }
    }

    override suspend fun getEpisodeDetails(
            seriesId: Int,
            seasonNumber: Int,
            episodeNumber: Int
                                           ): EpisodeDetailsResult {
        val apiCallResult: ApiCallResult<EpisodeDto> = networkManager.executeApiCall(
                "GetEpisodeDetails",
                apiCall = { -> api.getEpisodeDetails(seriesId, seasonNumber, episodeNumber) })

        return when (apiCallResult) {
            is ApiCallResult.Success<EpisodeDto> -> {
                EpisodeDetailsResult.Success(seasonMapper.toModel(apiCallResult.data))
            }
            is ApiCallResult.Failure             -> {
                EpisodeDetailsResult.Failure(episodeFailureMessage)
            }
        }
    }
}
