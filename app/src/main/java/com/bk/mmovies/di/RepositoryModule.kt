package com.bk.mmovies.di

import android.content.Context
import com.bk.mmovies.data.mapper.MovieDetailsMapper
import com.bk.mmovies.data.mapper.MovieMapper
import com.bk.mmovies.data.mapper.PersonDetailsMapper
import com.bk.mmovies.data.mapper.SearchResultMapper
import com.bk.mmovies.data.mapper.SeasonMapper
import com.bk.mmovies.data.mapper.SeriesAirDateLabelFormatter
import com.bk.mmovies.data.mapper.TvSeriesDetailsMapper
import com.bk.mmovies.data.mapper.TvSeriesMapper
import com.bk.mmovies.data.repositoryimpl.AuthenticationRepositoryImpl
import com.bk.mmovies.data.repositoryimpl.MovieRepositoryImpl
import com.bk.mmovies.data.repositoryimpl.PersonRepositoryImpl
import com.bk.mmovies.data.repositoryimpl.SearchRepositoryImpl
import com.bk.mmovies.data.repositoryimpl.TvSeriesRepositoryImpl
import com.bk.mmovies.data.source.local.LocalSessionDataCleaner
import com.bk.mmovies.data.source.local.dao.FavoriteDao
import com.bk.mmovies.data.source.local.dao.RecentSearchDao
import com.bk.mmovies.data.source.local.dao.TvFavoriteDao
import com.bk.mmovies.data.source.local.preferences.AuthCredentialsSharedPrefs
import com.bk.mmovies.data.source.remote.NetworkManager
import com.bk.mmovies.data.source.remote.api.TmdbApi
import com.bk.mmovies.domain.repository.AuthenticationRepository
import com.bk.mmovies.domain.repository.MovieRepository
import com.bk.mmovies.domain.repository.PersonRepository
import com.bk.mmovies.domain.repository.SearchRepository
import com.bk.mmovies.domain.repository.TvSeriesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideMovieRepository(
            api: TmdbApi,
            networkManager: NetworkManager,
            favoriteDao: FavoriteDao,
            movieMapper: MovieMapper,
            movieDetailsMapper: MovieDetailsMapper,
            @ApplicationContext context: Context,
                              ): MovieRepository =
            MovieRepositoryImpl(
                    api,
                    networkManager,
                    favoriteDao,
                    movieMapper,
                    movieDetailsMapper,
                    context
                               )

    @Provides
    @Singleton
    fun provideTvSeriesRepository(
            api: TmdbApi,
            networkManager: NetworkManager,
            tvSeriesMapper: TvSeriesMapper,
            seasonMapper: SeasonMapper,
            tvSeriesDetailsMapper: TvSeriesDetailsMapper,
            seriesAirDateLabelFormatter: SeriesAirDateLabelFormatter,
            tvFavoriteDao: TvFavoriteDao,
            @ApplicationContext context: Context,
                                 ): TvSeriesRepository =
            TvSeriesRepositoryImpl(
                    api,
                    networkManager,
                    tvSeriesMapper,
                    seasonMapper,
                    tvSeriesDetailsMapper,
                    seriesAirDateLabelFormatter,
                    tvFavoriteDao,
                    context
                                   )

    @Provides
    @Singleton
    fun providePersonRepository(
            api: TmdbApi,
            networkManager: NetworkManager,
            personDetailsMapper: PersonDetailsMapper,
            @ApplicationContext context: Context,
                               ): PersonRepository =
            PersonRepositoryImpl(
                    api,
                    networkManager,
                    personDetailsMapper,
                    context
                                 )

    @Provides
    @Singleton
    fun provideSearchRepository(
            api: TmdbApi,
            networkManager: NetworkManager,
            searchResultMapper: SearchResultMapper,
            recentSearchDao: RecentSearchDao,
            @ApplicationContext context: Context,
                               ): SearchRepository =
            SearchRepositoryImpl(
                    api,
                    networkManager,
                    searchResultMapper,
                    recentSearchDao,
                    context
                                 )

    @Provides
    @Singleton
    fun provideAuthenticationRepository(
            api: TmdbApi,
            authCredentialsSharedPrefs: AuthCredentialsSharedPrefs,
            networkManager: NetworkManager,
            localSessionDataCleaner: LocalSessionDataCleaner,
            @ApplicationContext context: Context,
                                       ): AuthenticationRepository = AuthenticationRepositoryImpl(
            api,
            authCredentialsSharedPrefs,
            networkManager,
            localSessionDataCleaner,
            context
                                                                                                  )
}