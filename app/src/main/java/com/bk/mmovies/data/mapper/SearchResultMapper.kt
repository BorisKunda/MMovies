package com.bk.mmovies.data.mapper

import com.bk.mmovies.data.source.remote.CAST_PROFILE_PATH_SIZE_SEGMENT
import com.bk.mmovies.data.source.remote.POSTER_PATH_SIZE_SEGMENT_LIST_ITEM
import com.bk.mmovies.data.source.remote.TMDB_IMAGE_BASE_URL
import com.bk.mmovies.data.source.remote.dto.MultiSearchResultDto
import com.bk.mmovies.domain.model.SearchResultMediaType
import com.bk.mmovies.domain.model.SearchResultModel
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
                    rating = dto.rating.toRatingPercent()
                                                   )

            MEDIA_TYPE_TV     -> SearchResultModel(
                    id = id,
                    mediaType = SearchResultMediaType.TV_SERIES,
                    title = dto.name ?: "",
                    imageUrl = dto.posterPath?.let { getFullImageUrl(POSTER_PATH_SIZE_SEGMENT_LIST_ITEM, it) } ?: "",
                    subtitle = dto.firstAirDate ?: "",
                    rating = dto.rating.toRatingPercent()
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

    private fun Double?.toRatingPercent(): Int = this?.let { (it * 10).toInt() } ?: 0

    private fun getFullImageUrl(sizeSegment: String, path: String): String =
            "$TMDB_IMAGE_BASE_URL$sizeSegment$path"
}
