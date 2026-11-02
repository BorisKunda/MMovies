package com.bk.mmovies.network

import com.bk.mmovies.data.source.remote.GUARDIAN_ORDER_BY_NEWEST
import com.bk.mmovies.data.source.remote.GUARDIAN_PAGE_SIZE
import com.bk.mmovies.data.source.remote.GUARDIAN_SECTIONS_FILM_AND_TV
import com.bk.mmovies.data.source.remote.GUARDIAN_SHOW_ELEMENTS_IMAGE
import com.bk.mmovies.data.source.remote.GUARDIAN_SHOW_FIELDS
import com.bk.mmovies.data.source.remote.GUARDIAN_SHOW_FIELDS_BODY
import com.bk.mmovies.data.source.remote.QUERY_PARAM_GUARDIAN_API_KEY
import com.bk.mmovies.data.source.remote.QUERY_PARAM_GUARDIAN_ORDER_BY
import com.bk.mmovies.data.source.remote.QUERY_PARAM_GUARDIAN_PAGE
import com.bk.mmovies.data.source.remote.QUERY_PARAM_GUARDIAN_PAGE_SIZE
import com.bk.mmovies.data.source.remote.QUERY_PARAM_GUARDIAN_SECTION
import com.bk.mmovies.data.source.remote.QUERY_PARAM_GUARDIAN_SHOW_ELEMENTS
import com.bk.mmovies.data.source.remote.QUERY_PARAM_GUARDIAN_SHOW_FIELDS
import com.bk.mmovies.data.source.remote.api.GuardianApi
import com.bk.mmovies.util.logDebug
import kotlinx.coroutines.runBlocking
import okhttp3.Dns
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.InetAddress

class GuardianApiTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var okHttpClient: OkHttpClient
    private lateinit var certificates: HandshakeCertificates
    private lateinit var retrofit: Retrofit
    private lateinit var api: GuardianApi
    private val TAG = "GuardianApiTest"
    private val mockServerDottedHostName = "mock.guardian.test"

    private val dummyArticleId = "film/2026/sep/16/dummy-film-review"

    @Before
    fun setup() {
        setMockWebServer()
        setOkHttp()
        setRetrofit()
        api = retrofit.create(GuardianApi::class.java)
    }

    private fun setMockWebServer() {
        val heldCertificate = HeldCertificate.Builder()
                .commonName("localhost")
                .addSubjectAlternativeName("localhost")
                .addSubjectAlternativeName(mockServerDottedHostName)
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
                .dns(object : Dns {
                    override fun lookup(hostname: String): List<InetAddress> {
                        return listOf(InetAddress.getByName(mockWebServer.hostName))
                    }
                })
                .sslSocketFactory(
                        certificates.sslSocketFactory(),
                        certificates.trustManager
                                 )
                .build()
    }

    private fun setRetrofit() {
        retrofit = Retrofit.Builder()
                .baseUrl(
                        mockWebServer.url("/")
                                .newBuilder()
                                .host(mockServerDottedHostName)
                                .build()
                        )
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    private fun enqueueResponse(
            code: Int,
            body: String
                               ) {
        mockWebServer.enqueue(
                MockResponse().apply {
                    setResponseCode(code).setBody(body.trimIndent())
                }
                             )
    }

    @Test
    fun testSearchSendsExpectedPathAndDefaultQueryParams() {
        enqueueResponse(
                200,
                MOCK_GUARDIAN_SEARCH_SUCCESS_RESPONSE
                       )
        runBlocking {
            api.search()

            val recordedRequest = mockWebServer.takeRequest()
            val requestUrl = recordedRequest.requestUrl!!

            assertEquals(
                    "GET",
                    recordedRequest.method
                        )
            assertEquals(
                    "/search",
                    requestUrl.encodedPath
                        )
            assertEquals(
                    GUARDIAN_SECTIONS_FILM_AND_TV,
                    requestUrl.queryParameter(QUERY_PARAM_GUARDIAN_SECTION)
                        )
            assertEquals(
                    "1",
                    requestUrl.queryParameter(QUERY_PARAM_GUARDIAN_PAGE)
                        )
            assertEquals(
                    GUARDIAN_PAGE_SIZE.toString(),
                    requestUrl.queryParameter(QUERY_PARAM_GUARDIAN_PAGE_SIZE)
                        )
            assertEquals(
                    GUARDIAN_SHOW_FIELDS,
                    requestUrl.queryParameter(QUERY_PARAM_GUARDIAN_SHOW_FIELDS)
                        )
            assertEquals(
                    GUARDIAN_ORDER_BY_NEWEST,
                    requestUrl.queryParameter(QUERY_PARAM_GUARDIAN_ORDER_BY)
                        )
            assertTrue(requestUrl.queryParameterNames.contains(QUERY_PARAM_GUARDIAN_API_KEY))
        }
    }

    @Test
    fun testSearchForwardsRequestedPageAndApiKey() {
        enqueueResponse(
                200,
                MOCK_GUARDIAN_SEARCH_SUCCESS_RESPONSE
                       )
        runBlocking {
            api.search(
                    page = 3,
                    apiKey = "dummy_key"
                      )

            val requestUrl = mockWebServer.takeRequest().requestUrl!!
            assertEquals(
                    "3",
                    requestUrl.queryParameter(QUERY_PARAM_GUARDIAN_PAGE)
                        )
            assertEquals(
                    "dummy_key",
                    requestUrl.queryParameter(QUERY_PARAM_GUARDIAN_API_KEY)
                        )
        }
    }

    @Test
    fun testSearchSuccessParsesArticles() {
        enqueueResponse(
                200,
                MOCK_GUARDIAN_SEARCH_SUCCESS_RESPONSE
                       )
        runBlocking {
            val result = api.search()

            assertTrue(result.isSuccessful)
            val searchResponse = result.body()?.response
            assertNotNull(searchResponse)
            assertEquals(
                    "ok",
                    searchResponse?.status
                        )
            assertEquals(
                    5,
                    searchResponse?.pages
                        )
            assertEquals(
                    1,
                    searchResponse?.results?.size
                        )
            val article = searchResponse?.results?.first()
            assertEquals(
                    "Dummy Film Review",
                    article?.webTitle
                        )
            assertEquals(
                    dummyArticleId,
                    article?.id
                        )
            assertEquals(
                    "Jane Doe",
                    article?.fields?.byline
                        )
            assertNull(article?.fields?.body)
        }
    }

    @Test
    fun testSearchFailureResponse() {
        enqueueResponse(
                401,
                MOCK_GUARDIAN_ERROR_RESPONSE
                       )
        runBlocking {
            val result = api.search()

            assertFalse(result.isSuccessful)
            assertEquals(
                    401,
                    result.code()
                        )
            assertNull(result.body())
            assertTrue(result.errorBody()?.string()?.contains("Invalid authentication credentials") == true)
        }
    }

    @Test
    fun testSearchMalformedResponseYieldsNullResponse() {
        enqueueResponse(
                200,
                MOCK_GUARDIAN_MALFORMED_RESPONSE
                       )
        runBlocking {
            val result = api.search()

            assertTrue(result.isSuccessful)
            assertNotNull(result.body())
            assertNull(result.body()?.response)
        }
    }

    @Test
    fun testGetArticleKeepsSlashesLiteralInPathAndSendsExpectedQueryParams() {
        enqueueResponse(
                200,
                MOCK_GUARDIAN_ARTICLE_SUCCESS_RESPONSE
                       )
        runBlocking {
            api.getArticle(dummyArticleId)

            val recordedRequest = mockWebServer.takeRequest()
            val requestUrl = recordedRequest.requestUrl!!

            assertEquals(
                    "GET",
                    recordedRequest.method
                        )
            assertEquals(
                    "/$dummyArticleId",
                    requestUrl.encodedPath
                        )
            assertEquals(
                    GUARDIAN_SHOW_FIELDS_BODY,
                    requestUrl.queryParameter(QUERY_PARAM_GUARDIAN_SHOW_FIELDS)
                        )
            assertEquals(
                    GUARDIAN_SHOW_ELEMENTS_IMAGE,
                    requestUrl.queryParameter(QUERY_PARAM_GUARDIAN_SHOW_ELEMENTS)
                        )
            assertTrue(requestUrl.queryParameterNames.contains(QUERY_PARAM_GUARDIAN_API_KEY))
        }
    }

    @Test
    fun testGetArticleSuccessParsesBodyAndImageElements() {
        enqueueResponse(
                200,
                MOCK_GUARDIAN_ARTICLE_SUCCESS_RESPONSE
                       )
        runBlocking {
            val result = api.getArticle(dummyArticleId)

            assertTrue(result.isSuccessful)
            val content = result.body()?.response?.content
            assertNotNull(content)
            assertEquals(
                    "<p>Full article body.</p>",
                    content?.fields?.body
                        )
            val imageAsset = content?.elements?.first()?.assets?.first()
            assertEquals(
                    "https://media.guim.co.uk/dummy/1000.jpg",
                    imageAsset?.file
                        )
            assertEquals(
                    "1000",
                    imageAsset?.typeData?.width
                        )
        }
    }

    @Test
    fun testGetArticleFailureResponse() {
        enqueueResponse(
                404,
                MOCK_GUARDIAN_ERROR_RESPONSE
                       )
        runBlocking {
            val result = api.getArticle(dummyArticleId)

            assertFalse(result.isSuccessful)
            assertEquals(
                    404,
                    result.code()
                        )
            assertNull(result.body())
        }
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }
}
