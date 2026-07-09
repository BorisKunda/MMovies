package com.bk.mmovies.network

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.bk.mmovies.util.logDebug
import com.bk.mmovies.domain.repository.MovieRepository
import com.bk.mmovies.data.repository.MovieRepositoryImpl
import com.bk.mmovies.data.source.local.DbManager
import com.bk.mmovies.data.source.local.db.MovieDb
import com.bk.mmovies.data.source.remote.NetworkManager
import com.bk.mmovies.data.source.remote.QUERY_PARAM_API_KEY
import com.bk.mmovies.data.source.remote.api.TmdbApi
import com.bk.mmovies.data.source.remote.dto.TokenValidityDto
import com.bk.mmovies.domain.model.result.TokenValidationResult
import com.google.gson.Gson
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
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


class NetworkTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var okHttpClient: OkHttpClient
    private lateinit var certificates: HandshakeCertificates
    private lateinit var retrofit: Retrofit
    private lateinit var networkManager: NetworkManager
    private lateinit var api: TmdbApi
    private lateinit var repository: MovieRepository
    private lateinit var result: TokenValidationResult
    private val TAG = "NetworkTest"

    @Before
    fun setup() {
        setMockWebServer()
        setOkHttp()
        setRetrofit()
        api = retrofit.create(TmdbApi::class.java)
        val context: Context = ApplicationProvider.getApplicationContext()
        val db = Room.databaseBuilder(
            context.applicationContext,
            MovieDb::class.java,
            "database-name"
        ).build()
        networkManager = NetworkManager(Gson())
        repository = MovieRepositoryImpl(api, db, networkManager, DbManager())
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
            .addInterceptor(Interceptor { chain ->
                val originalRequest = chain.request()
                val newUrl = originalRequest.url.newBuilder()
                    .addQueryParameter(QUERY_PARAM_API_KEY, "123")
                    .build()
                val newRequest = originalRequest.newBuilder()
                    .url(newUrl)
                    .build()
                chain.proceed(newRequest)
            })
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
    fun testGetTokenValidationResultTrue() {
        val tokenValidResponse =
            MockResponse().apply {
                setResponseCode(200).setBody(
                    MOCK_GET_IS_TOKEN_VALID_TRUE_RESPONSE.trimIndent()
                )
            }
        runBlocking {
            mockWebServer.enqueue(tokenValidResponse)
            val response: Response<TokenValidityDto> = api.getIsApiV3TokenValid()
            assertTrue(response.isSuccessful)
            assertNotNull(response.body())
            assertEquals(true, response.body()?.isTokenValid)
        }
    }

    @Test
    fun testGetTokenValidationResultFalse() {
        val tokenValidResponse =
            MockResponse().apply {
                setResponseCode(401).setBody(
                    MOCK_GET_IS_TOKEN_VALID_FALSE_RESPONSE.trimIndent()
                )
            }
        runBlocking {
            mockWebServer.enqueue(tokenValidResponse)
            val response = api.getIsApiV3TokenValid()
            assertFalse(response.isSuccessful)
            assertTrue(response.code() == 401)
        }
    }

    /** TEARDOWN */
    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }
}
