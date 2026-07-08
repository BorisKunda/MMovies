package com.bk.mmovies.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bk.mmovies.data.source.local.dao.MovieDao
import com.bk.mmovies.data.source.local.dbmodel.entity.MovieEntity

@Database(
    entities = [MovieEntity::class],
    version = 1
)
abstract class MovieDb : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}

