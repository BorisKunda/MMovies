package com.bk.mmovies.domain.model

/** Which TMDB media type an item came from. Favorite calls route on this. */
enum class CatalogMediaType { MOVIE, TV_SERIES }

data class CatalogItem( val id: Int,
                        val title: String,
                        val imageUrl: String,
                        val releaseDate: String,
                        // Raw TMDB ISO date (yyyy-MM-dd), kept alongside the
                        // locale-formatted releaseDate above purely so
                        // cross-page sorting compares real chronology instead
                        // of a display string (whose lexicographic order
                        // depends on locale and doesn't match date order).
                        val releaseDateIso: String = "",
                        // Carried on the item rather than inferred from the
                        // selected tab: a movie and a series can share an id,
                        // and toggling a favorite against the wrong repository
                        // silently favorites an unrelated title.
                        val mediaType: CatalogMediaType,
                        val rating: Int = 0,
                        val isFavorite: Boolean = false)

