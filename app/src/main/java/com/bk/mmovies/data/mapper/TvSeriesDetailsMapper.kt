package com.bk.mmovies.data.mapper

import com.bk.mmovies.data.source.remote.CAST_PROFILE_PATH_SIZE_SEGMENT
import com.bk.mmovies.data.source.remote.POSTER_PATH_SIZE_SEGMENT_LIST_ITEM
import com.bk.mmovies.data.source.remote.POSTER_PATH_SIZE_SEGMENT_LIST_ITEM_ZOOM
import com.bk.mmovies.data.source.remote.TMDB_IMAGE_BASE_URL
import com.bk.mmovies.data.source.remote.dto.CastMemberDto
import com.bk.mmovies.data.source.remote.dto.TvSeriesDetailsDto
import com.bk.mmovies.domain.model.CastMemberModel
import com.bk.mmovies.domain.model.TvSeriesDetailsModel
import com.bk.mmovies.locale.AppLanguage
import com.bk.mmovies.locale.LocaleMonitor
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

private const val CAST_LIST_LIMIT = 20

class TvSeriesDetailsMapper @Inject constructor(
        private val localeMonitor: LocaleMonitor,
        private val seasonMapper: SeasonMapper
                                                ) {

    fun toModel(dto: TvSeriesDetailsDto): TvSeriesDetailsModel = TvSeriesDetailsModel(
            id = dto.id ?: 0,
            title = dto.name ?: "",
            posterUrl = dto.posterPath?.let { getFullImageUrl(it, POSTER_PATH_SIZE_SEGMENT_LIST_ITEM) } ?: "",
            backdropUrl = dto.backdropPath?.let { getFullImageUrl(it, POSTER_PATH_SIZE_SEGMENT_LIST_ITEM_ZOOM) } ?: "",
            firstAirDate = dto.firstAirDate?.let { getFormattedDate(it) } ?: "",
            seasonsLabel = dto.numberOfSeasons.toSeasonsLabel(),
            userScore = dto.voteAverage.toRatingPercent(),
            genres = dto.genres?.mapNotNull { it.name } ?: emptyList(),
            overview = dto.overview ?: "",
            cast = dto.credits?.cast.toCastModels(),
            // A "season 0" entry is TMDB's convention for specials; hide it
            // from what is meant to be the regular season list.
            seasons = dto.seasons.orEmpty()
                    .filter { (it.seasonNumber ?: 0) > 0 }
                    .sortedBy { it.seasonNumber }
                    .map { seasonMapper.toModel(it) },
            isFavorite = dto.accountStates?.favorite ?: false
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

    private fun Double?.toRatingPercent(): Int = this?.let { (it * 10).toInt() } ?: 0

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

    private fun getFormattedDate(firstAirDate: String): String = try {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(firstAirDate)
        date?.let {
            SimpleDateFormat("MMMM d, yyyy", localeMonitor.currentLanguage.value.locale).format(it)
        } ?: firstAirDate
    } catch (e: ParseException) {
        firstAirDate
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
