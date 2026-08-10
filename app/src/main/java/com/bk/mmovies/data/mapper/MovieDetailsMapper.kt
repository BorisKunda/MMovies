package com.bk.mmovies.data.mapper

import com.bk.mmovies.data.source.remote.POSTER_PATH_SIZE_SEGMENT_LIST_ITEM
import com.bk.mmovies.data.source.remote.POSTER_PATH_SIZE_SEGMENT_ZOOM
import com.bk.mmovies.data.source.remote.TMDB_IMAGE_BASE_URL
import com.bk.mmovies.data.source.remote.dto.MovieDetailsDto
import com.bk.mmovies.domain.model.MovieDetailsModel
import com.bk.mmovies.locale.AppLanguage
import com.bk.mmovies.locale.LocaleMonitor
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class MovieDetailsMapper @Inject constructor(
        private val localeMonitor: LocaleMonitor
                                             ) {

    fun toModel(dto: MovieDetailsDto): MovieDetailsModel = MovieDetailsModel(
            id = dto.id ?: 0,
            title = dto.title ?: "",
            posterUrl = dto.posterPath?.let { getFullImageUrl(it, POSTER_PATH_SIZE_SEGMENT_LIST_ITEM) } ?: "",
            backdropUrl = dto.backdropPath?.let { getFullImageUrl(it, POSTER_PATH_SIZE_SEGMENT_ZOOM) } ?: "",
            releaseDate = dto.releaseDate?.let { getFormattedDate(it) } ?: "",
            runtime = dto.runtime.toFormattedRuntime(),
            userScore = dto.voteAverage.toRatingPercent(),
            genres = dto.genres?.mapNotNull { it.name } ?: emptyList(),
            overview = dto.overview ?: ""
                                                                          )

    private fun Double?.toRatingPercent(): Int = this?.let { (it * 10).toInt() } ?: 0

    private fun Int?.toFormattedRuntime(): String {
        val totalMinutes = this?.takeIf { it > 0 } ?: return ""
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return when (localeMonitor.currentLanguage.value) {
            AppLanguage.RUSSIAN -> "${hours}ч ${minutes}мин"
            AppLanguage.HEBREW -> "${hours.toHebrewHoursLabel()} $minutes דקות"
            AppLanguage.ENGLISH -> "${hours}h ${minutes}m"
        }
    }

    // Hebrew grammar: 1 hour and 2 hours have their own words rather than a
    // number, unlike every other count which prefixes the number as usual.
    private fun Int.toHebrewHoursLabel(): String = when (this) {
        1 -> "שעה"
        2 -> "שעתיים"
        else -> "$this שעות"
    }


    private fun getFormattedDate(releaseDate: String): String = try {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(releaseDate)
        date?.let {
            SimpleDateFormat("MMMM d, yyyy", localeMonitor.currentLanguage.value.locale).format(it)
        } ?: releaseDate
    } catch (e: ParseException) {
        releaseDate
    }

    private fun getFullImageUrl(imagePath: String, sizeSegment: String): String {
        val stringBuilder = StringBuilder()
        stringBuilder.apply {
            append(TMDB_IMAGE_BASE_URL)
            append(sizeSegment)
            append(imagePath)
        }
        return stringBuilder.toString()
    }
}
