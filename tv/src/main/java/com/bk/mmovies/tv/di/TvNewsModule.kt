package com.bk.mmovies.tv.di

import android.content.Context
import com.bk.mmovies.core.BuildConfig
import com.bk.mmovies.data.mapper.GuardianNewsMapper
import com.bk.mmovies.data.repositoryimpl.GuardianNewsRepositoryImpl
import com.bk.mmovies.data.source.remote.GUARDIAN_API_BASE_URL
import com.bk.mmovies.data.source.remote.NetworkManager
import com.bk.mmovies.data.source.remote.QUERY_PARAM_GUARDIAN_API_KEY
import com.bk.mmovies.data.source.remote.api.GuardianApi
import com.bk.mmovies.domain.repository.NewsRepository
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
import javax.inject.Qualifier
import javax.inject.Singleton

// :tv-only News stack: dedicated Retrofit/OkHttp for the Guardian Content
// API, kept separate from :core's TMDB/NewsAPI/Gemini stacks since it targets
// a different base URL. This module (and its NewsRepository binding below)
// is what actually makes :tv's News feature Guardian-backed while :app stays
// on NewsAPI (see :core's NewsNetworkModule + :app's AppNewsModule).
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GuardianHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GuardianRetrofitClient

@Module
@InstallIn(SingletonComponent::class)
object TvNewsModule {

    @Provides
    @Singleton
    @GuardianHttpClient
    fun provideGuardianOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .apply {
                    if (BuildConfig.DEBUG) {
                        addInterceptor(
                                HttpLoggingInterceptor { message -> logDebug("Network", message) }.apply {
                                    level = HttpLoggingInterceptor.Level.BASIC
                                    redactQueryParams(QUERY_PARAM_GUARDIAN_API_KEY)
                                }
                                      )
                    }
                }
                .build()
    }

    @Provides
    @Singleton
    @GuardianRetrofitClient
    fun provideGuardianRetrofit(
            @GuardianHttpClient okHttpClient: OkHttpClient,
            gson: Gson
                               ): Retrofit {
        return Retrofit.Builder()
                .baseUrl(GUARDIAN_API_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
    }

    @Provides
    @Singleton
    fun provideGuardianApi(@GuardianRetrofitClient retrofit: Retrofit): GuardianApi {
        return retrofit.create(GuardianApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNewsRepository(
            api: GuardianApi,
            networkManager: NetworkManager,
            guardianNewsMapper: GuardianNewsMapper,
            @ApplicationContext context: Context
                             ): NewsRepository =
            GuardianNewsRepositoryImpl(api, networkManager, guardianNewsMapper, context)
}
