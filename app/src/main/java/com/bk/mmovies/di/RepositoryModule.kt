package com.bk.mmovies.di

import android.content.Context
import com.bk.mmovies.data.mapper.MovieDetailsMapper
import com.bk.mmovies.data.mapper.MovieMapper
import com.bk.mmovies.data.repositoryimpl.AuthenticationRepositoryImpl
import com.bk.mmovies.data.repositoryimpl.MovieRepositoryImpl
import com.bk.mmovies.data.source.local.DbManager
import com.bk.mmovies.data.source.local.dao.FavoriteDao
import com.bk.mmovies.data.source.local.db.MovieDb
import com.bk.mmovies.data.source.local.preferences.AuthCredentialsSharedPrefs
import com.bk.mmovies.data.source.remote.NetworkManager
import com.bk.mmovies.data.source.remote.api.TmdbApi
import com.bk.mmovies.domain.repository.AuthenticationRepository
import com.bk.mmovies.domain.repository.MovieRepository
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
            db: MovieDb,
            networkManager: NetworkManager,
            dbManager: DbManager,
            favoriteDao: FavoriteDao,
            movieMapper: MovieMapper,
            movieDetailsMapper: MovieDetailsMapper,
            @ApplicationContext context: Context,
                              ): MovieRepository =
            MovieRepositoryImpl(
                    api,
                    db,
                    networkManager,
                    dbManager,
                    favoriteDao,
                    movieMapper,
                    movieDetailsMapper,
                    context
                               )

    @Provides
    @Singleton
    fun provideAuthenticationRepository(
            api: TmdbApi,
            authCredentialsSharedPrefs: AuthCredentialsSharedPrefs,
            networkManager: NetworkManager
                                       ): AuthenticationRepository = AuthenticationRepositoryImpl(
            api,
            authCredentialsSharedPrefs,
            networkManager
                                                                                                  )
}