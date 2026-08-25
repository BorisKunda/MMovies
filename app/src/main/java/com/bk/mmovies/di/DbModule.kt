package com.bk.mmovies.di


import android.content.Context
import androidx.room.Room
import com.bk.mmovies.data.source.local.DbManager
import com.bk.mmovies.data.source.local.dao.FavoriteDao
import com.bk.mmovies.data.source.local.dao.MovieDao
import com.bk.mmovies.data.source.local.dao.TvFavoriteDao
import com.bk.mmovies.data.source.local.db.MOVIE_DATABASE_NAME
import com.bk.mmovies.data.source.local.db.MovieDb
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
                // No user data of consequence lives in this cache, so a
                // schema bump can just rebuild it rather than carry migrations.
                .fallbackToDestructiveMigration()
                .build()
    }

    @Provides
    fun provideMovieDao(db: MovieDb): MovieDao {
        return db.movieDao()
    }

    @Provides
    fun provideFavoriteDao(db: MovieDb): FavoriteDao {
        return db.favoriteDao()
    }

    @Provides
    fun provideTvFavoriteDao(db: MovieDb): TvFavoriteDao {
        return db.tvFavoriteDao()
    }

    @Singleton
    @Provides
    fun provideDbManager(): DbManager {
        return DbManager()
    }
}