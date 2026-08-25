package com.bk.mmovies.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bk.mmovies.data.source.local.entity.RecentSearchEntity

@Dao
interface RecentSearchDao {
    @Query("SELECT query FROM recent_searches ORDER BY searchedAt DESC LIMIT :limit")
    suspend fun getRecentSearches(limit: Int): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recentSearch: RecentSearchEntity)

    @Query("DELETE FROM recent_searches WHERE query = :query")
    suspend fun deleteByQuery(query: String)

    @Query("DELETE FROM recent_searches")
    suspend fun clearAll()
}
