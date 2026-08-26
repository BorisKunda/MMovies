package com.bk.mmovies.di

import com.bk.mmovies.BuildConfig
import com.bk.mmovies.data.source.remote.NetworkManager
import com.bk.mmovies.data.source.remote.TMDB_API_BASE_URL
import com.bk.mmovies.data.source.remote.api.TmdbApi
import com.bk.mmovies.data.source.remote.interceptor.ApiKeyInterceptor
import com.bk.mmovies.data.source.remote.interceptor.DebugLoggingInterceptor
import com.bk.mmovies.data.source.remote.interceptor.LanguageInterceptor
import com.bk.mmovies.util.logDebug
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
            apiKeyInterceptor: ApiKeyInterceptor,
            languageInterceptor: LanguageInterceptor
                           ): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(apiKeyInterceptor)
                .addInterceptor(languageInterceptor)
                .apply {
                    if (BuildConfig.DEBUG) {
                        addInterceptor(
                                DebugLoggingInterceptor { message ->
                                    logDebug(
                                            "Network",
                                            message
                                            )
                                }
                                      )
                    }
                }
                .build()
    }

    @Provides
    @Singleton
    fun provideNetworkManager(
            gson: Gson
                             ): NetworkManager {
        return NetworkManager(gson)
    }

    @Provides
    @Singleton
    fun provideRetrofit(
            okHttpClient: OkHttpClient,
            gson: Gson
                       ): Retrofit {
        return Retrofit.Builder()
                .baseUrl(TMDB_API_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(
                        GsonConverterFactory.create(gson)
                                    )
                .build()
    }

    @Provides
    @Singleton
    fun provideTmdbApi(
            retrofit: Retrofit
                      ): TmdbApi {
        return retrofit.create(TmdbApi::class.java)
    }
}