package com.bk.mmovies.di

import com.bk.mmovies.domain.repository.MovieRepository
import com.bk.mmovies.data.repository.MovieRepositoryImpl
import com.bk.mmovies.data.source.local.DbManager
import com.bk.mmovies.data.source.local.db.MovieDb
import com.bk.mmovies.data.source.remote.NetworkManager
import com.bk.mmovies.data.source.remote.api.TmdbApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun provideMovieRepository(
        api: TmdbApi,
        db: MovieDb,
        networkManager: NetworkManager,
        dbManager: DbManager
    ): MovieRepository =
        MovieRepositoryImpl(
            api, db, networkManager, dbManager
        )
}