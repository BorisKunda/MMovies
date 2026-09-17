package com.bk.mmovies.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bk.mmovies.data.source.local.db.RECENT_SEARCHES_TABLE_NAME

@Entity(tableName = RECENT_SEARCHES_TABLE_NAME)
data class RecentSearchEntity(
        @PrimaryKey val query: String,
        val searchedAt: Long
                             )
