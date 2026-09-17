package com.bk.mmovies.tv.ui.catalog

import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.domain.model.CatalogMediaType

// Placeholder data for exercising TvRow/TvItem before real catalog data is
// wired in. Every entry reuses the one known-good TMDB poster path already
// verified to load - fabricating other TMDB image hashes would just 404.
private const val DUMMY_POSTER_URL = "https://image.tmdb.org/t/p/w500/uTWhbLc7Bj4qNSdW3ZvZKL8cOHv.jpg"

val dummyCatalogItems: List<CatalogItem> = listOf(
        "Silo",
        "Echoes Within",
        "The River Bends",
        "Sand & Steel",
        "Deeper Skies",
        "The Reckoning",
        "Frozen Truth",
        "Neon District",
        "Orbital",
        "The Blackwood Files",
        "Citadel",
        "Ashes of Dawn",
        "The Quiet Ones",
        "Borderline",
        "Silent Orbit",
        "Iron Requiem",
        "Pale Horizon",
        "Crimson Vale",
        "The Wanderer",
        "Glass Kingdom"
                                                  ).mapIndexed { index, title ->
    CatalogItem(
            id = index + 1,
            title = title,
            imageUrl = DUMMY_POSTER_URL,
            mediaType = CatalogMediaType.MOVIE
               )
}
