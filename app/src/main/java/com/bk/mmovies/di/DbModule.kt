package com.bk.mmovies.di


import android.content.Context
import androidx.room.Room
import com.bk.mmovies.data.source.local.DbManager
import com.bk.mmovies.data.source.local.db.MOVIE_DATABASE_NAME
import com.bk.mmovies.data.source.local.db.MovieDb
import com.bk.mmovies.data.source.local.dao.MovieDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MovieDb {
        return Room.databaseBuilder(
            context,
            MovieDb::class.java,
            MOVIE_DATABASE_NAME
        )
            .build()
    }

    @Provides
    fun provideMovieDao(db: MovieDb): MovieDao {
        return db.movieDao()
    }

    @Singleton
    @Provides
    fun provideDbManager(): DbManager {
        return DbManager()
    }
}