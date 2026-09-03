package com.bk.mmovies.data.repositoryimpl


import android.content.Context
import com.bk.mmovies.R
import com.bk.mmovies.data.mapper.MovieDetailsMapper
import com.bk.mmovies.data.mapper.MovieMapper
import com.bk.mmovies.data.source.local.dao.FavoriteDao
import com.bk.mmovies.data.source.local.entity.FavoriteEntity
import com.bk.mmovies.data.source.remote.APPEND_TO_RESPONSE_CREDITS_AND_VIDEOS
import com.bk.mmovies.data.source.remote.APPEND_TO_RESPONSE_CREDITS_ACCOUNT_STATES_AND_VIDEOS
import com.bk.mmovies.data.source.remote.NetworkManager
import com.bk.mmovies.data.source.remote.api.TmdbApi
import com.bk.mmovies.data.source.remote.dto.MovieDetailsDto
import com.bk.mmovies.data.source.remote.dto.MovieListDto
import com.bk.mmovies.data.source.remote.dto.ToggleFavoriteRequestDto
import com.bk.mmovies.data.source.remote.dto.ToggleFavoriteResponseDto
import com.bk.mmovies.data.source.remote.result.ApiCallResult
import com.bk.mmovies.data.source.remote.result.isConnectivityFailure
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.isLastPage
import com.bk.mmovies.domain.model.result.MovieDetailsResult
import com.bk.mmovies.domain.model.result.MoviesResult
import com.bk.mmovies.domain.model.result.ToggleFavoriteResult
import com.bk.mmovies.domain.repository.MovieRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

// TMDB returns 20 favorites per page, so this covers 1000 favorited movies.
private const val MAX_FAVORITE_SYNC_PAGES = 50

