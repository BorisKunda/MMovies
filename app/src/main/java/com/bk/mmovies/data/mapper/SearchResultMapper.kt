package com.bk.mmovies.data.mapper

import com.bk.mmovies.data.source.remote.CAST_PROFILE_PATH_SIZE_SEGMENT
import com.bk.mmovies.data.source.remote.POSTER_PATH_SIZE_SEGMENT_LIST_ITEM
import com.bk.mmovies.data.source.remote.TMDB_IMAGE_BASE_URL
import com.bk.mmovies.data.source.remote.dto.MultiSearchResultDto
import com.bk.mmovies.domain.model.SearchResultMediaType
import com.bk.mmovies.domain.model.SearchResultModel
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject

private const val MEDIA_TYPE_MOVIE = "movie"
private const val MEDIA_TYPE_TV = "tv"
private const val MEDIA_TYPE_PERSON = "person"

class SearchResultMapper @Inject constructor() {

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
                    subtitle = dto.releaseDate ?: "",
                    isUpcoming = dto.releaseDate.isUpcomingDate()
                                                   )

            MEDIA_TYPE_TV     -> SearchResultModel(
                    id = id,
                    mediaType = SearchResultMediaType.TV_SERIES,
                    title = dto.name ?: "",
                    imageUrl = dto.posterPath?.let { getFullImageUrl(POSTER_PATH_SIZE_SEGMENT_LIST_ITEM, it) } ?: "",
                    subtitle = dto.firstAirDate ?: ""
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

    private fun getFullImageUrl(sizeSegment: String, path: String): String =
            "$TMDB_IMAGE_BASE_URL$sizeSegment$path"
}
