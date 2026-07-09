package com.bk.mmovies.data.source.remote.api


import com.bk.mmovies.data.source.remote.NOW_PLAYING_MOVIES_LIST_ENDPOINT
import com.bk.mmovies.data.source.remote.POPULAR_MOVIES_LIST_ENDPOINT
import com.bk.mmovies.data.source.remote.TOKEN_VALIDITY_ENDPOINT
import com.bk.mmovies.data.source.remote.TOP_RATED_MOVIES_LIST_ENDPOINT
import com.bk.mmovies.data.source.remote.UPCOMING_MOVIES_LIST_ENDPOINT
import com.bk.mmovies.data.source.remote.dto.MovieListDto
import com.bk.mmovies.data.source.remote.dto.TokenValidityDto
import retrofit2.Response
import retrofit2.http.GET

interface TmdbApi {
    @GET(TOKEN_VALIDITY_ENDPOINT)
    suspend fun getIsApiV3TokenValid(): Response<TokenValidityDto>

    @GET(POPULAR_MOVIES_LIST_ENDPOINT)
    suspend fun getPopularMovies(): Response<MovieListDto>

    @GET(UPCOMING_MOVIES_LIST_ENDPOINT)
    suspend fun getUpcomingMovies(): Response<MovieListDto>

    @GET(NOW_PLAYING_MOVIES_LIST_ENDPOINT)
    suspend fun getNowPlayingMovies(): Response<MovieListDto>

    @GET(TOP_RATED_MOVIES_LIST_ENDPOINT)
    suspend fun getTopRatedMovies(): Response<MovieListDto>
}

