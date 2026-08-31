package com.bk.mmovies.data.mapper

import com.bk.mmovies.data.source.remote.CAST_PROFILE_PATH_SIZE_SEGMENT
import com.bk.mmovies.data.source.remote.POSTER_PATH_SIZE_SEGMENT_LIST_ITEM
import com.bk.mmovies.data.source.remote.POSTER_PATH_SIZE_SEGMENT_LIST_ITEM_ZOOM
import com.bk.mmovies.data.source.remote.dto.CastMemberDto
import com.bk.mmovies.data.source.remote.dto.CrewMemberDto
import com.bk.mmovies.data.source.remote.dto.MovieDetailsDto
import com.bk.mmovies.data.source.remote.dto.VideoDto
import com.bk.mmovies.domain.model.CastMemberModel
import com.bk.mmovies.domain.model.MovieDetailsModel
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
        private val localeMonitor: LocaleMonitor,
        private val runtimeLabelFormatter: RuntimeLabelFormatter
                                             ) {

    fun toModel(dto: MovieDetailsDto): MovieDetailsModel = MovieDetailsModel(
            id = dto.id ?: 0,
            title = dto.title ?: "",
            posterUrl = dto.posterPath?.let { getFullImageUrl(POSTER_PATH_SIZE_SEGMENT_LIST_ITEM, it) } ?: "",
            backdropUrl = dto.backdropPath?.let { getFullImageUrl(POSTER_PATH_SIZE_SEGMENT_LIST_ITEM_ZOOM, it) } ?: "",
            releaseDate = dto.releaseDate?.let { getFormattedDate(it) } ?: "",
            runtime = runtimeLabelFormatter.format(dto.runtime),
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
                                ?.let { getFullImageUrl(CAST_PROFILE_PATH_SIZE_SEGMENT, it) } ?: ""
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
                                ?.let { getFullImageUrl(CAST_PROFILE_PATH_SIZE_SEGMENT, it) } ?: ""
                                )
            } ?: emptyList()

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
}
