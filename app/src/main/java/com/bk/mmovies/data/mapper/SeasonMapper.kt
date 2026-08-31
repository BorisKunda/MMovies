package com.bk.mmovies.data.mapper

import android.content.Context
import com.bk.mmovies.R
import com.bk.mmovies.data.source.remote.CAST_PROFILE_PATH_SIZE_SEGMENT
import com.bk.mmovies.data.source.remote.POSTER_PATH_SIZE_SEGMENT_LIST_ITEM
import com.bk.mmovies.data.source.remote.STILL_PATH_SIZE_SEGMENT
import com.bk.mmovies.data.source.remote.dto.CrewMemberDto
import com.bk.mmovies.data.source.remote.dto.EpisodeDto
import com.bk.mmovies.data.source.remote.dto.SeasonDetailsDto
import com.bk.mmovies.data.source.remote.dto.SeasonSummaryDto
import com.bk.mmovies.domain.model.CastMemberModel
import com.bk.mmovies.domain.model.EpisodeModel
import com.bk.mmovies.domain.model.SeasonModel
import com.bk.mmovies.locale.LocaleMonitor
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

private const val DIRECTOR_JOB = "Director"
private val WRITER_JOBS = setOf("Writer", "Screenplay", "Story")

class SeasonMapper @Inject constructor(
        private val localeMonitor: LocaleMonitor,
        private val runtimeLabelFormatter: RuntimeLabelFormatter,
        @ApplicationContext private val context: Context
                                      ) {

    fun toModel(dto: SeasonDetailsDto): SeasonModel = SeasonModel(
            id = dto.id ?: 0,
            name = dto.name ?: "",
            overview = dto.overview ?: "",
            posterUrl = dto.posterPath?.let { getFullImageUrl(POSTER_PATH_SIZE_SEGMENT_LIST_ITEM, it) } ?: "",
            airDate = dto.airDate?.let { getFormattedDate(it) } ?: "",
            seasonNumber = dto.seasonNumber ?: 0,
            rating = dto.voteAverage.toRatingPercent(),
            episodeCountLabel = dto.episodes.orEmpty().size.toEpisodeCountLabel(),
            episodes = dto.episodes.orEmpty().map { toModel(it) })

    // A null id can't back a stable LazyColumn key, so a season TMDB somehow
    // sends without one is dropped rather than collapsed onto id 0 and
    // risking a key collision with a real season.
    fun toModel(dto: SeasonSummaryDto): SeasonModel? {
        val id = dto.id ?: return null
        return SeasonModel(
                id = id,
                name = dto.name ?: "",
                overview = dto.overview ?: "",
                posterUrl = dto.posterPath?.let { getFullImageUrl(POSTER_PATH_SIZE_SEGMENT_LIST_ITEM, it) } ?: "",
                airDate = dto.airDate?.let { getFormattedDate(it) } ?: "",
                seasonNumber = dto.seasonNumber ?: 0,
                rating = dto.voteAverage.toRatingPercent(),
                episodeCountLabel = (dto.episodeCount ?: 0).toEpisodeCountLabel())
    }

    fun toModel(dto: EpisodeDto): EpisodeModel = EpisodeModel(
            id = dto.id ?: 0,
            name = dto.name ?: "",
            overview = dto.overview ?: "",
            airDate = dto.airDate?.let { getFormattedDate(it) } ?: "",
            episodeNumber = dto.episodeNumber ?: 0,
            seasonNumber = dto.seasonNumber ?: 0,
            stillUrl = dto.stillPath?.let { getFullImageUrl(STILL_PATH_SIZE_SEGMENT, it) } ?: "",
            rating = dto.voteAverage.toRatingPercent(),
            runtime = runtimeLabelFormatter.format(dto.runtime),
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
                                ?.let { getFullImageUrl(CAST_PROFILE_PATH_SIZE_SEGMENT, it) } ?: ""
                                )
            } ?: emptyList()

    private fun Int.toEpisodeCountLabel(): String {
        if (this <= 0) return ""
        return context.resources.getQuantityString(R.plurals.episode_count, this, this)
    }

    private fun getFormattedDate(airDate: String): String = try {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(airDate)
        date?.let {
            SimpleDateFormat("MMMM d, yyyy", localeMonitor.currentLanguage.value.locale).format(it)
        } ?: airDate
    } catch (e: ParseException) {
        airDate
    }
}
