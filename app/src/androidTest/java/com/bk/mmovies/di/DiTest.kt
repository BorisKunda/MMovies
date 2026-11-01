package com.bk.mmovies.di

import com.bk.mmovies.data.source.local.db.MovieDb
import com.bk.mmovies.data.source.local.dao.FavoriteDao
import com.bk.mmovies.data.source.local.dao.RecentSearchDao
import com.bk.mmovies.data.source.local.dao.TvFavoriteDao
import com.bk.mmovies.data.source.remote.NetworkManager
import com.bk.mmovies.data.source.remote.api.GeminiApi
import com.bk.mmovies.data.source.remote.api.NewsApi
import com.bk.mmovies.data.source.remote.api.TmdbApi
import com.bk.mmovies.domain.repository.AiRecommendationRepository
import com.bk.mmovies.domain.repository.AuthenticationRepository
import com.bk.mmovies.domain.repository.MovieRepository
import com.bk.mmovies.domain.repository.NewsRepository
import com.bk.mmovies.domain.repository.PersonRepository
import com.bk.mmovies.domain.repository.SearchRepository
import com.bk.mmovies.domain.repository.TvSeriesRepository
import com.google.gson.Gson
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import okhttp3.OkHttpClient
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import javax.inject.Inject

@HiltAndroidTest
class DiTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Inject
    lateinit var movieRepository: MovieRepository

    @Inject
    lateinit var tvSeriesRepository: TvSeriesRepository

    @Inject
    lateinit var personRepository: PersonRepository

    @Inject
    lateinit var searchRepository: SearchRepository

    @Inject
    lateinit var authenticationRepository: AuthenticationRepository

    @Inject
    lateinit var newsRepository: NewsRepository

    @Inject
    lateinit var aiRecommendationRepository: AiRecommendationRepository

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var networkManager: NetworkManager

    @Inject
    lateinit var tmdbOkHttpClient: OkHttpClient

    @Inject
    lateinit var tmdbRetrofit: Retrofit

    @Inject
    lateinit var tmdbApi: TmdbApi

    @Inject
    @field:NewsHttpClient
    lateinit var newsOkHttpClient: OkHttpClient

    @Inject
    @field:NewsRetrofitClient
    lateinit var newsRetrofit: Retrofit

    @Inject
    lateinit var newsApi: NewsApi

    @Inject
    @field:GeminiHttpClient
    lateinit var geminiOkHttpClient: OkHttpClient

    @Inject
    @field:GeminiRetrofitClient
    lateinit var geminiRetrofit: Retrofit

    @Inject
    lateinit var geminiApi: GeminiApi

    @Inject
    lateinit var movieDb: MovieDb

    @Inject
    lateinit var favoriteDao: FavoriteDao

    @Inject
    lateinit var tvFavoriteDao: TvFavoriteDao

    @Inject
    lateinit var recentSearchDao: RecentSearchDao

    @Test
    fun repositories_areInjected() {
        assertNotNull(movieRepository)
        assertNotNull(tvSeriesRepository)
        assertNotNull(personRepository)
        assertNotNull(searchRepository)
        assertNotNull(authenticationRepository)
        assertNotNull(newsRepository)
        assertNotNull(aiRecommendationRepository)
    }

    @Test
    fun tmdbNetworkStack_isInjected() {
        assertNotNull(gson)
        assertNotNull(networkManager)
        assertNotNull(tmdbOkHttpClient)
        assertNotNull(tmdbRetrofit)
        assertNotNull(tmdbApi)
    }

    @Test
    fun newsNetworkStack_isInjected() {
        assertNotNull(newsOkHttpClient)
        assertNotNull(newsRetrofit)
        assertNotNull(newsApi)
    }

    @Test
    fun geminiNetworkStack_isInjected() {
        assertNotNull(geminiOkHttpClient)
        assertNotNull(geminiRetrofit)
        assertNotNull(geminiApi)
    }

    @Test
    fun database_andDaos_areInjected() {
        assertNotNull(movieDb)
        assertNotNull(favoriteDao)
        assertNotNull(tvFavoriteDao)
        assertNotNull(recentSearchDao)
    }
}