class MovieRepositoryImpl @Inject constructor(
        private val api: TmdbApi,
        private val networkManager: NetworkManager,
        private val favoriteDao: FavoriteDao,
        private val movieMapper: MovieMapper,
        private val movieDetailsMapper: MovieDetailsMapper,
        @ApplicationContext private val context: Context
                                             ) : MovieRepository {
    private val failureMessage: String
        get() = context.getString(R.string.error_movies_load_failed)
    private val detailsFailureMessage: String
        get() = context.getString(R.string.error_movie_details_load_failed)
    private val favoriteToggleFailureMessage: String
        get() = context.getString(R.string.error_favorite_toggle_failed)

    // getMoviesByCategory cross-references this on every single page load
    // (see below) to mark stars; without an in-memory copy that's a Room
    // query per page fetch even though syncFavoriteIds/toggleFavorite are the
    // only things that ever actually change it. Null means "not loaded yet",
    // not "no favorites" — the next read repopulates it from Room.
    private var favoriteIdsCache: Set<Int>? = null

    private suspend fun favoriteIds(): Set<Int> =
            favoriteIdsCache ?: favoriteDao.getAllFavoriteIds().toSet().also { favoriteIdsCache = it }

    override suspend fun getMoviesByCategory(category: MovieCategory, page: Int): MoviesResult {
        var apiCallResult: ApiCallResult<MovieListDto>? = null
        when (category) {
            MovieCategory.PopularMovieCategory -> {
                apiCallResult = networkManager.executeApiCall(
                        "GetPopularMovies",
                        apiCall = { -> api.getPopularMovies(page = page) })
            }

            MovieCategory.UpcomingMovieCategory -> {
                apiCallResult = networkManager.executeApiCall(
                        "GetUpcomingMovies",
                        apiCall = { -> api.getUpcomingMovies(primaryReleaseDateGte = tomorrowDate(), page = page) })
            }

            MovieCategory.NowPlayingMovieCategory -> {
                apiCallResult = networkManager.executeApiCall(
                        "GetNowPlayingMovies",
                        apiCall = { -> api.getNowPlayingMovies(page = page) })
            }

            MovieCategory.TopRatedMovieCategory -> {
                apiCallResult = networkManager.executeApiCall(
                        "GetTopRatedMovies",
                        apiCall = { -> api.getTopRatedMovies(page = page) })
            }

            MovieCategory.FavoritesMovieCategory -> {
                // Favorites need an accountId/sessionId the category-only
                // signature doesn't carry, so the ViewModel routes this
                // category to getFavoriteMovies() instead and never reaches
                // this branch. Kept only so the `when` stays exhaustive.
                return MoviesResult.Success(emptyList())
            }
        }
       return  when (apiCallResult) {
            is ApiCallResult.Success<MovieListDto> -> {
                // An empty page is a successful response, not a failure — the
                // caller decides how to present "nothing here".
                val movies = apiCallResult.data.movies.orEmpty()
                val sortedMovies = if (category == MovieCategory.UpcomingMovieCategory) {
                    movies.sortedBy { it.releaseDate }
                } else {
                    movies
                }
                // The category endpoints don't return per-item favorite status,
                // so cross-reference against the ids synced into the local
                // cache (see syncFavoriteIds) to mark stars correctly.
                val favoriteIds = favoriteIds()
                val models = movieMapper.toModels(sortedMovies).map {
                    it.copy(isFavorite = favoriteIds.contains(it.id))
                }
                MoviesResult.Success(models, page, apiCallResult.data.totalPages ?: page)
            }
            is ApiCallResult.Failure               -> {
                MoviesResult.Failure(failureMessage, apiCallResult.error.isConnectivityFailure)
            }
       }
    }

    override suspend fun getMovieDetails(movieId: Int, sessionId: String?): MovieDetailsResult {
        // account_states (and thus favorite status) is only returned by TMDB
        // when a session_id accompanies the request.
        val appendToResponse = if (sessionId != null) {
            APPEND_TO_RESPONSE_CREDITS_ACCOUNT_STATES_AND_VIDEOS
        } else {
            APPEND_TO_RESPONSE_CREDITS_AND_VIDEOS
        }
        val apiCallResult: ApiCallResult<MovieDetailsDto> = networkManager.executeApiCall(
                "GetMovieDetails",
                apiCall = { -> api.getMovieDetails(movieId, appendToResponse, sessionId) })

        return when (apiCallResult) {
            is ApiCallResult.Success<MovieDetailsDto> -> {
                MovieDetailsResult.Success(movieDetailsMapper.toModel(apiCallResult.data))
            }
            is ApiCallResult.Failure                  -> {
                MovieDetailsResult.Failure(detailsFailureMessage)
            }
        }
    }

    override suspend fun getFavoriteMovies(accountId: Int, sessionId: String, page: Int): MoviesResult {
        val apiCallResult: ApiCallResult<MovieListDto> = networkManager.executeApiCall(
                "GetFavoriteMovies",
                apiCall = { -> api.getFavoriteMovies(accountId, sessionId, page = page) })

        return when (apiCallResult) {
            is ApiCallResult.Success<MovieListDto> -> {
                // These results are the favorites list itself, so every entry
                // is a favorite by definition regardless of what the mapper's
                // default would otherwise produce.
                val movies = movieMapper.toModels(apiCallResult.data.movies.orEmpty())
                        .map { it.copy(isFavorite = true) }
                MoviesResult.Success(movies, page, apiCallResult.data.totalPages ?: page)
            }
            is ApiCallResult.Failure               -> {
                MoviesResult.Failure(failureMessage, apiCallResult.error.isConnectivityFailure)
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
            val apiCallResult: ApiCallResult<MovieListDto> = networkManager.executeApiCall(
                    "SyncFavoriteIds",
                    apiCall = { -> api.getFavoriteMovies(accountId, sessionId, page = page) })
            // Leave the existing cache alone on a mid-sync failure rather than
            // replacing it with a partial list — a half list would clear stars
            // for favorites the account really has.
            if (apiCallResult !is ApiCallResult.Success<MovieListDto>) return
            ids += apiCallResult.data.movies.orEmpty().mapNotNull { it.id }
            if (isLastPage(page, apiCallResult.data.totalPages ?: page)) break
            page++
        }
        favoriteDao.replaceAll(ids.map { FavoriteEntity(it) })
        favoriteIdsCache = ids.toSet()
    }

    override suspend fun getCachedFavoriteIds(): Set<Int> = favoriteIds()

    override suspend fun toggleFavorite(
            accountId: Int,
            sessionId: String,
            movieId: Int,
            isFavorite: Boolean
                                        ): ToggleFavoriteResult {
        val apiCallResult: ApiCallResult<ToggleFavoriteResponseDto> = networkManager.executeApiCall(
                "ToggleFavorite",
                apiCall = {
                    ->
                    api.toggleFavorite(
                            accountId,
                            sessionId,
                            ToggleFavoriteRequestDto(mediaId = movieId, favorite = isFavorite)
                                       )
                })

        return when (apiCallResult) {
            is ApiCallResult.Success<ToggleFavoriteResponseDto> -> {
                if (isFavorite) {
                    favoriteDao.insert(FavoriteEntity(movieId))
                    favoriteIdsCache = favoriteIdsCache?.plus(movieId)
                } else {
                    favoriteDao.deleteById(movieId)
                    favoriteIdsCache = favoriteIdsCache?.minus(movieId)
                }
                ToggleFavoriteResult.Success
            }
            is ApiCallResult.Failure                            -> {
                ToggleFavoriteResult.Failure(favoriteToggleFailureMessage)
            }
        }
    }

}
