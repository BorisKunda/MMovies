package com.bk.mmovies.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bk.mmovies.data.source.local.db.FAVORITES_TABLE_NAME

@Entity(tableName = FAVORITES_TABLE_NAME)
data class FavoriteEntity(
        @PrimaryKey val movieId: Int
                         )
