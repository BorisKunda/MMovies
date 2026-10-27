package com.bk.mmovies.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.bk.mmovies.data.source.remote.TMDB_IMAGE_BASE_URL
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle
import java.util.Date
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

// Shared by every mapper that reformats a TMDB "yyyy-MM-dd" date into the
// user-facing "MMMM d, yyyy" label for the current app locale.
internal fun formatTmdbDate(rawDate: String, locale: Locale): String? = try {
    val date = LocalDate.parse(rawDate, TMDB_DATE_FORMATTER)
    formatLongDate(date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(), locale, ZoneOffset.UTC.id)
} catch (e: DateTimeParseException) {
    null
}

// NewsAPI sends publishedAt as a full ISO-8601 UTC instant
// ("2024-01-15T13:45:00Z"), not a bare date like TMDB - a separate parser
// from formatTmdbDate is needed rather than reusing it.
internal fun formatNewsApiDate(rawDate: String, locale: Locale): String? = try {
    val instant = Instant.parse(rawDate)
    formatLongDate(instant.toEpochMilli(), locale, ZoneId.systemDefault().id)
} catch (e: DateTimeParseException) {
    null
}

private val fallbackLongDateFormatters = ConcurrentHashMap<Locale, DateTimeFormatter>()

private val icuLongDateFormats = ThreadLocal<HashMap<Pair<Locale, String>, android.icu.text.DateFormat>>()

// java.time on Android lacks the genitive month names some languages need
// in a full date (Russian "29 июля", not "29 июль"); ICU, available from
// API 24, has them.
private fun formatLongDate(epochMillis: Long, locale: Locale, zoneId: String): String =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            formatLongDateWithIcu(epochMillis, locale, zoneId)
        } else {
            val formatter = fallbackLongDateFormatters.getOrPut(locale) {
                DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(locale)
            }
            formatter.withZone(ZoneId.of(zoneId)).format(Instant.ofEpochMilli(epochMillis))
        }

@RequiresApi(Build.VERSION_CODES.N)
private fun formatLongDateWithIcu(epochMillis: Long, locale: Locale, zoneId: String): String {
    val formatsByKey = icuLongDateFormats.get() ?: HashMap<Pair<Locale, String>, android.icu.text.DateFormat>()
            .also { icuLongDateFormats.set(it) }
    val format = formatsByKey.getOrPut(locale to zoneId) {
        android.icu.text.DateFormat.getDateInstance(android.icu.text.DateFormat.LONG, locale).apply {
            timeZone = android.icu.util.TimeZone.getTimeZone(zoneId)
        }
    }
    return format.format(Date(epochMillis))
}
