package com.bk.mmovies.data.source.local

import com.bk.mmovies.data.source.local.dao.FavoriteDao
import com.bk.mmovies.data.source.local.dao.RecentSearchDao
import com.bk.mmovies.data.source.local.dao.TvFavoriteDao
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wipes every table that belongs to the signed-in account.
 *
 * Clearing SharedPreferences alone used to leave the favorite-id caches and
 * the search history behind, so the next person to sign in on the device saw
 * the previous account's stars until a sync overwrote them — and their whole
 * search history indefinitely.
 */
@Singleton
class LocalSessionDataCleaner @Inject constructor(
        private val favoriteDao: FavoriteDao,
        private val tvFavoriteDao: TvFavoriteDao,
        private val recentSearchDao: RecentSearchDao
                                                 ) {

    suspend fun clearAccountScopedData() {
        favoriteDao.clearAll()
        tvFavoriteDao.clearAll()
        recentSearchDao.clearAll()
    }
}
