package com.bk.mmovies.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bk.mmovies.data.source.local.dao.FavoriteDao
import com.bk.mmovies.data.source.local.dao.RecentSearchDao
import com.bk.mmovies.data.source.local.dao.TvFavoriteDao
import com.bk.mmovies.data.source.local.db.MovieDb
import com.bk.mmovies.data.source.local.entity.FavoriteEntity
import com.bk.mmovies.data.source.local.entity.RecentSearchEntity
import com.bk.mmovies.data.source.local.entity.TvFavoriteEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DbTest {

    private lateinit var movieDb: MovieDb
    private lateinit var favoriteDao: FavoriteDao
    private lateinit var tvFavoriteDao: TvFavoriteDao
    private lateinit var recentSearchDao: RecentSearchDao

    @Before
    fun setUp() {
        movieDb = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                MovieDb::class.java
                                              ).build()
        favoriteDao = movieDb.favoriteDao()
        tvFavoriteDao = movieDb.tvFavoriteDao()
        recentSearchDao = movieDb.recentSearchDao()
    }

    @After
    fun tearDown() {
        movieDb.close()
    }

    @Test
    fun insertAndReadFavoriteIds() = runBlocking {
        favoriteDao.insert(FavoriteEntity(1))
        favoriteDao.insert(FavoriteEntity(2))

        assertEquals(setOf(1, 2), favoriteDao.getAllFavoriteIds().toSet())
    }

    @Test
    fun deleteFavoriteByIdRemovesOnlyThatId() = runBlocking {
        favoriteDao.insertAll(listOf(FavoriteEntity(1), FavoriteEntity(2), FavoriteEntity(3)))

        favoriteDao.deleteById(2)

        assertEquals(setOf(1, 3), favoriteDao.getAllFavoriteIds().toSet())
    }

    @Test
    fun replaceAllSwapsTheWholeFavoriteCache() = runBlocking {
        favoriteDao.insertAll(listOf(FavoriteEntity(1), FavoriteEntity(2)))

        favoriteDao.replaceAll(listOf(FavoriteEntity(3), FavoriteEntity(4), FavoriteEntity(5)))

        assertEquals(setOf(3, 4, 5), favoriteDao.getAllFavoriteIds().toSet())
    }

    @Test
    fun replaceAllWithEmptyListClearsTheFavoriteCache() = runBlocking {
        favoriteDao.insertAll(listOf(FavoriteEntity(1), FavoriteEntity(2)))

        favoriteDao.replaceAll(emptyList())

        assertEquals(emptySet<Int>(), favoriteDao.getAllFavoriteIds().toSet())
    }

    @Test
    fun tvReplaceAllSwapsTheWholeFavoriteCache() = runBlocking {
        tvFavoriteDao.insertAll(listOf(TvFavoriteEntity(1), TvFavoriteEntity(2)))

        tvFavoriteDao.replaceAll(listOf(TvFavoriteEntity(9)))

        assertEquals(setOf(9), tvFavoriteDao.getAllFavoriteIds().toSet())
    }

    @Test
    fun recentSearchesComeBackNewestFirst() = runBlocking {
        recentSearchDao.upsertIgnoringCase(RecentSearchEntity("oldest", 1L), keep = 10)
        recentSearchDao.upsertIgnoringCase(RecentSearchEntity("newest", 3L), keep = 10)
        recentSearchDao.upsertIgnoringCase(RecentSearchEntity("middle", 2L), keep = 10)

        assertEquals(
                listOf("newest", "middle", "oldest"),
                recentSearchDao.getRecentSearches(limit = 10)
                    )
    }

    @Test
    fun reSearchingIgnoringCaseBumpsInsteadOfDuplicating() = runBlocking {
        recentSearchDao.upsertIgnoringCase(RecentSearchEntity("Batman", 1L), keep = 10)
        recentSearchDao.upsertIgnoringCase(RecentSearchEntity("batman", 2L), keep = 10)

        assertEquals(listOf("batman"), recentSearchDao.getRecentSearches(limit = 10))
    }

    // Guards the leak where the table kept every query ever typed because
    // only the read side was limited.
    @Test
    fun recentSearchesAreTrimmedToTheKeepLimit() = runBlocking {
        repeat(15) { index ->
            recentSearchDao.upsertIgnoringCase(
                    RecentSearchEntity("query$index", index.toLong()),
                    keep = 10
                                              )
        }

        val stored = recentSearchDao.getRecentSearches(limit = 100)

        assertEquals(10, stored.size)
        assertEquals("query14", stored.first())
        assertEquals("query5", stored.last())
    }
}
