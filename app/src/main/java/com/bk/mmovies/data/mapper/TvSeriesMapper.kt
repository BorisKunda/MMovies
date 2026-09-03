package com.bk.mmovies.data.mapper

import com.bk.mmovies.data.source.remote.POSTER_PATH_SIZE_SEGMENT_LIST_ITEM
import com.bk.mmovies.data.source.remote.dto.TvSeriesDto
import com.bk.mmovies.domain.model.TvSeriesModel
import com.bk.mmovies.locale.LocaleMonitor
import javax.inject.Inject

class TvSeriesMapper @Inject constructor(
        private val localeMonitor: LocaleMonitor
                                         ) {

    // A null id can't back a stable LazyColumn key, so an item TMDB somehow
    // sends without one is dropped rather than collapsed onto id 0 and
    // risking a key collision with a real series.
    fun toModels(dtos: List<TvSeriesDto>): List<TvSeriesModel> = dtos.mapNotNull { toModel(it) }

    fun toModel(dto: TvSeriesDto): TvSeriesModel? {
        val id = dto.id ?: return null
        return TvSeriesModel(
                id = id,
                title = dto.title ?: "",
                desc = dto.desc ?: "",
                imageUrl = dto.imageUrl?.let { getFullImageUrl(POSTER_PATH_SIZE_SEGMENT_LIST_ITEM, it) } ?: "",
                firstAirDate = dto.firstAirDate?.let { getFormattedDate(it) } ?: "",
                firstAirDateIso = dto.firstAirDate ?: "",
                rating = dto.rating.toRatingPercent())
    }

    private fun getFormattedDate(firstAirDate: String): String =
            formatTmdbDate(firstAirDate, localeMonitor.currentLanguage.value.locale) ?: firstAirDate
}
