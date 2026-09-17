package com.bk.mmovies.di

import android.content.Context
import com.bk.mmovies.core.BuildConfig
import com.bk.mmovies.data.mapper.RecommendationMapper
import com.bk.mmovies.data.repositoryimpl.AiRecommendationRepositoryImpl
import com.bk.mmovies.data.source.remote.GEMINI_API_BASE_URL
import com.bk.mmovies.data.source.remote.NetworkManager
import com.bk.mmovies.data.source.remote.QUERY_PARAM_GEMINI_API_KEY
import com.bk.mmovies.data.source.remote.api.GeminiApi
import com.bk.mmovies.domain.repository.AiRecommendationRepository
import com.bk.mmovies.util.logDebug
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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

@Module
@InstallIn(SingletonComponent::class)
object GeminiNetworkModule {

    @Provides
    @Singleton
    @GeminiHttpClient
    fun provideGeminiOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                // Generating a full JSON suggestions list is a slower call than a
                // typical TMDB/NewsAPI lookup - OkHttp's 10s default read timeout
                // was observed timing out real generateContent responses on-device.
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .apply {
                    if (BuildConfig.DEBUG) {
                        addInterceptor(
                                HttpLoggingInterceptor { message ->
                                    logDebug(
                                            "Network",
                                            message
                                            )
                                }.apply {
                                    level = HttpLoggingInterceptor.Level.BASIC
                                    // The API key rides as a query param on
                                    // every call - keep it out of logcat like
                                    // every other API key in this app.
                                    redactQueryParams(QUERY_PARAM_GEMINI_API_KEY)
                                }
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
            @ApplicationContext context: Context
                                          ): AiRecommendationRepository =
            AiRecommendationRepositoryImpl(api, networkManager, recommendationMapper, gson, context)
}
