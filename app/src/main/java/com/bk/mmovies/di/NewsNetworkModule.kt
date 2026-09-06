package com.bk.mmovies.di

import com.bk.mmovies.BuildConfig
import com.bk.mmovies.data.mapper.NewsMapper
import com.bk.mmovies.data.repositoryimpl.NewsRepositoryImpl
import com.bk.mmovies.data.source.remote.NEWS_API_BASE_URL
import com.bk.mmovies.data.source.remote.NetworkManager
import com.bk.mmovies.data.source.remote.QUERY_PARAM_NEWS_API_KEY
import com.bk.mmovies.data.source.remote.api.NewsApi
import com.bk.mmovies.domain.repository.NewsRepository
import com.bk.mmovies.util.logDebug
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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
                                HttpLoggingInterceptor { message ->
                                    logDebug(
                                            "Network",
                                            message
                                            )
                                }.apply {
                                    level = HttpLoggingInterceptor.Level.BASIC
                                    // The hard-coded NewsAPI key is not a
                                    // per-user secret, but it's still kept
                                    // out of logcat like every other API key
                                    // in this app.
                                    redactQueryParams(QUERY_PARAM_NEWS_API_KEY)
                                }
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

    @Provides
    @Singleton
    fun provideNewsRepository(
            api: NewsApi,
            networkManager: NetworkManager,
            newsMapper: NewsMapper,
            @ApplicationContext context: Context
                             ): NewsRepository =
            NewsRepositoryImpl(api, networkManager, newsMapper, context)
}
