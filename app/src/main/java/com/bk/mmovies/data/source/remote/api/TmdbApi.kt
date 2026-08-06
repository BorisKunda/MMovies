package com.bk.mmovies.data.source.remote.api


import com.bk.mmovies.data.source.remote.DISCOVER_MOVIES_ENDPOINT
import com.bk.mmovies.data.source.remote.MOVIE_ENDPOINT
import com.bk.mmovies.data.source.remote.NOW_PLAYING_MOVIES_LIST_ENDPOINT
import com.bk.mmovies.data.source.remote.POPULAR_MOVIES_LIST_ENDPOINT
import com.bk.mmovies.data.source.remote.TOKEN_VALIDITY_ENDPOINT
import com.bk.mmovies.data.source.remote.TOP_RATED_MOVIES_LIST_ENDPOINT
import com.bk.mmovies.data.source.remote.dto.MovieDetailsDto
import com.bk.mmovies.data.source.remote.dto.MovieListDto
import com.bk.mmovies.data.source.remote.dto.TokenValidityDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {
    @GET(TOKEN_VALIDITY_ENDPOINT)
    suspend fun getIsApiV3TokenValid(): Response<TokenValidityDto>

    @GET(MOVIE_ENDPOINT)
    suspend fun getMovieDetails(
            @Path("movie_id") movieId: Int
                               ): Response<MovieDetailsDto>


    @GET(POPULAR_MOVIES_LIST_ENDPOINT)
    suspend fun getPopularMovies(): Response<MovieListDto>

    @GET(DISCOVER_MOVIES_ENDPOINT)
    suspend fun getUpcomingMovies(
            @Query("language") language: String = "en-US",
            @Query("with_original_language") withOriginalLanguage: String = "en",
            @Query("include_adult") includeAdult: Boolean = false,
            @Query("include_video") includeVideo: Boolean = false,
            @Query("sort_by") sortBy: String = "popularity.desc",
            @Query("primary_release_date.gte") primaryReleaseDateGte: String,
            @Query("with_release_type") withReleaseType: String = "2|3|4",
            @Query("page") page: Int = 1
                                 ): Response<MovieListDto>

    @GET(NOW_PLAYING_MOVIES_LIST_ENDPOINT)
    suspend fun getNowPlayingMovies(): Response<MovieListDto>

    @GET(TOP_RATED_MOVIES_LIST_ENDPOINT)
    suspend fun getTopRatedMovies(): Response<MovieListDto>
}

