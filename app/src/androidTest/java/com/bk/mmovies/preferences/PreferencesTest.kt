package com.bk.mmovies.preferences

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bk.mmovies.data.source.local.preferences.ApiKeyStorage
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ApiKeyStorageTest {

    private lateinit var apiKeyStorage: ApiKeyStorage

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        apiKeyStorage = ApiKeyStorage(context)
        apiKeyStorage.clearStorage()
    }

    @Test
    fun testGetApiKeyReturnsNullIfNoKeySaved() {
        val result = apiKeyStorage.getApiKey()
        assertNull(result)
    }

    @Test
    fun testSaveKey() {
        val dummyKey = "123"
        apiKeyStorage.saveApiKey(dummyKey)
        val savedKey = apiKeyStorage.getApiKey()
        assertEquals(
                dummyKey,
                savedKey
                    )
        apiKeyStorage.clearStorage()
    }

    @Test
    fun testRemoveKey() {
        val dummyKey = "123"
        apiKeyStorage.saveApiKey(dummyKey)
        apiKeyStorage.removeApiKey()
        val savedKey = apiKeyStorage.getApiKey()
        assertNull(savedKey)
    }

    @After
    fun tearDown() {
        apiKeyStorage.clearStorage()
    }
}