package com.bk.mmovies.data.repositoryimpl

import android.content.Context
import com.bk.mmovies.R
import com.bk.mmovies.data.mapper.MEDIA_TYPE_TV
import com.bk.mmovies.data.mapper.SeasonMapper
import com.bk.mmovies.data.mapper.SeriesAirDateLabelFormatter
import com.bk.mmovies.data.mapper.TvSeriesDetailsMapper
import com.bk.mmovies.data.mapper.TvSeriesMapper
import com.bk.mmovies.data.source.local.dao.TvFavoriteDao
import com.bk.mmovies.data.source.local.entity.TvFavoriteEntity
import com.bk.mmovies.data.source.remote.APPEND_TO_RESPONSE_CREDITS_AND_VIDEOS
import com.bk.mmovies.data.source.remote.APPEND_TO_RESPONSE_CREDITS_ACCOUNT_STATES_AND_VIDEOS
import com.bk.mmovies.data.source.remote.NetworkManager
import com.bk.mmovies.data.source.remote.api.TmdbApi
import com.bk.mmovies.data.source.remote.dto.EpisodeDto
import com.bk.mmovies.data.source.remote.dto.SeasonDetailsDto
import com.bk.mmovies.data.source.remote.dto.ToggleFavoriteRequestDto
import com.bk.mmovies.data.source.remote.dto.ToggleFavoriteResponseDto
import com.bk.mmovies.data.source.remote.dto.TvSeriesAirDateInfoDto
import com.bk.mmovies.data.source.remote.dto.TvSeriesDetailsDto
import com.bk.mmovies.data.source.remote.dto.TvSeriesListDto
import com.bk.mmovies.data.source.remote.result.ApiCallResult
import com.bk.mmovies.data.source.remote.result.isConnectivityFailure
import com.bk.mmovies.domain.model.SeriesAirDateLabel
import com.bk.mmovies.domain.model.TvSeriesCategory
import com.bk.mmovies.domain.model.isLastPage
import com.bk.mmovies.domain.model.result.EpisodeDetailsResult
import com.bk.mmovies.domain.model.result.SeasonDetailsResult
import com.bk.mmovies.domain.model.result.ToggleFavoriteResult
import com.bk.mmovies.domain.model.result.TvSeriesDetailsResult
import com.bk.mmovies.domain.model.result.TvSeriesResult
import com.bk.mmovies.domain.model.result.TvSeriesResult.*
import com.bk.mmovies.domain.repository.TvSeriesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

// TMDB returns 20 favorites per page, so this covers 1000 favorited series.
private const val MAX_FAVORITE_SYNC_PAGES = 50

