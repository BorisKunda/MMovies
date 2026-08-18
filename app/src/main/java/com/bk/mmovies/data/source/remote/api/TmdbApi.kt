package com.bk.mmovies.data.source.remote.api


import com.bk.mmovies.data.source.remote.AUTHENTICATION_ENDPOINT
import com.bk.mmovies.data.source.remote.DELETE_SESSION_ENDPOINT
import com.bk.mmovies.data.source.remote.DISCOVER_MOVIES_LIST_ENDPOINT
import com.bk.mmovies.data.source.remote.GUEST_SESSION_ID_ENDPOINT
import com.bk.mmovies.data.source.remote.LOGIN_SESSION_ID_ENDPOINT
import com.bk.mmovies.data.source.remote.LOGIN_VALIDATION_TOKEN_ENDPOINT
import com.bk.mmovies.data.source.remote.LOGIN_WITH_CREDENTIALS_ENDPOINT
import com.bk.mmovies.data.source.remote.MOVIE_ENDPOINT
import com.bk.mmovies.data.source.remote.NOW_PLAYING_MOVIES_LIST_ENDPOINT
import com.bk.mmovies.data.source.remote.POPULAR_MOVIES_LIST_ENDPOINT
import com.bk.mmovies.data.source.remote.TOP_RATED_MOVIES_LIST_ENDPOINT
import com.bk.mmovies.data.source.remote.dto.DeleteSessionDto
import com.bk.mmovies.data.source.remote.dto.DeleteSessionRequestDto
import com.bk.mmovies.data.source.remote.dto.GuestSessionIdDto
import com.bk.mmovies.data.source.remote.dto.LoginSessionIdDto
import com.bk.mmovies.data.source.remote.dto.LoginValidationTokenDto
import com.bk.mmovies.data.source.remote.dto.LoginWithCredentialsRequestDto
import com.bk.mmovies.data.source.remote.dto.MovieDetailsDto
import com.bk.mmovies.data.source.remote.dto.MovieListDto
import com.bk.mmovies.data.source.remote.dto.V3TokenValidityDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {
    @GET(AUTHENTICATION_ENDPOINT)
    suspend fun getIsApiV3TokenValid(): Response<V3TokenValidityDto>

    @GET(LOGIN_VALIDATION_TOKEN_ENDPOINT)
    suspend fun loginStep1GetValidationToken(): Response<LoginValidationTokenDto>

    @POST(LOGIN_WITH_CREDENTIALS_ENDPOINT)
    suspend fun loginStep2WithUsernameAndPassword(
            @Body loginWithCredentialsRequestDto: LoginWithCredentialsRequestDto
                                              ): Response<LoginValidationTokenDto>

    @GET(LOGIN_SESSION_ID_ENDPOINT)
    suspend fun loginStep3GetSessionId(
            @Query("request_token") loginValidationToken: String
                                       ): Response<LoginSessionIdDto>

    @GET(GUEST_SESSION_ID_ENDPOINT)
    suspend fun getGuestSessionId(): Response<GuestSessionIdDto>

    @HTTP(method = "DELETE", path = DELETE_SESSION_ENDPOINT, hasBody = true)
    suspend fun deleteSession(@Body deleteSessionRequestDto: DeleteSessionRequestDto): Response<DeleteSessionDto>

    @GET(MOVIE_ENDPOINT)
    suspend fun getMovieDetails(
            @Path("movie_id") movieId: Int
                               ): Response<MovieDetailsDto>


    @GET(POPULAR_MOVIES_LIST_ENDPOINT)
    suspend fun getPopularMovies(): Response<MovieListDto>

    @GET(DISCOVER_MOVIES_LIST_ENDPOINT)
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

