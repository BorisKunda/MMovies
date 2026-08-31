package com.bk.mmovies.data.mapper

import com.bk.mmovies.data.source.remote.TMDB_IMAGE_BASE_URL

// Shared by every mapper that turns a TMDB relative image path into a full
// URL, so the six near-identical StringBuilder-based copies that used to
// live in each mapper file collapse into one.
internal fun getFullImageUrl(sizeSegment: String, imagePath: String): String =
        "$TMDB_IMAGE_BASE_URL$sizeSegment$imagePath"

// TMDB's vote_average is 0-10; every mapper that surfaces a user score
// renders it as a 0-100 percent instead.
internal fun Double?.toRatingPercent(): Int = this?.let { (it * 10).toInt() } ?: 0
