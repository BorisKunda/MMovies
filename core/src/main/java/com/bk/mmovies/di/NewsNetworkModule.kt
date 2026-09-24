package com.bk.mmovies.di

import com.bk.mmovies.core.BuildConfig
import com.bk.mmovies.data.source.remote.NEWS_API_BASE_URL
import com.bk.mmovies.data.source.remote.QUERY_PARAM_NEWS_API_KEY
import com.bk.mmovies.data.source.remote.api.NewsApi
import com.bk.mmovies.util.logDebug
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import com.bk.mmovies.data.source.remote.interceptor.ReadableLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

// Dedicated Retrofit/OkHttp stack for NewsAPI, kept separate from the TMDB
// stack in NetworkModule.kt since it targets a different base URL. Qualifiers
// avoid a duplicate-binding clash with the unqualified OkHttpClient/Retrofit
// NetworkModule already provides for TMDB.
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NewsHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NewsRetrofitClient

@Module
@InstallIn(SingletonComponent::class)
object NewsNetworkModule {

    @Provides
    @Singleton
    @NewsHttpClient
    fun provideNewsOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .apply {
                    if (BuildConfig.DEBUG) {
                        addInterceptor(
                                // The hard-coded NewsAPI key is not a
                                // per-user secret, but it's still kept
                                // out of logcat like every other API key
                                // in this app.
                                ReadableLoggingInterceptor(
                                        log = { message -> logDebug("Network", message) },
                                        redactedQueryParams = listOf(QUERY_PARAM_NEWS_API_KEY)
                                                          )
                                          )
                    }
                }
                .build()
    }

    @Provides
    @Singleton
    @NewsRetrofitClient
    fun provideNewsRetrofit(
            @NewsHttpClient okHttpClient: OkHttpClient,
            gson: Gson
                           ): Retrofit {
        return Retrofit.Builder()
                .baseUrl(NEWS_API_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
    }

    @Provides
    @Singleton
    fun provideNewsApi(@NewsRetrofitClient retrofit: Retrofit): NewsApi {
        return retrofit.create(NewsApi::class.java)
    }

    // The NewsRepository binding itself lives in each app module's own di/
    // package (:app's AppNewsModule, :tv's TvNewsModule) rather than here -
    // :tv binds NewsRepository to a Guardian-backed implementation instead
    // of NewsApi-backed NewsRepositoryImpl, and Hilt would see a duplicate
    // unqualified NewsRepository binding in :tv's graph if both this module
    // and TvNewsModule provided one. This module only ever needs to keep
    // providing NewsApi/Retrofit/OkHttpClient, since :app's AppNewsModule
    // still constructs NewsRepositoryImpl from those.
}
