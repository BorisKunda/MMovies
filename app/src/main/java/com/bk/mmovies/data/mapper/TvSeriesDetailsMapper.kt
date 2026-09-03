package com.bk.mmovies.data.mapper

import android.content.Context
import com.bk.mmovies.R
import com.bk.mmovies.data.source.remote.CAST_PROFILE_PATH_SIZE_SEGMENT
import com.bk.mmovies.data.source.remote.POSTER_PATH_SIZE_SEGMENT_LIST_ITEM
import com.bk.mmovies.data.source.remote.POSTER_PATH_SIZE_SEGMENT_LIST_ITEM_ZOOM
import com.bk.mmovies.data.source.remote.dto.CastMemberDto
import com.bk.mmovies.data.source.remote.dto.CreatedByDto
import com.bk.mmovies.data.source.remote.dto.TvSeriesDetailsDto
import com.bk.mmovies.data.source.remote.dto.VideoDto
import com.bk.mmovies.domain.model.CastMemberModel
import com.bk.mmovies.domain.model.TvSeriesDetailsModel
import com.bk.mmovies.locale.AppLanguage
import com.bk.mmovies.locale.LocaleMonitor
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val CAST_LIST_LIMIT = 20
private const val VIDEO_SITE_YOUTUBE = "YouTube"
private const val VIDEO_TYPE_TRAILER = "Trailer"
private const val YOUTUBE_WATCH_URL = "https://www.youtube.com/watch?v="

class TvSeriesDetailsMapper @Inject constructor(
        private val localeMonitor: LocaleMonitor,
        private val seasonMapper: SeasonMapper,
        private val seriesAirDateLabelFormatter: SeriesAirDateLabelFormatter,
        @ApplicationContext private val context: Context
                                                ) {

    fun toModel(dto: TvSeriesDetailsDto): TvSeriesDetailsModel = TvSeriesDetailsModel(
            id = dto.id ?: 0,
            title = dto.name ?: "",
            posterUrl = dto.posterPath?.let { getFullImageUrl(POSTER_PATH_SIZE_SEGMENT_LIST_ITEM, it) } ?: "",
            backdropUrl = dto.backdropPath?.let { getFullImageUrl(POSTER_PATH_SIZE_SEGMENT_LIST_ITEM_ZOOM, it) } ?: "",
            airDateLabel = seriesAirDateLabelFormatter.format(dto.status, dto.firstAirDate, dto.lastAirDate),
            seasonsLabel = dto.numberOfSeasons.toSeasonsLabel(),
            userScore = dto.voteAverage.toRatingPercent(),
            genres = dto.genres?.mapNotNull { it.name } ?: emptyList(),
            overview = dto.overview ?: "",
            cast = dto.credits?.cast.toCastModels(),
            creators = dto.createdBy.toCreatorModels(),
            // A "season 0" entry is TMDB's convention for specials; hide it
            // from what is meant to be the regular season list.
            seasons = dto.seasons.orEmpty()
                    .filter { (it.seasonNumber ?: 0) > 0 }
                    .sortedBy { it.seasonNumber }
                    .mapNotNull { seasonMapper.toModel(it) },
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

    // TV shows are credited with named creators at the series level (TMDB's
    // "created_by"), unlike movies whose single director sits in the crew
    // list — so this reads a different field entirely rather than a job filter.
    private fun List<CreatedByDto>?.toCreatorModels(): List<CastMemberModel> = this
            ?.distinctBy { it.id }
            ?.mapNotNull { createdByDto ->
                val id = createdByDto.id ?: return@mapNotNull null
                val name = createdByDto.name ?: return@mapNotNull null
                CastMemberModel(
                        id = id,
                        name = name,
                        character = context.getString(
                                if (createdByDto.gender == TMDB_GENDER_FEMALE) {
                                    R.string.details_creator_role_female
                                } else {
                                    R.string.details_creator_role
                                }
                                                      ),
                        profileUrl = createdByDto.profilePath
                                ?.let { getFullImageUrl(CAST_PROFILE_PATH_SIZE_SEGMENT, it) } ?: ""
                                )
            } ?: emptyList()

    private fun Int?.toSeasonsLabel(): String {
        val seasons = this?.takeIf { it > 0 } ?: return ""
        return when (localeMonitor.currentLanguage.value) {
            AppLanguage.RUSSIAN -> "$seasons ${seasons.toRussianSeasonsWord()}"
            AppLanguage.HEBREW  -> if (seasons == 1) "עונה אחת" else "$seasons עונות"
            AppLanguage.ENGLISH -> if (seasons == 1) "1 Season" else "$seasons Seasons"
        }
    }

    // Russian counting nouns decline by the last digit (with 11-14 always
    // taking the plural form), unlike English/Hebrew's simple one-vs-many split.
    private fun Int.toRussianSeasonsWord(): String {
        val lastTwoDigits = this % 100
        if (lastTwoDigits in 11..14) return "сезонов"
        return when (this % 10) {
            1    -> "сезон"
            2, 3, 4 -> "сезона"
            else -> "сезонов"
        }
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
