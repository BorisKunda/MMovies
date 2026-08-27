package com.bk.mmovies.data.mapper

import com.bk.mmovies.data.source.remote.CAST_PROFILE_PATH_SIZE_SEGMENT
import com.bk.mmovies.data.source.remote.POSTER_PATH_SIZE_SEGMENT_LIST_ITEM
import com.bk.mmovies.data.source.remote.POSTER_PATH_SIZE_SEGMENT_LIST_ITEM_ZOOM
import com.bk.mmovies.data.source.remote.TMDB_IMAGE_BASE_URL
import com.bk.mmovies.data.source.remote.dto.CastMemberDto
import com.bk.mmovies.data.source.remote.dto.CrewMemberDto
import com.bk.mmovies.data.source.remote.dto.MovieDetailsDto
import com.bk.mmovies.data.source.remote.dto.VideoDto
import com.bk.mmovies.domain.model.CastMemberModel
import com.bk.mmovies.domain.model.MovieDetailsModel
import com.bk.mmovies.locale.AppLanguage
import com.bk.mmovies.locale.LocaleMonitor
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

private const val CAST_LIST_LIMIT = 20
private const val DIRECTOR_JOB = "Director"
private val WRITER_JOBS = setOf("Writer", "Screenplay", "Story")
private const val VIDEO_SITE_YOUTUBE = "YouTube"
private const val VIDEO_TYPE_TRAILER = "Trailer"
private const val YOUTUBE_WATCH_URL = "https://www.youtube.com/watch?v="

class MovieDetailsMapper @Inject constructor(
        private val localeMonitor: LocaleMonitor
                                             ) {

    fun toModel(dto: MovieDetailsDto): MovieDetailsModel = MovieDetailsModel(
            id = dto.id ?: 0,
            title = dto.title ?: "",
            posterUrl = dto.posterPath?.let { getFullImageUrl(it, POSTER_PATH_SIZE_SEGMENT_LIST_ITEM) } ?: "",
            backdropUrl = dto.backdropPath?.let { getFullImageUrl(it, POSTER_PATH_SIZE_SEGMENT_LIST_ITEM_ZOOM) } ?: "",
            releaseDate = dto.releaseDate?.let { getFormattedDate(it) } ?: "",
            runtime = dto.runtime.toFormattedRuntime(),
            userScore = dto.voteAverage.toRatingPercent(),
            genres = dto.genres?.mapNotNull { it.name } ?: emptyList(),
            overview = dto.overview ?: "",
            cast = dto.credits?.cast.toCastModels(),
            director = dto.credits?.crew.toCrewModels(DIRECTOR_JOB).firstOrNull(),
            writers = dto.credits?.crew.toCrewModels(WRITER_JOBS),
            isFavorite = dto.accountStates?.favorite ?: false,
            trailerUrl = dto.videos?.results.toTrailerUrl()
                                                                          )

    // TMDB returns the full credited cast, often 30+ names; the app only
    // shows a horizontal strip, so cap it to the leads (already sorted by
    // billing order) instead of rendering everyone.
    //
    // distinctBy is load-bearing: an actor credited for two roles appears
    // twice with the same person id, which would crash the LazyRow keyed on
    // that id ("Key was already used"). Dedupe before the take so the strip
    // still fills to CAST_LIST_LIMIT.
    private fun List<CastMemberDto>?.toCastModels(): List<CastMemberModel> = this
            ?.sortedBy { it.order ?: Int.MAX_VALUE }
            ?.distinctBy { it.id }
            ?.take(CAST_LIST_LIMIT)
            ?.mapNotNull { castMemberDto ->
                val id = castMemberDto.id ?: return@mapNotNull null
                val name = castMemberDto.name ?: return@mapNotNull null
                CastMemberModel(
                        id = id,
                        name = name,
                        character = castMemberDto.character ?: "",
                        profileUrl = castMemberDto.profilePath
                                ?.let { getFullImageUrl(it, CAST_PROFILE_PATH_SIZE_SEGMENT) } ?: ""
                                )
            } ?: emptyList()

    // Crew credits list every job (editor, composer, etc.); only the
    // director/writer names are shown, keyed by TMDB's job labels.
    private fun List<CrewMemberDto>?.toCrewModels(job: String): List<CastMemberModel> =
            toCrewModels(setOf(job))

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

    // A sub-hour (or exactly-N-hour) runtime has to drop the empty component
    // rather than render a literal "0h 45m" / "1h 0m".
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


    private fun getFormattedDate(releaseDate: String): String = try {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(releaseDate)
        date?.let {
            SimpleDateFormat("MMMM d, yyyy", localeMonitor.currentLanguage.value.locale).format(it)
        } ?: releaseDate
    } catch (e: ParseException) {
        releaseDate
    }

    // TMDB lists every clip/teaser/featurette alongside the actual trailer;
    // prefer an official YouTube trailer, then fall back progressively so a
    // show with no "official" flag set still gets a playable link.
    private fun List<VideoDto>?.toTrailerUrl(): String? {
        val youtubeVideos = this?.filter { it.site == VIDEO_SITE_YOUTUBE && it.key != null } ?: return null
        val trailer = youtubeVideos.firstOrNull { it.type == VIDEO_TYPE_TRAILER && it.official == true }
                ?: youtubeVideos.firstOrNull { it.type == VIDEO_TYPE_TRAILER }
                ?: youtubeVideos.firstOrNull()
        return trailer?.key?.let { "$YOUTUBE_WATCH_URL$it" }
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
