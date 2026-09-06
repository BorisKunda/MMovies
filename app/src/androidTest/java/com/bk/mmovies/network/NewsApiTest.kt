package com.bk.mmovies.network

import com.bk.mmovies.data.source.remote.QUERY_PARAM_NEWS_API_KEY
import com.bk.mmovies.data.source.remote.QUERY_PARAM_NEWS_CATEGORY
import com.bk.mmovies.data.source.remote.QUERY_PARAM_NEWS_COUNTRY
import com.bk.mmovies.data.source.remote.QUERY_PARAM_NEWS_PAGE
import com.bk.mmovies.data.source.remote.QUERY_PARAM_NEWS_PAGE_SIZE
import com.bk.mmovies.data.source.remote.api.NewsApi
import com.bk.mmovies.data.source.remote.dto.NewsResponseDto
import com.bk.mmovies.util.logDebug
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.HeldCertificate
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NewsApiTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var okHttpClient: OkHttpClient
    private lateinit var certificates: HandshakeCertificates
    private lateinit var retrofit: Retrofit
    private lateinit var api: NewsApi
    private val TAG = "NewsApiTest"

    @Before
    fun setup() {
        setMockWebServer()
        setOkHttp()
        setRetrofit()
        api = retrofit.create(NewsApi::class.java)
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

    @Test
    fun testGetTopHeadlinesSendsExpectedQueryParams() {
        val response = MockResponse().apply {
            setResponseCode(200).setBody(MOCK_NEWS_TOP_HEADLINES_SUCCESS_RESPONSE.trimIndent())
        }
        runBlocking {
            mockWebServer.enqueue(response)
            api.getTopHeadlines()

            val recordedRequest = mockWebServer.takeRequest()
            val requestUrl = recordedRequest.requestUrl!!

            assertEquals("/top-headlines", requestUrl.encodedPath)
            assertEquals("us", requestUrl.queryParameter(QUERY_PARAM_NEWS_COUNTRY))
            assertEquals("entertainment", requestUrl.queryParameter(QUERY_PARAM_NEWS_CATEGORY))
            assertEquals("1", requestUrl.queryParameter(QUERY_PARAM_NEWS_PAGE))
            assertEquals("20", requestUrl.queryParameter(QUERY_PARAM_NEWS_PAGE_SIZE))
            assertNotNull(requestUrl.queryParameter(QUERY_PARAM_NEWS_API_KEY))
        }
    }

    @Test
    fun testGetTopHeadlinesForwardsRequestedPage() {
        val response = MockResponse().apply {
            setResponseCode(200).setBody(MOCK_NEWS_TOP_HEADLINES_SUCCESS_RESPONSE.trimIndent())
        }
        runBlocking {
            mockWebServer.enqueue(response)
            api.getTopHeadlines(page = 2)

            val requestUrl = mockWebServer.takeRequest().requestUrl!!
            assertEquals("2", requestUrl.queryParameter(QUERY_PARAM_NEWS_PAGE))
        }
    }

    @Test
    fun testGetTopHeadlinesSuccessParsesArticles() {
        val response = MockResponse().apply {
            setResponseCode(200).setBody(MOCK_NEWS_TOP_HEADLINES_SUCCESS_RESPONSE.trimIndent())
        }
        runBlocking {
            mockWebServer.enqueue(response)
            val result: Response<NewsResponseDto> = api.getTopHeadlines()

            assertTrue(result.isSuccessful)
            val body = result.body()
            assertNotNull(body)
            assertEquals(1, body?.articles?.size)
            assertEquals("Movie News Title", body?.articles?.first()?.title)
        }
    }

    @Test
    fun testGetTopHeadlinesFailureResponse() {
        val response = MockResponse().apply {
            setResponseCode(401).setBody(MOCK_NEWS_ERROR_RESPONSE.trimIndent())
        }
        runBlocking {
            mockWebServer.enqueue(response)
            val result = api.getTopHeadlines()

            assertFalse(result.isSuccessful)
            assertTrue(result.code() == 401)
            assertNull(result.body())
            assertTrue(result.errorBody()?.string()?.contains("apiKeyInvalid") == true)
        }
    }

    @Test
    fun testGetTopHeadlinesMalformedResponseYieldsEmptyArticles() {
        val response = MockResponse().apply {
            setResponseCode(200).setBody(MOCK_NEWS_MALFORMED_RESPONSE.trimIndent())
        }
        runBlocking {
            mockWebServer.enqueue(response)
            val result = api.getTopHeadlines()

            assertTrue(result.isSuccessful)
            val body = result.body()
            assertNotNull(body)
            assertNull(body?.articles)
        }
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }
}
