package com.bk.mmovies.preferences

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bk.mmovies.data.source.local.preferences.GeminiModelAvailabilitySharedPrefs
import com.bk.mmovies.data.source.remote.GEMINI_MODELS
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GeminiModelAvailabilitySharedPrefsTest {

    private lateinit var modelAvailabilityStorage: GeminiModelAvailabilitySharedPrefs

    private val firstModel = GEMINI_MODELS.first()
    private val secondModel = GEMINI_MODELS[1]
    private val today = 20_000L

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        modelAvailabilityStorage = GeminiModelAvailabilitySharedPrefs(context)
        modelAvailabilityStorage.resetAll()
    }

    @Test
    fun testGetLimitReachedDayReturnsZeroIfNothingSaved() {
        assertEquals(
                0L,
                modelAvailabilityStorage.getLimitReachedDay(firstModel)
                    )
    }

    @Test
    fun testModelIsAvailableIfNoLimitHit() {
        assertTrue(modelAvailabilityStorage.isAvailable(firstModel, today))
    }

    @Test
    fun testMarkDailyLimitReachedSavesDay() {
        modelAvailabilityStorage.markDailyLimitReached(firstModel, today)
        assertEquals(
                today,
                modelAvailabilityStorage.getLimitReachedDay(firstModel)
                    )
    }

    @Test
    fun testModelIsUnavailableOnSameDayAsLimit() {
        modelAvailabilityStorage.markDailyLimitReached(firstModel, today)
        assertFalse(modelAvailabilityStorage.isAvailable(firstModel, today))
    }

    @Test
    fun testModelIsAvailableAgainOnLaterDayAndEntryIsForgotten() {
        modelAvailabilityStorage.markDailyLimitReached(firstModel, today)
        assertTrue(modelAvailabilityStorage.isAvailable(firstModel, today + 1))
        assertEquals(
                0L,
                modelAvailabilityStorage.getLimitReachedDay(firstModel)
                    )
    }

    @Test
    fun testModelIsAvailableIfClockMovedBackwardsAndEntryIsForgotten() {
        modelAvailabilityStorage.markDailyLimitReached(firstModel, today)
        assertTrue(modelAvailabilityStorage.isAvailable(firstModel, today - 1))
        assertEquals(
                0L,
                modelAvailabilityStorage.getLimitReachedDay(firstModel)
                    )
    }

    @Test
    fun testLimitOfOneModelDoesNotAffectAnother() {
        modelAvailabilityStorage.markDailyLimitReached(firstModel, today)
        assertTrue(modelAvailabilityStorage.isAvailable(secondModel, today))
    }

    @Test
    fun testGetAllReturnsEveryKnownModelWithAvailability() {
        modelAvailabilityStorage.markDailyLimitReached(firstModel, today)
        val allModels = modelAvailabilityStorage.getAll(today)
        assertEquals(
                GEMINI_MODELS,
                allModels.keys.toList()
                    )
        assertFalse(allModels.getValue(firstModel))
        assertTrue(allModels.getValue(secondModel))
    }

    @Test
    fun testGetAvailableModelsSkipsLimitedModelsAndKeepsOrder() {
        modelAvailabilityStorage.markDailyLimitReached(firstModel, today)
        assertEquals(
                GEMINI_MODELS - firstModel,
                modelAvailabilityStorage.getAvailableModels(today)
                    )
    }

    @Test
    fun testResetAllMakesEveryModelAvailable() {
        GEMINI_MODELS.forEach { modelAvailabilityStorage.markDailyLimitReached(it, today) }
        modelAvailabilityStorage.resetAll()
        assertEquals(
                GEMINI_MODELS,
                modelAvailabilityStorage.getAvailableModels(today)
                    )
    }

    @After
    fun tearDown() {
        modelAvailabilityStorage.resetAll()
    }
}
