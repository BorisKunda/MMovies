package com.bk.mmovies.di

import android.content.Context
import com.bk.mmovies.core.BuildConfig
import com.bk.mmovies.data.mapper.RecommendationMapper
import com.bk.mmovies.data.repositoryimpl.AiRecommendationRepositoryImpl
import com.bk.mmovies.data.source.local.preferences.GeminiModelAvailabilitySharedPrefs
import com.bk.mmovies.data.source.remote.GEMINI_API_BASE_URL
import com.bk.mmovies.data.source.remote.NetworkManager
import com.bk.mmovies.data.source.remote.QUERY_PARAM_GEMINI_API_KEY
import com.bk.mmovies.data.source.remote.api.GeminiApi
import com.bk.mmovies.data.source.remote.interceptor.ReadableLoggingInterceptor
import com.bk.mmovies.domain.repository.AiRecommendationRepository
import com.bk.mmovies.util.logDebug
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

// Dedicated Retrofit/OkHttp stack for the Gemini API, kept separate from the
// TMDB stack in NetworkModule.kt since it targets a different base URL.
// Qualifiers avoid a duplicate-binding clash with the unqualified
// OkHttpClient/Retrofit NetworkModule already provides for TMDB.
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeminiHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeminiRetrofitClient

// Longest a single Gemini request may take before it is given up on.
private const val GEMINI_CALL_TIMEOUT_SECONDS = 10L

@Module
@InstallIn(SingletonComponent::class)
object GeminiNetworkModule {

    @Provides
    @Singleton
    @GeminiHttpClient
    fun provideGeminiOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                // Hard cap of GEMINI_CALL_TIMEOUT_SECONDS on the WHOLE call (DNS,
                // connect, sending, waiting for and reading the answer), so one
                // model can never hold up the others for long. The individual
                // timeouts below are set to the same value: without callTimeout
                // each one would apply separately and a call could take longer.
                // Real generateContent answers took about 4-9s on-device (more
                // than a typical TMDB lookup), so this leaves little slack - a
                // slow-but-valid answer past the cap counts as a failure.
                // Never resend a request behind the caller's back: OkHttp by default
                // silently retries some connection failures on the same model.
                // A failure must surface so the caller can move to the NEXT model
                // (see AiRecommendationRepositoryImpl).
                .retryOnConnectionFailure(false)
                .callTimeout(GEMINI_CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .connectTimeout(GEMINI_CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(GEMINI_CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(GEMINI_CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .apply {
                    if (BuildConfig.DEBUG) {
                        addInterceptor(
                                // Full request/response bodies, so a failure's
                                // reason (e.g. which quota a 429 hit) is in logcat.
                                // The API key rides as a query param on every
                                // call - keep it out of logcat like every other
                                // API key in this app.
                                ReadableLoggingInterceptor(
                                        log = { message -> logDebug("Network", message) },
                                        redactedQueryParams = listOf(QUERY_PARAM_GEMINI_API_KEY)
                                                          )
                                      )
                    }
                }
                .build()
    }

    @Provides
    @Singleton
    @GeminiRetrofitClient
    fun provideGeminiRetrofit(
            @GeminiHttpClient okHttpClient: OkHttpClient,
            gson: Gson
                             ): Retrofit {
        return Retrofit.Builder()
                .baseUrl(GEMINI_API_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
    }

    @Provides
    @Singleton
    fun provideGeminiApi(@GeminiRetrofitClient retrofit: Retrofit): GeminiApi {
        return retrofit.create(GeminiApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAiRecommendationRepository(
            api: GeminiApi,
            networkManager: NetworkManager,
            recommendationMapper: RecommendationMapper,
            gson: Gson,
            modelAvailability: GeminiModelAvailabilitySharedPrefs,
            @ApplicationContext context: Context
                                          ): AiRecommendationRepository =
            AiRecommendationRepositoryImpl(api, networkManager, recommendationMapper, gson, modelAvailability, context)
}
