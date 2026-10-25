package com.bk.mmovies.demomobile.ui.screen.catalog

import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.domain.model.CatalogMediaType

private const val MOCK_POSTER_URI_PREFIX = "android.resource://com.bk.mmovies.demomobile/drawable/"

val mockMovieCatalogItems: List<CatalogItem> = listOf(
    CatalogItem(
        id = 205301,
        title = "Last Orbit",
        imageUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_last_orbit",
        backdropUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_last_orbit",
        releaseDate = "2000-12-13",
        releaseDateIso = "2000-12-13",
        mediaType = CatalogMediaType.MOVIE,
        rating = 24,
        isFavorite = false
    ),
    CatalogItem(
        id = 535637,
        title = "Midnight Rain",
        imageUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_midnight_rain",
        backdropUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_midnight_rain",
        releaseDate = "2003-09-02",
        releaseDateIso = "2003-09-02",
        mediaType = CatalogMediaType.MOVIE,
        rating = 77,
        isFavorite = false
    ),
    CatalogItem(
        id = 756242,
        title = "The Emerald Gate",
        imageUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_the_emerald_gate",
        backdropUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_the_emerald_gate",
        releaseDate = "2006-02-01",
        releaseDateIso = "2006-02-01",
        mediaType = CatalogMediaType.MOVIE,
        rating = 22,
        isFavorite = false
    ),
    CatalogItem(
        id = 341900,
        title = "Summer in Bloom",
        imageUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_summer_in_bloom",
        backdropUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_summer_in_bloom",
        releaseDate = "2005-08-26",
        releaseDateIso = "2005-08-26",
        mediaType = CatalogMediaType.MOVIE,
        rating = 40,
        isFavorite = false
    ),
    CatalogItem(
        id = 182167,
        title = "Below the Surface",
        imageUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_below_the_surface",
        backdropUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_below_the_surface",
        releaseDate = "2025-10-01",
        releaseDateIso = "2025-10-01",
        mediaType = CatalogMediaType.MOVIE,
        rating = 95,
        isFavorite = false
    ),
    CatalogItem(
        id = 163753,
        title = "Dust and Thunder",
        imageUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_dust_and_thunder",
        backdropUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_dust_and_thunder",
        releaseDate = "2023-08-14",
        releaseDateIso = "2023-08-14",
        mediaType = CatalogMediaType.MOVIE,
        rating = 10,
        isFavorite = false
    ),
    CatalogItem(
        id = 132165,
        title = "Little Cloud",
        imageUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_little_cloud",
        backdropUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_little_cloud",
        releaseDate = "2025-07-17",
        releaseDateIso = "2025-07-17",
        mediaType = CatalogMediaType.MOVIE,
        rating = 76,
        isFavorite = false
    ),
    CatalogItem(
        id = 630484,
        title = "The Final Note",
        imageUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_the_final_note",
        backdropUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_the_final_note",
        releaseDate = "2006-02-22",
        releaseDateIso = "2006-02-22",
        mediaType = CatalogMediaType.MOVIE,
        rating = 67,
        isFavorite = false
    ),
    CatalogItem(
        id = 145013,
        title = "Frozen Signal",
        imageUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_frozen_signal",
        backdropUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_frozen_signal",
        releaseDate = "2022-01-05",
        releaseDateIso = "2022-01-05",
        mediaType = CatalogMediaType.MOVIE,
        rating = 63,
        isFavorite = false
    ),
    CatalogItem(
        id = 856672,
        title = "Run at Dawn",
        imageUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_run_at_dawn",
        backdropUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_run_at_dawn",
        releaseDate = "2022-05-21",
        releaseDateIso = "2022-05-21",
        mediaType = CatalogMediaType.MOVIE,
        rating = 12,
        isFavorite = false
    )
)

val mockTvSeriesCatalogItems: List<CatalogItem> = listOf(
    CatalogItem(
        id = 998223,
        title = "Harbor Secrets",
        imageUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_harbor_secrets",
        backdropUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_harbor_secrets",
        releaseDate = "2023-12-09",
        releaseDateIso = "2023-12-09",
        mediaType = CatalogMediaType.TV_SERIES,
        rating = 8,
        isFavorite = false
    ),
    CatalogItem(
        id = 188711,
        title = "Colony Nine",
        imageUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_colony_nine",
        backdropUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_colony_nine",
        releaseDate = "2014-12-21",
        releaseDateIso = "2014-12-21",
        mediaType = CatalogMediaType.TV_SERIES,
        rating = 63,
        isFavorite = false
    ),
    CatalogItem(
        id = 827895,
        title = "The Last Dynasty",
        imageUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_the_last_dynasty",
        backdropUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_the_last_dynasty",
        releaseDate = "2011-12-17",
        releaseDateIso = "2011-12-17",
        mediaType = CatalogMediaType.TV_SERIES,
        rating = 97,
        isFavorite = false
    ),
    CatalogItem(
        id = 928646,
        title = "Apartment 404",
        imageUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_apartment_404",
        backdropUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_apartment_404",
        releaseDate = "2013-09-10",
        releaseDateIso = "2013-09-10",
        mediaType = CatalogMediaType.TV_SERIES,
        rating = 47,
        isFavorite = false
    ),
    CatalogItem(
        id = 997924,
        title = "Wildwood Academy",
        imageUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_wildwood_academy",
        backdropUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_wildwood_academy",
        releaseDate = "2026-02-02",
        releaseDateIso = "2026-02-02",
        mediaType = CatalogMediaType.TV_SERIES,
        rating = 40,
        isFavorite = false
    ),
    CatalogItem(
        id = 390875,
        title = "Code of Silence",
        imageUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_code_of_silence",
        backdropUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_code_of_silence",
        releaseDate = "2014-07-28",
        releaseDateIso = "2014-07-28",
        mediaType = CatalogMediaType.TV_SERIES,
        rating = 24,
        isFavorite = false
    ),
    CatalogItem(
        id = 158754,
        title = "Second Shift",
        imageUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_second_shift",
        backdropUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_second_shift",
        releaseDate = "2024-06-13",
        releaseDateIso = "2024-06-13",
        mediaType = CatalogMediaType.TV_SERIES,
        rating = 87,
        isFavorite = false
    ),
    CatalogItem(
        id = 257388,
        title = "Beyond the Ridge",
        imageUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_beyond_the_ridge",
        backdropUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_beyond_the_ridge",
        releaseDate = "2004-09-23",
        releaseDateIso = "2004-09-23",
        mediaType = CatalogMediaType.TV_SERIES,
        rating = 66,
        isFavorite = false
    ),
    CatalogItem(
        id = 596727,
        title = "Starlight Diner",
        imageUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_starlight_diner",
        backdropUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_starlight_diner",
        releaseDate = "2011-08-05",
        releaseDateIso = "2011-08-05",
        mediaType = CatalogMediaType.TV_SERIES,
        rating = 99,
        isFavorite = false
    ),
    CatalogItem(
        id = 335315,
        title = "The Clockmaker",
        imageUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_the_clockmaker",
        backdropUrl = MOCK_POSTER_URI_PREFIX + "mock_poster_the_clockmaker",
        releaseDate = "2004-01-18",
        releaseDateIso = "2004-01-18",
        mediaType = CatalogMediaType.TV_SERIES,
        rating = 53,
        isFavorite = false
    )
)