class TvSeriesRepositoryImpl @Inject constructor(
        private val api: TmdbApi,
        private val networkManager: NetworkManager,
        private val tvSeriesMapper: TvSeriesMapper,
        private val seasonMapper: SeasonMapper,
        private val tvSeriesDetailsMapper: TvSeriesDetailsMapper,
        private val seriesAirDateLabelFormatter: SeriesAirDateLabelFormatter,
        private val tvFavoriteDao: TvFavoriteDao,
        @ApplicationContext private val context: Context
                                                 ) : TvSeriesRepository {
    // Unbounded but keyed by series id, which stays small relative to how
    // long the process lives; a failed lookup is deliberately not cached so
    // scrolling back to the row retries it instead of getting stuck on TBA.
    private val airDateLabelCache = ConcurrentHashMap<Int, SeriesAirDateLabel>()

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

    // getTvSeriesByCategory cross-references this on every single page load
    // (see below) to mark stars; without an in-memory copy that's a Room
    // query per page fetch even though syncFavoriteIds/toggleFavorite are the
    // only things that ever actually change it. Null means "not loaded yet",
    // not "no favorites" — the next read repopulates it from Room.
    private var favoriteIdsCache: Set<Int>? = null

    private suspend fun favoriteIds(): Set<Int> =
            favoriteIdsCache ?: tvFavoriteDao.getAllFavoriteIds().toSet().also { favoriteIdsCache = it }

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
                val favoriteIds = favoriteIds()
                val models = tvSeriesMapper.toModels(sortedTvSeriesDtos).map {
                    it.copy(isFavorite = favoriteIds.contains(it.id))
                }
                TvSeriesResult.Success(models, page, apiCallResult.data.totalPages ?: page)
            }
            is ApiCallResult.Failure                  -> {
                TvSeriesResult.Failure(failureMessage, apiCallResult.error.isConnectivityFailure)
            }
        }
    }

    override suspend fun getTvSeriesDetails(seriesId: Int, sessionId: String?): TvSeriesDetailsResult {
        // account_states (and thus favorite status) is only returned by TMDB
        // when a session_id accompanies the request.
        val appendToResponse = if (sessionId != null) {
            APPEND_TO_RESPONSE_CREDITS_ACCOUNT_STATES_AND_VIDEOS
        } else {
            APPEND_TO_RESPONSE_CREDITS_AND_VIDEOS
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

    override suspend fun getAirDateLabel(seriesId: Int): SeriesAirDateLabel {
        airDateLabelCache[seriesId]?.let { return it }

        val apiCallResult: ApiCallResult<TvSeriesAirDateInfoDto> = networkManager.executeApiCall(
                "GetTvSeriesAirDateInfo",
                apiCall = { -> api.getTvSeriesAirDateInfo(seriesId) })

        val label = when (apiCallResult) {
            is ApiCallResult.Success<TvSeriesAirDateInfoDto> -> seriesAirDateLabelFormatter.format(
                    status = apiCallResult.data.status,
                    firstAirDate = apiCallResult.data.firstAirDate,
                    lastAirDate = apiCallResult.data.lastAirDate
                                                                                                    )
            // Same "no date yet" fallback the formatter itself uses for a
            // missing first_air_date — a failed lookup reads no differently
            // to the user than a series TMDB hasn't dated yet.
            is ApiCallResult.Failure                          ->
                seriesAirDateLabelFormatter.format(status = null, firstAirDate = null, lastAirDate = null)
        }

        if (apiCallResult is ApiCallResult.Success<TvSeriesAirDateInfoDto>) {
            airDateLabelCache[seriesId] = label
        }
        return label
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
                TvSeriesResult.Failure(failureMessage, apiCallResult.error.isConnectivityFailure)
            }
        }
    }

    // The category endpoints cross-reference this cache to draw stars, so it
    // has to hold *every* favorite, not just the first page TMDB returns
    // (20 per page). Bounded so a surprising totalPages can't spin forever.
    override suspend fun syncFavoriteIds(accountId: Int, sessionId: String) {
        val ids = mutableListOf<Int>()
        var page = 1
        while (page <= MAX_FAVORITE_SYNC_PAGES) {
            val apiCallResult: ApiCallResult<TvSeriesListDto> = networkManager.executeApiCall(
                    "SyncTvFavoriteIds",
                    apiCall = { -> api.getFavoriteTvSeries(accountId, sessionId, page = page) })
            // Leave the existing cache alone on a mid-sync failure rather than
            // replacing it with a partial list — a half list would clear stars
            // for favorites the account really has.
            if (apiCallResult !is ApiCallResult.Success<TvSeriesListDto>) return
            ids += apiCallResult.data.tvSeries.orEmpty().mapNotNull { it.id }
            if (isLastPage(page, apiCallResult.data.totalPages ?: page)) break
            page++
        }
        tvFavoriteDao.replaceAll(ids.map { TvFavoriteEntity(it) })
        favoriteIdsCache = ids.toSet()
    }

    override suspend fun getCachedFavoriteIds(): Set<Int> = favoriteIds()

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
                            ToggleFavoriteRequestDto(mediaType = MEDIA_TYPE_TV, mediaId = tvSeriesId, favorite = isFavorite)
                                       )
                })

        return when (apiCallResult) {
            is ApiCallResult.Success<ToggleFavoriteResponseDto> -> {
                if (isFavorite) {
                    tvFavoriteDao.insert(TvFavoriteEntity(tvSeriesId))
                    favoriteIdsCache = favoriteIdsCache?.plus(tvSeriesId)
                } else {
                    tvFavoriteDao.deleteById(tvSeriesId)
                    favoriteIdsCache = favoriteIdsCache?.minus(tvSeriesId)
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
                // A missing id can't back a valid EpisodeModel, so treat it the
                // same as a failed call rather than surfacing a broken episode.
                seasonMapper.toModel(apiCallResult.data)
                        ?.let { EpisodeDetailsResult.Success(it) }
                        ?: EpisodeDetailsResult.Failure(episodeFailureMessage)
            }
            is ApiCallResult.Failure             -> {
                EpisodeDetailsResult.Failure(episodeFailureMessage)
            }
        }
    }
}
