package com.bk.mmovies.preferences

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bk.mmovies.data.source.local.preferences.RecommendationsDiskCacheSharedPrefs
import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.domain.model.CatalogMediaType
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecommendationsDiskCacheSharedPrefsTest {

    private lateinit var recommendationsStorage: RecommendationsDiskCacheSharedPrefs

    private val firstAccountId = 1
    private val secondAccountId = 2

    private val dummyFavoriteKeys = setOf(
            10 to CatalogMediaType.MOVIE,
            20 to CatalogMediaType.TV_SERIES
                                         )

    private val dummyItems = listOf(
            CatalogItem(
                    id = 10,
                    title = "Dummy Movie",
                    imageUrl = "https://example.com/poster.jpg",
                    backdropUrl = "https://example.com/backdrop.jpg",
                    releaseDate = "Jan 1, 2020",
                    releaseDateIso = "2020-01-01",
                    mediaType = CatalogMediaType.MOVIE,
                    rating = 8,
                    isFavorite = true
                       ),
            CatalogItem(
                    id = 20,
                    title = "Dummy Series",
                    imageUrl = "https://example.com/series.jpg",
                    mediaType = CatalogMediaType.TV_SERIES
                       )
                                   )

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        recommendationsStorage = RecommendationsDiskCacheSharedPrefs(context)
        recommendationsStorage.clearAll()
    }

    @Test
    fun testLoadReturnsNullIfNothingSaved() {
        assertNull(recommendationsStorage.load(firstAccountId))
    }

    @Test
    fun testSaveAndLoadRestoresFavoritesAndItems() {
        recommendationsStorage.save(
                firstAccountId,
                dummyFavoriteKeys,
                dummyItems
                                   )
        val loaded = recommendationsStorage.load(firstAccountId)
        assertNotNull(loaded)
        assertEquals(
                dummyFavoriteKeys,
                loaded!!.favoriteKeys
                    )
        assertEquals(
                dummyItems,
                loaded.items
                    )
    }

    @Test
    fun testSaveWithEmptyListsLoadsEmptyListsNotNull() {
        recommendationsStorage.save(
                firstAccountId,
                emptySet(),
                emptyList()
                                   )
        val loaded = recommendationsStorage.load(firstAccountId)
        assertNotNull(loaded)
        assertEquals(
                emptySet<Pair<Int, CatalogMediaType>>(),
                loaded!!.favoriteKeys
                    )
        assertEquals(
                emptyList<CatalogItem>(),
                loaded.items
                    )
    }

    @Test
    fun testSaveOverwritesPreviousValueForSameAccount() {
        recommendationsStorage.save(
                firstAccountId,
                dummyFavoriteKeys,
                dummyItems
                                   )
        recommendationsStorage.save(
                firstAccountId,
                emptySet(),
                dummyItems.take(1)
                                   )
        val loaded = recommendationsStorage.load(firstAccountId)
        assertEquals(
                dummyItems.take(1),
                loaded!!.items
                    )
        assertEquals(
                emptySet<Pair<Int, CatalogMediaType>>(),
                loaded.favoriteKeys
                    )
    }

    @Test
    fun testAccountsAreStoredSeparately() {
        recommendationsStorage.save(
                firstAccountId,
                dummyFavoriteKeys,
                dummyItems
                                   )
        assertNull(recommendationsStorage.load(secondAccountId))
    }

    @Test
    fun testClearRemovesOnlyThatAccount() {
        recommendationsStorage.save(
                firstAccountId,
                dummyFavoriteKeys,
                dummyItems
                                   )
        recommendationsStorage.save(
                secondAccountId,
                dummyFavoriteKeys,
                dummyItems
                                   )
        recommendationsStorage.clear(firstAccountId)
        assertNull(recommendationsStorage.load(firstAccountId))
        assertNotNull(recommendationsStorage.load(secondAccountId))
    }

    @Test
    fun testClearAllRemovesEveryAccount() {
        recommendationsStorage.save(
                firstAccountId,
                dummyFavoriteKeys,
                dummyItems
                                   )
        recommendationsStorage.save(
                secondAccountId,
                dummyFavoriteKeys,
                dummyItems
                                   )
        recommendationsStorage.clearAll()
        assertNull(recommendationsStorage.load(firstAccountId))
        assertNull(recommendationsStorage.load(secondAccountId))
    }

    @After
    fun tearDown() {
        recommendationsStorage.clearAll()
    }
}
