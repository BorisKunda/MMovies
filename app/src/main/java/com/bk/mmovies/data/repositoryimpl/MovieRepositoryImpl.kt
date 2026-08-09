package com.bk.mmovies.data.repositoryimpl


import android.content.Context
import coil3.util.CoilUtils.result
import com.bk.mmovies.R
import com.bk.mmovies.data.mapper.MovieDetailsMapper
import com.bk.mmovies.data.mapper.MovieMapper
import com.bk.mmovies.data.source.local.DbManager
import com.bk.mmovies.data.source.local.db.MovieDb
import com.bk.mmovies.data.source.remote.NetworkManager
import com.bk.mmovies.data.source.remote.api.TmdbApi
import com.bk.mmovies.data.source.remote.dto.MovieDetailsDto
import com.bk.mmovies.data.source.remote.dto.MovieListDto
import com.bk.mmovies.data.source.remote.result.ApiCallResult
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.result.MovieDetailsResult
import com.bk.mmovies.domain.model.result.MoviesResult
import com.bk.mmovies.domain.repository.MovieRepository
import com.bk.mmovies.util.logDebug
import com.bk.mmovies.util.logError
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
        private val movieMapper: MovieMapper,
        private val movieDetailsMapper: MovieDetailsMapper,
        @ApplicationContext private val context: Context
                                             ) : MovieRepository {
    private val TAG = "MovieRepositoryImpl"
    private val failureMessage: String
        get() = context.getString(R.string.error_movies_load_failed)
    private val detailsFailureMessage: String
        get() = context.getString(R.string.error_movie_details_load_failed)

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
                // Not implemented yet: an empty success lets the UI show a
                // proper empty state instead of a load-failure screen.
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
                MoviesResult.Success(movieMapper.toModels(sortedMovies))
            }
            is ApiCallResult.Failure               -> {
                MoviesResult.Failure(failureMessage)
            }
       }
    }

    override suspend fun getMovieDetails(movieId: Int): MovieDetailsResult {
        val apiCallResult: ApiCallResult<MovieDetailsDto> = networkManager.executeApiCall(
                "GetMovieDetails",
                apiCall = { -> api.getMovieDetails(movieId) })

        return when (apiCallResult) {
            is ApiCallResult.Success<MovieDetailsDto> -> {
                MovieDetailsResult.Success(movieDetailsMapper.toModel(apiCallResult.data))
            }
            is ApiCallResult.Failure                  -> {
                MovieDetailsResult.Failure(detailsFailureMessage)
            }
        }
    }

}