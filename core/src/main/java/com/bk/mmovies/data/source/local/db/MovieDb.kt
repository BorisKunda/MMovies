package com.bk.mmovies.data.source.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bk.mmovies.data.source.local.dao.FavoriteDao
import com.bk.mmovies.data.source.local.dao.RecentSearchDao
import com.bk.mmovies.data.source.local.dao.TvFavoriteDao
import com.bk.mmovies.data.source.local.entity.FavoriteEntity
import com.bk.mmovies.data.source.local.entity.RecentSearchEntity
import com.bk.mmovies.data.source.local.entity.TvFavoriteEntity

// v5 drops the `movies` table: MovieEntity/MovieDao were never read or written
// by any production code path, only carried in the schema.
@Database(
        entities = [FavoriteEntity::class, TvFavoriteEntity::class, RecentSearchEntity::class],
        version = 5,
        exportSchema = false
         )
abstract class MovieDb : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun tvFavoriteDao(): TvFavoriteDao
    abstract fun recentSearchDao(): RecentSearchDao
}

