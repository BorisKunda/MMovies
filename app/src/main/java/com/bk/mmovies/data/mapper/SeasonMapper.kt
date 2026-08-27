package com.bk.mmovies.data.mapper

import com.bk.mmovies.data.source.remote.CAST_PROFILE_PATH_SIZE_SEGMENT
import com.bk.mmovies.data.source.remote.POSTER_PATH_SIZE_SEGMENT_LIST_ITEM
import com.bk.mmovies.data.source.remote.STILL_PATH_SIZE_SEGMENT
import com.bk.mmovies.data.source.remote.TMDB_IMAGE_BASE_URL
import com.bk.mmovies.data.source.remote.dto.CrewMemberDto
import com.bk.mmovies.data.source.remote.dto.EpisodeDto
import com.bk.mmovies.data.source.remote.dto.SeasonDetailsDto
import com.bk.mmovies.data.source.remote.dto.SeasonSummaryDto
import com.bk.mmovies.domain.model.CastMemberModel
import com.bk.mmovies.domain.model.EpisodeModel
import com.bk.mmovies.domain.model.SeasonModel
import com.bk.mmovies.locale.AppLanguage
import com.bk.mmovies.locale.LocaleMonitor
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

private const val DIRECTOR_JOB = "Director"
private val WRITER_JOBS = setOf("Writer", "Screenplay", "Story")

class SeasonMapper @Inject constructor(
        private val localeMonitor: LocaleMonitor
                                      ) {

    fun toModel(dto: SeasonDetailsDto): SeasonModel = SeasonModel(
            id = dto.id ?: 0,
            name = dto.name ?: "",
            overview = dto.overview ?: "",
            posterUrl = dto.posterPath?.let { getFullImageUrl(it) } ?: "",
            airDate = dto.airDate?.let { getFormattedDate(it) } ?: "",
            seasonNumber = dto.seasonNumber ?: 0,
            rating = dto.voteAverage.toRatingPercent(),
            episodeCountLabel = dto.episodes.orEmpty().size.toEpisodeCountLabel(),
            episodes = dto.episodes.orEmpty().map { toModel(it) })

    fun toModel(dto: SeasonSummaryDto): SeasonModel = SeasonModel(
            id = dto.id ?: 0,
            name = dto.name ?: "",
            overview = dto.overview ?: "",
            posterUrl = dto.posterPath?.let { getFullImageUrl(it) } ?: "",
            airDate = dto.airDate?.let { getFormattedDate(it) } ?: "",
            seasonNumber = dto.seasonNumber ?: 0,
            rating = dto.voteAverage.toRatingPercent(),
            episodeCountLabel = (dto.episodeCount ?: 0).toEpisodeCountLabel())

    fun toModel(dto: EpisodeDto): EpisodeModel = EpisodeModel(
            id = dto.id ?: 0,
            name = dto.name ?: "",
            overview = dto.overview ?: "",
            airDate = dto.airDate?.let { getFormattedDate(it) } ?: "",
            episodeNumber = dto.episodeNumber ?: 0,
            seasonNumber = dto.seasonNumber ?: 0,
            stillUrl = dto.stillPath?.let { getFullImageUrl(it, STILL_PATH_SIZE_SEGMENT) } ?: "",
            rating = dto.voteAverage.toRatingPercent(),
            runtime = dto.runtime.toFormattedRuntime(),
            director = dto.crew.toCrewModels(setOf(DIRECTOR_JOB)).firstOrNull(),
            writers = dto.crew.toCrewModels(WRITER_JOBS))

    // Crew credits list every job (editor, composer, etc.); only the
    // director/writer names are shown, keyed by TMDB's job labels.
    private fun List<CrewMemberDto>?.toCrewModels(jobs: Set<String>): List<CastMemberModel> = this
            ?.filter { it.job in jobs }
            ?.distinctBy { it.id }
            ?.mapNotNull { crewMemberDto ->
                val id = crewMemberDto.id ?: return@mapNotNull null
                val name = crewMemberDto.name ?: return@mapNotNull null
                CastMemberModel(
                        id = id,
                        name = name,
                        character = crewMemberDto.job ?: "",
                        profileUrl = crewMemberDto.profilePath
                                ?.let { getFullImageUrl(it, CAST_PROFILE_PATH_SIZE_SEGMENT) } ?: ""
                                )
            } ?: emptyList()

    private fun Double?.toRatingPercent(): Int = this?.let { (it * 10).toInt() } ?: 0

    private fun Int.toEpisodeCountLabel(): String {
        if (this <= 0) return ""
        return when (localeMonitor.currentLanguage.value) {
            AppLanguage.RUSSIAN -> "$this ${toRussianEpisodesWord()}"
            AppLanguage.HEBREW  -> if (this == 1) "פרק אחד" else "$this פרקים"
            AppLanguage.ENGLISH -> if (this == 1) "1 Episode" else "$this Episodes"
        }
    }

    // Russian counting nouns decline by the last digit (with 11-14 always
    // taking the plural form), unlike English/Hebrew's simple one-vs-many split.
    private fun Int.toRussianEpisodesWord(): String {
        val lastTwoDigits = this % 100
        if (lastTwoDigits in 11..14) return "эпизодов"
        return when (this % 10) {
            1       -> "эпизод"
            2, 3, 4 -> "эпизода"
            else    -> "эпизодов"
        }
    }

    // Episodes are almost always under an hour, so the hours component has to
    // drop out entirely rather than render a literal "0h 45m" / "0 שעות".
    private fun Int?.toFormattedRuntime(): String {
        val totalMinutes = this?.takeIf { it > 0 } ?: return ""
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return when (localeMonitor.currentLanguage.value) {
            AppLanguage.RUSSIAN -> when {
                hours == 0   -> "${minutes}мин"
                minutes == 0 -> "${hours}ч"
                else         -> "${hours}ч ${minutes}мин"
            }
            AppLanguage.HEBREW  -> when {
                hours == 0   -> minutes.toHebrewMinutesLabel()
                minutes == 0 -> hours.toHebrewHoursLabel()
                else         -> "${hours.toHebrewHoursLabel()} ${minutes.toHebrewMinutesLabel()}"
            }
            AppLanguage.ENGLISH -> when {
                hours == 0   -> "${minutes}m"
                minutes == 0 -> "${hours}h"
                else         -> "${hours}h ${minutes}m"
            }
        }
    }

    // Hebrew grammar: 1 hour and 2 hours have their own words rather than a
    // number, unlike every other count which prefixes the number as usual.
    private fun Int.toHebrewHoursLabel(): String = when (this) {
        1 -> "שעה"
        2 -> "שעתיים"
        else -> "$this שעות"
    }

    private fun Int.toHebrewMinutesLabel(): String =
            if (this == 1) "דקה אחת" else "$this דקות"

    private fun getFormattedDate(airDate: String): String = try {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(airDate)
        date?.let {
            SimpleDateFormat("MMMM d, yyyy", localeMonitor.currentLanguage.value.locale).format(it)
        } ?: airDate
    } catch (e: ParseException) {
        airDate
    }

    private fun getFullImageUrl(
            imagePath: String,
            sizeSegment: String = POSTER_PATH_SIZE_SEGMENT_LIST_ITEM
                                ): String {
        val stringBuilder = StringBuilder()
        stringBuilder.apply {
            append(TMDB_IMAGE_BASE_URL)
            append(sizeSegment)
            append(imagePath)
        }
        return stringBuilder.toString()
    }
}
