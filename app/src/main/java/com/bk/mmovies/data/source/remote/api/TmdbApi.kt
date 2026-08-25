package com.bk.mmovies.data.source.remote.api


import com.bk.mmovies.data.source.remote.ACCOUNT_DETAILS_ENDPOINT
import com.bk.mmovies.data.source.remote.APPEND_TO_RESPONSE_CREDITS
import com.bk.mmovies.data.source.remote.AIRING_TODAY_TV_SERIES_LIST_ENDPOINT
import com.bk.mmovies.data.source.remote.AUTHENTICATION_ENDPOINT
import com.bk.mmovies.data.source.remote.DELETE_SESSION_ENDPOINT
import com.bk.mmovies.data.source.remote.DISCOVER_MOVIES_LIST_ENDPOINT
import com.bk.mmovies.data.source.remote.DISCOVER_TV_SERIES_LIST_ENDPOINT
import com.bk.mmovies.data.source.remote.FAVORITE_ENDPOINT
import com.bk.mmovies.data.source.remote.FAVORITE_MOVIES_ENDPOINT
import com.bk.mmovies.data.source.remote.FAVORITE_TV_SERIES_ENDPOINT
import com.bk.mmovies.data.source.remote.GUEST_SESSION_ID_ENDPOINT
import com.bk.mmovies.data.source.remote.LOGIN_SESSION_ID_ENDPOINT
import com.bk.mmovies.data.source.remote.LOGIN_VALIDATION_TOKEN_ENDPOINT
import com.bk.mmovies.data.source.remote.LOGIN_WITH_CREDENTIALS_ENDPOINT
import com.bk.mmovies.data.source.remote.MOVIE_ENDPOINT
import com.bk.mmovies.data.source.remote.NOW_PLAYING_MOVIES_LIST_ENDPOINT
import com.bk.mmovies.data.source.remote.PERSON_ENDPOINT
import com.bk.mmovies.data.source.remote.ON_TV_TV_SERIES_LIST_ENDPOINT
import com.bk.mmovies.data.source.remote.POPULAR_MOVIES_LIST_ENDPOINT
import com.bk.mmovies.data.source.remote.POPULAR_TV_SERIES_LIST_ENDPOINT
import com.bk.mmovies.data.source.remote.QUERY_PARAM_APPEND_TO_RESPONSE
import com.bk.mmovies.data.source.remote.QUERY_PARAM_LANGUAGE
import com.bk.mmovies.data.source.remote.QUERY_PARAM_PAGE
import com.bk.mmovies.data.source.remote.QUERY_PARAM_QUERY
import com.bk.mmovies.data.source.remote.QUERY_PARAM_SESSION_ID
import com.bk.mmovies.data.source.remote.QUERY_PARAM_SORT_BY
import com.bk.mmovies.data.source.remote.SEARCH_MULTI_ENDPOINT
import com.bk.mmovies.data.source.remote.TOP_RATED_MOVIES_LIST_ENDPOINT
import com.bk.mmovies.data.source.remote.TOP_RATED_TV_SERIES_LIST_ENDPOINT
import com.bk.mmovies.data.source.remote.TV_EPISODE_ENDPOINT
import com.bk.mmovies.data.source.remote.TV_SEASON_ENDPOINT
import com.bk.mmovies.data.source.remote.TV_SERIES_ENDPOINT
import com.bk.mmovies.data.source.remote.dto.AccountDetailsDto
import com.bk.mmovies.data.source.remote.dto.DeleteSessionDto
import com.bk.mmovies.data.source.remote.dto.DeleteSessionRequestDto
import com.bk.mmovies.data.source.remote.dto.GuestSessionIdDto
import com.bk.mmovies.data.source.remote.dto.LoginSessionIdDto
import com.bk.mmovies.data.source.remote.dto.LoginValidationTokenDto
import com.bk.mmovies.data.source.remote.dto.LoginWithCredentialsRequestDto
import com.bk.mmovies.data.source.remote.dto.MovieDetailsDto
import com.bk.mmovies.data.source.remote.dto.MovieListDto
import com.bk.mmovies.data.source.remote.dto.MultiSearchListDto
import com.bk.mmovies.data.source.remote.dto.EpisodeDto
import com.bk.mmovies.data.source.remote.dto.PersonDetailsDto
import com.bk.mmovies.data.source.remote.dto.SeasonDetailsDto
import com.bk.mmovies.data.source.remote.dto.ToggleFavoriteRequestDto
import com.bk.mmovies.data.source.remote.dto.ToggleFavoriteResponseDto
import com.bk.mmovies.data.source.remote.dto.TvSeriesDetailsDto
import com.bk.mmovies.data.source.remote.dto.TvSeriesListDto
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

    @GET(ACCOUNT_DETAILS_ENDPOINT)
    suspend fun getAccountDetails(
            @Query(QUERY_PARAM_SESSION_ID) sessionId: String
                                  ): Response<AccountDetailsDto>

    @GET(MOVIE_ENDPOINT)
    suspend fun getMovieDetails(
            @Path("movie_id") movieId: Int,
            @Query(QUERY_PARAM_APPEND_TO_RESPONSE) appendToResponse: String = APPEND_TO_RESPONSE_CREDITS,
            // Only present for a logged-in session, so account_states (favorite
            // status) is included solely when it can actually be resolved.
            @Query(QUERY_PARAM_SESSION_ID) sessionId: String? = null
                               ): Response<MovieDetailsDto>

    @POST(FAVORITE_ENDPOINT)
    suspend fun toggleFavorite(
            @Path("account_id") accountId: Int,
            @Query(QUERY_PARAM_SESSION_ID) sessionId: String,
            @Body toggleFavoriteRequestDto: ToggleFavoriteRequestDto
                              ): Response<ToggleFavoriteResponseDto>

    @GET(FAVORITE_MOVIES_ENDPOINT)
    suspend fun getFavoriteMovies(
            @Path("account_id") accountId: Int,
            @Query(QUERY_PARAM_SESSION_ID) sessionId: String,
            @Query(QUERY_PARAM_LANGUAGE) language: String = "en-US",
            @Query(QUERY_PARAM_PAGE) page: Int = 1,
            @Query(QUERY_PARAM_SORT_BY) sortBy: String = "created_at.asc"
                                  ): Response<MovieListDto>

    @GET(FAVORITE_TV_SERIES_ENDPOINT)
    suspend fun getFavoriteTvSeries(
            @Path("account_id") accountId: Int,
            @Query(QUERY_PARAM_SESSION_ID) sessionId: String,
            @Query(QUERY_PARAM_LANGUAGE) language: String = "en-US",
            @Query(QUERY_PARAM_PAGE) page: Int = 1,
            @Query(QUERY_PARAM_SORT_BY) sortBy: String = "created_at.asc"
                                    ): Response<TvSeriesListDto>


    @GET(POPULAR_MOVIES_LIST_ENDPOINT)
    suspend fun getPopularMovies(
            @Query(QUERY_PARAM_PAGE) page: Int = 1
                                 ): Response<MovieListDto>

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
    suspend fun getNowPlayingMovies(
            @Query(QUERY_PARAM_PAGE) page: Int = 1
                                    ): Response<MovieListDto>

    @GET(TOP_RATED_MOVIES_LIST_ENDPOINT)
    suspend fun getTopRatedMovies(
            @Query(QUERY_PARAM_PAGE) page: Int = 1
                                  ): Response<MovieListDto>

    @GET(POPULAR_TV_SERIES_LIST_ENDPOINT)
    suspend fun getPopularTvSeries(
            @Query(QUERY_PARAM_PAGE) page: Int = 1
                                   ): Response<TvSeriesListDto>

    @GET(AIRING_TODAY_TV_SERIES_LIST_ENDPOINT)
    suspend fun getAiringTodayTvSeries(
            @Query(QUERY_PARAM_PAGE) page: Int = 1
                                       ): Response<TvSeriesListDto>

    @GET(ON_TV_TV_SERIES_LIST_ENDPOINT)
    suspend fun getOnTvTvSeries(
            @Query(QUERY_PARAM_PAGE) page: Int = 1
                                ): Response<TvSeriesListDto>

    @GET(TOP_RATED_TV_SERIES_LIST_ENDPOINT)
    suspend fun getTopRatedTvSeries(
            @Query(QUERY_PARAM_PAGE) page: Int = 1
                                    ): Response<TvSeriesListDto>

    @GET(DISCOVER_TV_SERIES_LIST_ENDPOINT)
    suspend fun getUpcomingTvSeries(
            @Query("language") language: String = "en-US",
            @Query("with_original_language") withOriginalLanguage: String = "en",
            @Query("include_adult") includeAdult: Boolean = false,
            @Query("sort_by") sortBy: String = "popularity.desc",
            @Query("first_air_date.gte") firstAirDateGte: String,
            @Query("page") page: Int = 1
                                    ): Response<TvSeriesListDto>

    @GET(TV_SERIES_ENDPOINT)
    suspend fun getTvSeriesDetails(
            @Path("series_id") seriesId: Int,
            @Query(QUERY_PARAM_APPEND_TO_RESPONSE) appendToResponse: String = APPEND_TO_RESPONSE_CREDITS,
            // Only present for a logged-in session, so account_states (favorite
            // status) is included solely when it can actually be resolved.
            @Query(QUERY_PARAM_SESSION_ID) sessionId: String? = null
                                   ): Response<TvSeriesDetailsDto>

    @GET(TV_SEASON_ENDPOINT)
    suspend fun getSeasonDetails(
            @Path("series_id") seriesId: Int,
            @Path("season_number") seasonNumber: Int
                                 ): Response<SeasonDetailsDto>

    @GET(TV_EPISODE_ENDPOINT)
    suspend fun getEpisodeDetails(
            @Path("series_id") seriesId: Int,
            @Path("season_number") seasonNumber: Int,
            @Path("episode_number") episodeNumber: Int
                                  ): Response<EpisodeDto>

    @GET(PERSON_ENDPOINT)
    suspend fun getPersonDetails(
            @Path("person_id") personId: Int,
            @Query(QUERY_PARAM_LANGUAGE) language: String = "en-US"
                                 ): Response<PersonDetailsDto>

    @GET(SEARCH_MULTI_ENDPOINT)
    suspend fun searchMulti(
            @Query(QUERY_PARAM_QUERY) query: String,
            @Query(QUERY_PARAM_PAGE) page: Int = 1
                           ): Response<MultiSearchListDto>
}

