package com.bk.mmovies.network

import com.bk.mmovies.data.source.remote.APPEND_TO_RESPONSE_CREDITS
import com.bk.mmovies.data.source.remote.QUERY_PARAM_APPEND_TO_RESPONSE
import com.bk.mmovies.data.source.remote.QUERY_PARAM_PAGE
import com.bk.mmovies.data.source.remote.QUERY_PARAM_QUERY
import com.bk.mmovies.data.source.remote.QUERY_PARAM_SESSION_ID
import com.bk.mmovies.data.source.remote.QUERY_PARAM_SORT_BY
import com.bk.mmovies.data.source.remote.api.TmdbApi
import com.bk.mmovies.data.source.remote.dto.DeleteSessionRequestDto
import com.bk.mmovies.data.source.remote.dto.LoginWithCredentialsRequestDto
import com.bk.mmovies.data.source.remote.dto.ToggleFavoriteRequestDto
import com.bk.mmovies.util.logDebug
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.HeldCertificate
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TmdbApiTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var okHttpClient: OkHttpClient
    private lateinit var certificates: HandshakeCertificates
    private lateinit var retrofit: Retrofit
    private lateinit var api: TmdbApi
    private val TAG = "TmdbApiTest"

    private val dummySessionId = "dummy_session_id"
    private val dummyAccountId = 5

    @Before
    fun setup() {
        setMockWebServer()
        setOkHttp()
        setRetrofit()
        api = retrofit.create(TmdbApi::class.java)
    }

    private fun setMockWebServer() {
        val heldCertificate = HeldCertificate.Builder()
                .commonName("localhost")
                .addSubjectAlternativeName("localhost")
                .build()
        certificates = HandshakeCertificates.Builder()
                .heldCertificate(heldCertificate)
                .addTrustedCertificate(heldCertificate.certificate)
                .build()
        mockWebServer = MockWebServer()
        mockWebServer.protocols = listOf(Protocol.HTTP_1_1)
        mockWebServer.useHttps(
                certificates.sslSocketFactory(),
                false
                              )
        mockWebServer.start()
    }

    private fun setOkHttp() {
        okHttpClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor(logger = {
                    logDebug(
                            TAG,
                            message = it
                            )
                }).apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .sslSocketFactory(
                        certificates.sslSocketFactory(),
                        certificates.trustManager
                                 )
                .build()
    }

    private fun setRetrofit() {
        retrofit = Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    private fun enqueueResponse(
            code: Int = 200,
            body: String = MOCK_EMPTY_JSON_OBJECT_RESPONSE
                               ) {
        mockWebServer.enqueue(
                MockResponse().apply {
                    setResponseCode(code).setBody(body.trimIndent())
                }
                             )
    }

    private fun assertMethodAndPath(
            recordedRequest: RecordedRequest,
            expectedMethod: String,
            expectedPath: String
                                   ) {
        assertEquals(
                expectedMethod,
                recordedRequest.method
                    )
        assertEquals(
                expectedPath,
                recordedRequest.requestUrl!!.encodedPath
                    )
    }

    @Test
    fun testGetIsApiV3TokenValidUsesExpectedPath() {
        enqueueResponse(body = MOCK_GET_IS_TOKEN_VALID_TRUE_RESPONSE)
        runBlocking {
            api.getIsApiV3TokenValid()
            assertMethodAndPath(
                    mockWebServer.takeRequest(),
                    "GET",
                    "/authentication"
                               )
        }
    }

    @Test
    fun testLoginStep1ParsesValidationToken() {
        enqueueResponse(body = MOCK_LOGIN_VALIDATION_TOKEN_RESPONSE)
        runBlocking {
            val response = api.loginStep1GetValidationToken()

            assertMethodAndPath(
                    mockWebServer.takeRequest(),
                    "GET",
                    "/authentication/token/new"
                               )
            assertTrue(response.isSuccessful)
            assertEquals(
                    "dummy_request_token",
                    response.body()?.loginValidationToken
                        )
        }
    }

    @Test
    fun testLoginStep2PostsCredentialsAsJson() {
        enqueueResponse(body = MOCK_LOGIN_VALIDATION_TOKEN_RESPONSE)
        runBlocking {
            api.loginStep2WithUsernameAndPassword(
                    LoginWithCredentialsRequestDto(
                            username = "dummy_user",
                            password = "dummy_password",
                            loginValidationToken = "dummy_request_token"
                                                  )
                                                 )

            val recordedRequest = mockWebServer.takeRequest()
            assertMethodAndPath(
                    recordedRequest,
                    "POST",
                    "/authentication/token/validate_with_login"
                               )
            val sentBody = recordedRequest.body.readUtf8()
            assertTrue(sentBody.contains("\"username\":\"dummy_user\""))
            assertTrue(sentBody.contains("\"password\":\"dummy_password\""))
            assertTrue(sentBody.contains("\"request_token\":\"dummy_request_token\""))
        }
    }

    @Test
    fun testLoginStep3SendsRequestTokenAndParsesSessionId() {
        enqueueResponse(body = MOCK_LOGIN_SESSION_ID_RESPONSE)
        runBlocking {
            val response = api.loginStep3GetSessionId("dummy_request_token")

            val recordedRequest = mockWebServer.takeRequest()
            assertMethodAndPath(
                    recordedRequest,
                    "GET",
                    "/authentication/session/new"
                               )
            assertEquals(
                    "dummy_request_token",
                    recordedRequest.requestUrl!!.queryParameter("request_token")
                        )
            assertEquals(
                    dummySessionId,
                    response.body()?.sessionId
                        )
        }
    }

    @Test
    fun testGetGuestSessionIdParsesGuestSession() {
        enqueueResponse(body = MOCK_GUEST_SESSION_ID_RESPONSE)
        runBlocking {
            val response = api.getGuestSessionId()

            assertMethodAndPath(
                    mockWebServer.takeRequest(),
                    "GET",
                    "/authentication/guest_session/new"
                               )
            assertEquals(
                    "dummy_guest_session_id",
                    response.body()?.guestSessionId
                        )
        }
    }

    @Test
    fun testDeleteSessionSendsDeleteWithJsonBody() {
        enqueueResponse(body = MOCK_DELETE_SESSION_RESPONSE)
        runBlocking {
            val response = api.deleteSession(DeleteSessionRequestDto(dummySessionId))

            val recordedRequest = mockWebServer.takeRequest()
            assertMethodAndPath(
                    recordedRequest,
                    "DELETE",
                    "/authentication/session"
                               )
            assertTrue(recordedRequest.body.readUtf8().contains("\"session_id\":\"$dummySessionId\""))
            assertEquals(
                    true,
                    response.body()?.success
                        )
        }
    }

    @Test
    fun testGetAccountDetailsSendsSessionId() {
        enqueueResponse()
        runBlocking {
            api.getAccountDetails(dummySessionId)

            val recordedRequest = mockWebServer.takeRequest()
            assertMethodAndPath(
                    recordedRequest,
                    "GET",
                    "/account"
                               )
            assertEquals(
                    dummySessionId,
                    recordedRequest.requestUrl!!.queryParameter(QUERY_PARAM_SESSION_ID)
                        )
        }
    }

    @Test
    fun testGetAccountDetailsFailureResponse() {
        enqueueResponse(
                401,
                MOCK_TMDB_INVALID_SESSION_RESPONSE
                       )
        runBlocking {
            val response = api.getAccountDetails(dummySessionId)

            assertFalse(response.isSuccessful)
            assertEquals(
                    401,
                    response.code()
                        )
            assertNull(response.body())
            assertTrue(response.errorBody()?.string()?.contains("Authentication failed") == true)
        }
    }

    @Test
    fun testGetPopularMoviesSendsPageAndParsesMovies() {
        enqueueResponse(body = MOCK_GET_POPULAR_MOVIES_RESPONSE)
        runBlocking {
            val response: Response<*> = api.getPopularMovies(page = 2)

            val recordedRequest = mockWebServer.takeRequest()
            assertMethodAndPath(
                    recordedRequest,
                    "GET",
                    "/movie/popular"
                               )
            assertEquals(
                    "2",
                    recordedRequest.requestUrl!!.queryParameter(QUERY_PARAM_PAGE)
                        )
            assertTrue(response.isSuccessful)
        }
    }

    @Test
    fun testGetPopularMoviesDefaultsToFirstPage() {
        enqueueResponse(body = MOCK_GET_POPULAR_MOVIES_RESPONSE)
        runBlocking {
            val response = api.getPopularMovies()

            val requestUrl = mockWebServer.takeRequest().requestUrl!!
            assertEquals(
                    "1",
                    requestUrl.queryParameter(QUERY_PARAM_PAGE)
                        )
            val movies = response.body()?.movies
            assertNotNull(movies)
            assertEquals(
                    "Thrash",
                    movies?.first()?.title
                        )
            assertEquals(
                    1290417,
                    movies?.first()?.id
                        )
        }
    }

    @Test
    fun testMovieListEndpointsUseExpectedPaths() {
        val movieListCalls: List<Pair<String, suspend () -> Unit>> = listOf(
                "/movie/now_playing" to { api.getNowPlayingMovies(); Unit },
                "/movie/top_rated" to { api.getTopRatedMovies(); Unit }
                                                                           )
        runBlocking {
            movieListCalls.forEach { (expectedPath, call) ->
                enqueueResponse()
                call()
                assertMethodAndPath(
                        mockWebServer.takeRequest(),
                        "GET",
                        expectedPath
                                   )
            }
        }
    }

    @Test
    fun testTvSeriesListEndpointsUseExpectedPaths() {
        val tvSeriesListCalls: List<Pair<String, suspend () -> Unit>> = listOf(
                "/tv/popular" to { api.getPopularTvSeries(); Unit },
                "/tv/airing_today" to { api.getAiringTodayTvSeries(); Unit },
                "/tv/on_the_air" to { api.getOnTvTvSeries(); Unit },
                "/tv/top_rated" to { api.getTopRatedTvSeries(); Unit }
                                                                              )
        runBlocking {
            tvSeriesListCalls.forEach { (expectedPath, call) ->
                enqueueResponse()
                call()
                assertMethodAndPath(
                        mockWebServer.takeRequest(),
                        "GET",
                        expectedPath
                                   )
            }
        }
    }

    @Test
    fun testGetUpcomingMoviesSendsDiscoverQueryParams() {
        enqueueResponse()
        runBlocking {
            api.getUpcomingMovies(primaryReleaseDateGte = "2026-11-01")

            val recordedRequest = mockWebServer.takeRequest()
            val requestUrl = recordedRequest.requestUrl!!
            assertMethodAndPath(
                    recordedRequest,
                    "GET",
                    "/discover/movie"
                               )
            assertEquals(
                    "en",
                    requestUrl.queryParameter("with_original_language")
                        )
            assertEquals(
                    "false",
                    requestUrl.queryParameter("include_adult")
                        )
            assertEquals(
                    "popularity.desc",
                    requestUrl.queryParameter(QUERY_PARAM_SORT_BY)
                        )
            assertEquals(
                    "2026-11-01",
                    requestUrl.queryParameter("primary_release_date.gte")
                        )
            assertEquals(
                    "2|3|4",
                    requestUrl.queryParameter("with_release_type")
                        )
            assertNull(requestUrl.queryParameter("with_runtime.gte"))
            assertNull(requestUrl.queryParameter("with_runtime.lte"))
        }
    }

    @Test
    fun testGetUpcomingMoviesSendsRuntimeFilterWhenGiven() {
        enqueueResponse()
        runBlocking {
            api.getUpcomingMovies(
                    primaryReleaseDateGte = "2026-11-01",
                    withRuntimeGte = 60,
                    withRuntimeLte = 200
                                 )

            val requestUrl = mockWebServer.takeRequest().requestUrl!!
            assertEquals(
                    "60",
                    requestUrl.queryParameter("with_runtime.gte")
                        )
            assertEquals(
                    "200",
                    requestUrl.queryParameter("with_runtime.lte")
                        )
        }
    }

    @Test
    fun testGetUpcomingTvSeriesSendsDiscoverQueryParams() {
        enqueueResponse()
        runBlocking {
            api.getUpcomingTvSeries(firstAirDateGte = "2026-11-01")

            val recordedRequest = mockWebServer.takeRequest()
            val requestUrl = recordedRequest.requestUrl!!
            assertMethodAndPath(
                    recordedRequest,
                    "GET",
                    "/discover/tv"
                               )
            assertEquals(
                    "2026-11-01",
                    requestUrl.queryParameter("first_air_date.gte")
                        )
            assertEquals(
                    "popularity.desc",
                    requestUrl.queryParameter(QUERY_PARAM_SORT_BY)
                        )
        }
    }

    @Test
    fun testGetMovieDetailsRequestsCreditsAndOmitsSessionIdByDefault() {
        enqueueResponse(body = MOCK_MATRIX_JSON_OBJ)
        runBlocking {
            val response = api.getMovieDetails(603)

            val recordedRequest = mockWebServer.takeRequest()
            val requestUrl = recordedRequest.requestUrl!!
            assertMethodAndPath(
                    recordedRequest,
                    "GET",
                    "/movie/603"
                               )
            assertEquals(
                    APPEND_TO_RESPONSE_CREDITS,
                    requestUrl.queryParameter(QUERY_PARAM_APPEND_TO_RESPONSE)
                        )
            assertNull(requestUrl.queryParameter(QUERY_PARAM_SESSION_ID))
            assertTrue(response.isSuccessful)
            assertNotNull(response.body())
        }
    }

    @Test
    fun testGetMovieDetailsSendsSessionIdWhenGiven() {
        enqueueResponse()
        runBlocking {
            api.getMovieDetails(
                    movieId = 603,
                    sessionId = dummySessionId
                               )

            assertEquals(
                    dummySessionId,
                    mockWebServer.takeRequest().requestUrl!!.queryParameter(QUERY_PARAM_SESSION_ID)
                        )
        }
    }

    @Test
    fun testGetTvSeriesDetailsRequestsCreditsAndOmitsSessionIdByDefault() {
        enqueueResponse()
        runBlocking {
            api.getTvSeriesDetails(1399)

            val recordedRequest = mockWebServer.takeRequest()
            val requestUrl = recordedRequest.requestUrl!!
            assertMethodAndPath(
                    recordedRequest,
                    "GET",
                    "/tv/1399"
                               )
            assertEquals(
                    APPEND_TO_RESPONSE_CREDITS,
                    requestUrl.queryParameter(QUERY_PARAM_APPEND_TO_RESPONSE)
                        )
            assertNull(requestUrl.queryParameter(QUERY_PARAM_SESSION_ID))
        }
    }

    @Test
    fun testGetTvSeriesAirDateInfoUsesSeriesPath() {
        enqueueResponse()
        runBlocking {
            api.getTvSeriesAirDateInfo(1399)

            assertMethodAndPath(
                    mockWebServer.takeRequest(),
                    "GET",
                    "/tv/1399"
                               )
        }
    }

    @Test
    fun testGetSeasonDetailsUsesSeriesAndSeasonInPath() {
        enqueueResponse()
        runBlocking {
            api.getSeasonDetails(
                    seriesId = 1399,
                    seasonNumber = 2
                                )

            assertMethodAndPath(
                    mockWebServer.takeRequest(),
                    "GET",
                    "/tv/1399/season/2"
                               )
        }
    }

    @Test
    fun testGetEpisodeDetailsUsesSeriesSeasonAndEpisodeInPath() {
        enqueueResponse()
        runBlocking {
            api.getEpisodeDetails(
                    seriesId = 1399,
                    seasonNumber = 2,
                    episodeNumber = 5
                                 )

            assertMethodAndPath(
                    mockWebServer.takeRequest(),
                    "GET",
                    "/tv/1399/season/2/episode/5"
                               )
        }
    }

    @Test
    fun testGetPersonDetailsUsesPersonPath() {
        enqueueResponse()
        runBlocking {
            api.getPersonDetails(287)

            assertMethodAndPath(
                    mockWebServer.takeRequest(),
                    "GET",
                    "/person/287"
                               )
        }
    }

    @Test
    fun testSearchMultiSendsQueryAndPage() {
        enqueueResponse()
        runBlocking {
            api.searchMulti(
                    query = "the matrix",
                    page = 3
                           )

            val recordedRequest = mockWebServer.takeRequest()
            val requestUrl = recordedRequest.requestUrl!!
            assertMethodAndPath(
                    recordedRequest,
                    "GET",
                    "/search/multi"
                               )
            assertEquals(
                    "the matrix",
                    requestUrl.queryParameter(QUERY_PARAM_QUERY)
                        )
            assertEquals(
                    "3",
                    requestUrl.queryParameter(QUERY_PARAM_PAGE)
                        )
        }
    }

    @Test
    fun testToggleFavoritePostsBodyWithSessionIdAndAccountInPath() {
        enqueueResponse(body = MOCK_TOGGLE_FAVORITE_RESPONSE)
        runBlocking {
            val response = api.toggleFavorite(
                    accountId = dummyAccountId,
                    sessionId = dummySessionId,
                    toggleFavoriteRequestDto = ToggleFavoriteRequestDto(
                            mediaType = "tv",
                            mediaId = 1399,
                            favorite = true
                                                                       )
                                             )

            val recordedRequest = mockWebServer.takeRequest()
            assertMethodAndPath(
                    recordedRequest,
                    "POST",
                    "/account/$dummyAccountId/favorite"
                               )
            assertEquals(
                    dummySessionId,
                    recordedRequest.requestUrl!!.queryParameter(QUERY_PARAM_SESSION_ID)
                        )
            val sentBody = recordedRequest.body.readUtf8()
            assertTrue(sentBody.contains("\"media_type\":\"tv\""))
            assertTrue(sentBody.contains("\"media_id\":1399"))
            assertTrue(sentBody.contains("\"favorite\":true"))
            assertEquals(
                    1,
                    response.body()?.statusCode
                        )
        }
    }

    @Test
    fun testGetFavoriteMoviesSendsSessionPageAndSortDefaults() {
        enqueueResponse()
        runBlocking {
            api.getFavoriteMovies(
                    accountId = dummyAccountId,
                    sessionId = dummySessionId
                                 )

            val recordedRequest = mockWebServer.takeRequest()
            val requestUrl = recordedRequest.requestUrl!!
            assertMethodAndPath(
                    recordedRequest,
                    "GET",
                    "/account/$dummyAccountId/favorite/movies"
                               )
            assertEquals(
                    dummySessionId,
                    requestUrl.queryParameter(QUERY_PARAM_SESSION_ID)
                        )
            assertEquals(
                    "1",
                    requestUrl.queryParameter(QUERY_PARAM_PAGE)
                        )
            assertEquals(
                    "created_at.asc",
                    requestUrl.queryParameter(QUERY_PARAM_SORT_BY)
                        )
        }
    }

    @Test
    fun testGetFavoriteTvSeriesSendsSessionPageAndSortDefaults() {
        enqueueResponse()
        runBlocking {
            api.getFavoriteTvSeries(
                    accountId = dummyAccountId,
                    sessionId = dummySessionId,
                    page = 2
                                   )

            val recordedRequest = mockWebServer.takeRequest()
            val requestUrl = recordedRequest.requestUrl!!
            assertMethodAndPath(
                    recordedRequest,
                    "GET",
                    "/account/$dummyAccountId/favorite/tv"
                               )
            assertEquals(
                    "2",
                    requestUrl.queryParameter(QUERY_PARAM_PAGE)
                        )
            assertEquals(
                    "created_at.asc",
                    requestUrl.queryParameter(QUERY_PARAM_SORT_BY)
                        )
        }
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }
}
