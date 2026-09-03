package com.bk.mmovies.data.mapper

import com.bk.mmovies.data.source.remote.TMDB_IMAGE_BASE_URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

// Shared by every mapper that turns a TMDB relative image path into a full
// URL, so the six near-identical StringBuilder-based copies that used to
// live in each mapper file collapse into one.
internal fun getFullImageUrl(sizeSegment: String, imagePath: String): String =
        "$TMDB_IMAGE_BASE_URL$sizeSegment$imagePath"

// TMDB's vote_average is 0-10; every mapper that surfaces a user score
// renders it as a 0-100 percent instead.
internal fun Double?.toRatingPercent(): Int = this?.let { (it * 10).toInt() } ?: 0

// TMDB's crew/created_by gender field: 0 = not specified, 1 = female,
// 2 = male, 3 = non-binary. Only female has a distinct grammatical role
// label (e.g. Hebrew director/writer/creator); every other value falls back
// to the existing (male/neutral) label.
internal const val TMDB_GENDER_FEMALE = 1

// TMDB always sends dates as "yyyy-MM-dd" in English, regardless of the
// app's locale.
private val TMDB_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)

// DateTimeFormatter is immutable/thread-safe (unlike SimpleDateFormat), so a
// per-locale instance can be built once and reused for every item mapped
// instead of re-parsing the pattern/symbol tables on every call.
private val displayDateFormatters = ConcurrentHashMap<Locale, DateTimeFormatter>()

// Shared by every mapper that reformats a TMDB "yyyy-MM-dd" date into the
// user-facing "MMMM d, yyyy" label for the current app locale.
internal fun formatTmdbDate(rawDate: String, locale: Locale): String? = try {
    val date = LocalDate.parse(rawDate, TMDB_DATE_FORMATTER)
    val formatter = displayDateFormatters.getOrPut(locale) { DateTimeFormatter.ofPattern("MMMM d, yyyy", locale) }
    date.format(formatter)
} catch (e: DateTimeParseException) {
    null
}
