package com.bk.mmovies.data.repositoryimpl


import android.content.Context
import com.bk.mmovies.R
import com.bk.mmovies.data.mapper.MovieDetailsMapper
import com.bk.mmovies.data.mapper.MovieMapper
import com.bk.mmovies.data.source.local.DbManager
import com.bk.mmovies.data.source.local.dao.FavoriteDao
import com.bk.mmovies.data.source.local.db.MovieDb
import com.bk.mmovies.data.source.local.entity.FavoriteEntity
import com.bk.mmovies.data.source.remote.APPEND_TO_RESPONSE_CREDITS
import com.bk.mmovies.data.source.remote.APPEND_TO_RESPONSE_CREDITS_AND_ACCOUNT_STATES
import com.bk.mmovies.data.source.remote.NetworkManager
import com.bk.mmovies.data.source.remote.api.TmdbApi
import com.bk.mmovies.data.source.remote.dto.MovieDetailsDto
import com.bk.mmovies.data.source.remote.dto.MovieListDto
import com.bk.mmovies.data.source.remote.dto.ToggleFavoriteRequestDto
import com.bk.mmovies.data.source.remote.dto.ToggleFavoriteResponseDto
import com.bk.mmovies.data.source.remote.result.ApiCallResult
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.result.MovieDetailsResult
import com.bk.mmovies.domain.model.result.MoviesResult
import com.bk.mmovies.domain.model.result.ToggleFavoriteResult
import com.bk.mmovies.domain.repository.MovieRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
        private val api: TmdbApi,
        private val db: MovieDb,
        private val networkManager: NetworkManager,
        private val dbManager: DbManager,
        private val favoriteDao: FavoriteDao,
        private val movieMapper: MovieMapper,
        private val movieDetailsMapper: MovieDetailsMapper,
        @ApplicationContext private val context: Context
                                             ) : MovieRepository {
    private val TAG = "MovieRepositoryImpl"
    private val failureMessage: String
        get() = context.getString(R.string.error_movies_load_failed)
    private val detailsFailureMessage: String
        get() = context.getString(R.string.error_movie_details_load_failed)
    private val favoriteToggleFailureMessage: String
        get() = context.getString(R.string.error_favorite_toggle_failed)

    private fun tomorrowDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        return SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(calendar.time)
    }

    override suspend fun getMoviesByCategory(category: MovieCategory): MoviesResult {
        var apiCallResult: ApiCallResult<MovieListDto>? = null
        when (category) {
            MovieCategory.PopularMovieCategory -> {
                apiCallResult = networkManager.executeApiCall(
                        "GetPopularMovies",
                        apiCall = { -> api.getPopularMovies() })
            }

            MovieCategory.UpcomingMovieCategory -> {
                apiCallResult = networkManager.executeApiCall(
                        "GetUpcomingMovies",
                        apiCall = { -> api.getUpcomingMovies(primaryReleaseDateGte = tomorrowDate()) })
            }

            MovieCategory.NowPlayingMovieCategory -> {
                apiCallResult = networkManager.executeApiCall(
                        "GetNowPlayingMovies",
                        apiCall = { -> api.getNowPlayingMovies() })
            }

            MovieCategory.TopRatedMovieCategory -> {
                apiCallResult = networkManager.executeApiCall(
                        "GetTopRatedMovies",
                        apiCall = { -> api.getTopRatedMovies() })
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
                val favoriteIds = favoriteDao.getAllFavoriteIds().toSet()
                val models = movieMapper.toModels(sortedMovies).map {
                    it.copy(isFavorite = favoriteIds.contains(it.id))
                }
                MoviesResult.Success(models)
            }
            is ApiCallResult.Failure               -> {
                MoviesResult.Failure(failureMessage)
            }
       }
    }

    override suspend fun getMovieDetails(movieId: Int, sessionId: String?): MovieDetailsResult {
        // account_states (and thus favorite status) is only returned by TMDB
        // when a session_id accompanies the request.
        val appendToResponse = if (sessionId != null) {
            APPEND_TO_RESPONSE_CREDITS_AND_ACCOUNT_STATES
        } else {
            APPEND_TO_RESPONSE_CREDITS
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

    override suspend fun getFavoriteMovies(accountId: Int, sessionId: String): MoviesResult {
        val apiCallResult: ApiCallResult<MovieListDto> = networkManager.executeApiCall(
                "GetFavoriteMovies",
                apiCall = { -> api.getFavoriteMovies(accountId, sessionId) })

        return when (apiCallResult) {
            is ApiCallResult.Success<MovieListDto> -> {
                // These results are the favorites list itself, so every entry
                // is a favorite by definition regardless of what the mapper's
                // default would otherwise produce.
                val movies = movieMapper.toModels(apiCallResult.data.movies.orEmpty())
                        .map { it.copy(isFavorite = true) }
                MoviesResult.Success(movies)
            }
            is ApiCallResult.Failure               -> {
                MoviesResult.Failure(failureMessage)
            }
        }
    }

    override suspend fun syncFavoriteIds(accountId: Int, sessionId: String) {
        val apiCallResult: ApiCallResult<MovieListDto> = networkManager.executeApiCall(
                "SyncFavoriteIds",
                apiCall = { -> api.getFavoriteMovies(accountId, sessionId) })

        if (apiCallResult is ApiCallResult.Success<MovieListDto>) {
            val ids = apiCallResult.data.movies.orEmpty().mapNotNull { it.id }
            favoriteDao.clearAll()
            favoriteDao.insertAll(ids.map { FavoriteEntity(it) })
        }
    }

    override suspend fun getCachedFavoriteIds(): Set<Int> {
        return favoriteDao.getAllFavoriteIds().toSet()
    }

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
                } else {
                    favoriteDao.deleteById(movieId)
                }
                ToggleFavoriteResult.Success
            }
            is ApiCallResult.Failure                            -> {
                ToggleFavoriteResult.Failure(favoriteToggleFailureMessage)
            }
        }
    }

}