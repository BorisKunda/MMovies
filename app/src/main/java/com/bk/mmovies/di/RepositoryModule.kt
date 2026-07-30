package com.bk.mmovies.di

import com.bk.mmovies.data.repositoryimpl.ApiKeyRepositoryImpl
import com.bk.mmovies.data.repositoryimpl.MovieRepositoryImpl
import com.bk.mmovies.data.source.local.DbManager
import com.bk.mmovies.data.source.local.db.MovieDb
import com.bk.mmovies.data.source.local.preferences.ApiKeyStorage
import com.bk.mmovies.data.source.remote.NetworkManager
import com.bk.mmovies.data.source.remote.api.TmdbApi
import com.bk.mmovies.domain.repository.ApiKeyRepository
import com.bk.mmovies.domain.repository.MovieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
                              ): MovieRepository =
            MovieRepositoryImpl(
                    api,
                    db,
                    networkManager,
                    dbManager
                               )

    @Provides
    @Singleton
    fun provideApiKeyRepository(
            api: TmdbApi,
            apiKeyStorage: ApiKeyStorage,
            networkManager: NetworkManager
                               ): ApiKeyRepository = ApiKeyRepositoryImpl(
            api,
            apiKeyStorage,
            networkManager
                                                                         )
}