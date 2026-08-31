package com.bk.mmovies.data.mapper

import com.bk.mmovies.data.source.remote.CAST_PROFILE_PATH_SIZE_SEGMENT
import com.bk.mmovies.data.source.remote.POSTER_PATH_SIZE_SEGMENT_LIST_ITEM
import com.bk.mmovies.data.source.remote.dto.MultiSearchResultDto
import com.bk.mmovies.domain.model.SearchResultMediaType
import com.bk.mmovies.domain.model.SearchResultModel
import com.bk.mmovies.locale.LocaleMonitor
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.Locale
import javax.inject.Inject

private const val MEDIA_TYPE_MOVIE = "movie"
const val MEDIA_TYPE_TV = "tv"
private const val MEDIA_TYPE_PERSON = "person"

class SearchResultMapper @Inject constructor(
        private val localeMonitor: LocaleMonitor
                                             ) {

    // "collection", "company", etc. also come back from /search/multi but
    // have nothing this app can open, so they're dropped rather than mapped.
    fun toModels(dtos: List<MultiSearchResultDto>): List<SearchResultModel> =
            dtos.mapNotNull { toModel(it) }

    private fun toModel(dto: MultiSearchResultDto): SearchResultModel? {
        val id = dto.id ?: return null
        return when (dto.mediaType) {
            MEDIA_TYPE_MOVIE  -> SearchResultModel(
                    id = id,
                    mediaType = SearchResultMediaType.MOVIE,
                    title = dto.title ?: "",
                    imageUrl = dto.posterPath?.let { getFullImageUrl(POSTER_PATH_SIZE_SEGMENT_LIST_ITEM, it) } ?: "",
                    subtitle = dto.releaseDate?.let { getFormattedDate(it) } ?: "",
                    isUpcoming = dto.releaseDate.isUpcomingDate()
                                                   )

            MEDIA_TYPE_TV     -> SearchResultModel(
                    id = id,
                    mediaType = SearchResultMediaType.TV_SERIES,
                    title = dto.name ?: "",
                    imageUrl = dto.posterPath?.let { getFullImageUrl(POSTER_PATH_SIZE_SEGMENT_LIST_ITEM, it) } ?: "",
                    subtitle = dto.firstAirDate?.let { getFormattedDate(it) } ?: ""
                                                   )

            MEDIA_TYPE_PERSON -> SearchResultModel(
                    id = id,
                    mediaType = SearchResultMediaType.PERSON,
                    title = dto.name ?: "",
                    imageUrl = dto.profilePath?.let { getFullImageUrl(CAST_PROFILE_PATH_SIZE_SEGMENT, it) } ?: "",
                    subtitle = ""
                                                   )

            else              -> null
        }
    }

    private fun getFormattedDate(rawDate: String): String = try {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(rawDate)
        date?.let {
            SimpleDateFormat("MMMM d, yyyy", localeMonitor.currentLanguage.value.locale).format(it)
        } ?: rawDate
    } catch (e: ParseException) {
        rawDate
    }

    // No release date at all is treated as not-yet-released, same as a
    // release date that's clearly in the future.
    private fun String?.isUpcomingDate(): Boolean {
        if (this.isNullOrBlank()) return true
        return try {
            LocalDate.parse(this).isAfter(LocalDate.now())
        } catch (e: DateTimeParseException) {
            false
        }
    }
}
