package com.bk.mmovies.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.bk.mmovies.data.source.local.entity.RecentSearchEntity

@Dao
interface RecentSearchDao {
    @Query("SELECT query FROM recent_searches ORDER BY searchedAt DESC LIMIT :limit")
    suspend fun getRecentSearches(limit: Int): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recentSearch: RecentSearchEntity)

    // The primary key is case-sensitive, so a search that only differs by
    // case would otherwise insert alongside the existing row instead of
    // bumping it to the top; this clears any case-insensitive match first.
    @Query("DELETE FROM recent_searches WHERE query = :query COLLATE NOCASE")
    suspend fun deleteByQueryIgnoreCase(query: String)

    @Transaction
    suspend fun upsertIgnoringCase(recentSearch: RecentSearchEntity) {
        deleteByQueryIgnoreCase(recentSearch.query)
        insert(recentSearch)
    }

    @Query("DELETE FROM recent_searches WHERE query = :query")
    suspend fun deleteByQuery(query: String)

    @Query("DELETE FROM recent_searches")
    suspend fun clearAll()
}
