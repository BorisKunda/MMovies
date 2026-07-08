package com.bk.mmovies.di


import com.bk.mmovies.BuildConfig
import com.bk.mmovies.app.logDebug
import com.bk.mmovies.data.source.remote.NETWORK_TAG
import com.bk.mmovies.data.source.remote.NetworkManager
import com.bk.mmovies.data.source.remote.QUERY_PARAM_API_KEY
import com.bk.mmovies.data.source.remote.TMDB_BASE_URL
import com.bk.mmovies.data.source.remote.TmdbApi
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
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
    fun provideInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val tmdbKey = BuildConfig.TMDB_API_KEY
            val newUrl = originalRequest.url.newBuilder()
                .addQueryParameter(QUERY_PARAM_API_KEY, tmdbKey)
                .build()

            val newRequest = originalRequest.newBuilder()
                .url(newUrl)
                .build()

            chain.proceed(newRequest)
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(interceptor: Interceptor): OkHttpClient {
        val okHttpBuilder = OkHttpClient.Builder()
        okHttpBuilder.addInterceptor(interceptor)
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor(logger = { message: String ->
                logDebug(NETWORK_TAG, message)
            })
            loggingInterceptor.level = Level.BODY
            okHttpBuilder.addInterceptor(loggingInterceptor)
        }
        return okHttpBuilder.build()
    }

    @Provides
    @Singleton
    fun provideNetworkManager(gson: Gson): NetworkManager {
        return NetworkManager(gson)
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(TMDB_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideTmdbApi(retrofit: Retrofit): TmdbApi {
        return retrofit.create(TmdbApi::class.java)
    }
}
