package com.bk.mmovies.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bk.mmovies.data.source.local.entity.TvFavoriteEntity

@Dao
interface TvFavoriteDao {
    @Query("SELECT tvSeriesId FROM tv_favorites")
    suspend fun getAllFavoriteIds(): List<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(favorites: List<TvFavoriteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: TvFavoriteEntity)

    @Query("DELETE FROM tv_favorites WHERE tvSeriesId = :tvSeriesId")
    suspend fun deleteById(tvSeriesId: Int)

    @Query("DELETE FROM tv_favorites")
    suspend fun clearAll()
}
