package com.bk.mmovies.data.source.local.dbmodel.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bk.mmovies.data.source.local.MOVIES_TABLE_NAME

@Entity(tableName = MOVIES_TABLE_NAME)
data class MovieEntity(
    val movieId: Int,
    val title: String,
    val desc: String,
    val imageUrl: String,
    val releaseDate: String,
    val categoryId: Int,
    val isFavorite: Boolean,
) {
    @PrimaryKey(autoGenerate = true)
    var dbId: Int = 0
}
