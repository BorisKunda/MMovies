package com.bk.mmovies.data.source.local.preferences

import android.content.Context
import androidx.core.content.edit
import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.domain.model.CatalogMediaType
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

data class SavedRecommendations(
        val favoriteKeys: Set<Pair<Int, CatalogMediaType>>,
        val items: List<CatalogItem>
                               )

@Singleton
class RecommendationsDiskCacheSharedPrefs @Inject constructor(
        @ApplicationContext context: Context
                                                             ) {

    private val sharedPreferences =
            context.getSharedPreferences(
                    PREFERENCES_NAME,
                    Context.MODE_PRIVATE
                                        )

    fun load(accountId: Int): SavedRecommendations? {
        val json = sharedPreferences.getString(
                keyFor(accountId),
                null
                                              ) ?: return null
        return runCatching { decode(json) }.getOrNull()
    }

    fun save(
            accountId: Int,
            favoriteKeys: Set<Pair<Int, CatalogMediaType>>,
            items: List<CatalogItem>
            ) {
        sharedPreferences.edit {
            putString(
                    keyFor(accountId),
                    encode(
                            favoriteKeys,
                            items
                          )
                     )
        }
    }

    fun clear(accountId: Int) {
        sharedPreferences.edit { remove(keyFor(accountId)) }
    }

    fun clearAll() {
        sharedPreferences.edit { clear() }
    }

    private fun keyFor(accountId: Int) = "$KEY_PREFIX$accountId"

    private fun encode(
            favoriteKeys: Set<Pair<Int, CatalogMediaType>>,
            items: List<CatalogItem>
                      ): String {
        val favoritesJson = JSONArray()
        favoriteKeys.forEach { (id, mediaType) ->
            favoritesJson.put(
                    JSONObject()
                            .put(
                                    FIELD_ID,
                                    id
                                )
                            .put(
                                    FIELD_MEDIA_TYPE,
                                    mediaType.name
                                )
                             )
        }
        val itemsJson = JSONArray()
        items.forEach { item ->
            itemsJson.put(
                    JSONObject()
                            .put(
                                    FIELD_ID,
                                    item.id
                                )
                            .put(
                                    FIELD_TITLE,
                                    item.title
                                )
                            .put(
                                    FIELD_IMAGE_URL,
                                    item.imageUrl
                                )
                            .put(
                                    FIELD_BACKDROP_URL,
                                    item.backdropUrl
                                )
                            .put(
                                    FIELD_RELEASE_DATE,
                                    item.releaseDate
                                )
                            .put(
                                    FIELD_RELEASE_DATE_ISO,
                                    item.releaseDateIso
                                )
                            .put(
                                    FIELD_MEDIA_TYPE,
                                    item.mediaType.name
                                )
                            .put(
                                    FIELD_RATING,
                                    item.rating
                                )
                            .put(
                                    FIELD_IS_FAVORITE,
                                    item.isFavorite
                                )
                         )
        }
        return JSONObject()
                .put(
                        FIELD_FAVORITES,
                        favoritesJson
                    )
                .put(
                        FIELD_ITEMS,
                        itemsJson
                    )
                .toString()
    }

    private fun decode(json: String): SavedRecommendations {
        val root = JSONObject(json)
        val favoritesJson = root.getJSONArray(FIELD_FAVORITES)
        val favoriteKeys = (0 until favoritesJson.length()).map { index ->
            val entry = favoritesJson.getJSONObject(index)
            entry.getInt(FIELD_ID) to CatalogMediaType.valueOf(entry.getString(FIELD_MEDIA_TYPE))
        }.toSet()
        val itemsJson = root.getJSONArray(FIELD_ITEMS)
        val items = (0 until itemsJson.length()).map { index ->
            val entry = itemsJson.getJSONObject(index)
            CatalogItem(
                    id = entry.getInt(FIELD_ID),
                    title = entry.getString(FIELD_TITLE),
                    imageUrl = entry.getString(FIELD_IMAGE_URL),
                    backdropUrl = entry.getString(FIELD_BACKDROP_URL),
                    releaseDate = entry.getString(FIELD_RELEASE_DATE),
                    releaseDateIso = entry.getString(FIELD_RELEASE_DATE_ISO),
                    mediaType = CatalogMediaType.valueOf(entry.getString(FIELD_MEDIA_TYPE)),
                    rating = entry.getInt(FIELD_RATING),
                    isFavorite = entry.getBoolean(FIELD_IS_FAVORITE)
                       )
        }
        return SavedRecommendations(
                favoriteKeys,
                items
                                   )
    }

    private companion object {
        const val PREFERENCES_NAME = "recommendations_disk_cache_preferences"
        const val KEY_PREFIX = "recommendations_"
        const val FIELD_FAVORITES = "favorites"
        const val FIELD_ITEMS = "items"
        const val FIELD_ID = "id"
        const val FIELD_TITLE = "title"
        const val FIELD_IMAGE_URL = "imageUrl"
        const val FIELD_BACKDROP_URL = "backdropUrl"
        const val FIELD_RELEASE_DATE = "releaseDate"
        const val FIELD_RELEASE_DATE_ISO = "releaseDateIso"
        const val FIELD_MEDIA_TYPE = "mediaType"
        const val FIELD_RATING = "rating"
        const val FIELD_IS_FAVORITE = "isFavorite"
    }
}
