package com.bk.mmovies.data.mapper

import com.bk.mmovies.data.source.remote.POSTER_PATH_SIZE_SEGMENT_LIST_ITEM
import com.bk.mmovies.data.source.remote.TMDB_IMAGE_BASE_URL
import com.bk.mmovies.data.source.remote.dto.TvSeriesDto
import com.bk.mmovies.domain.model.TvSeriesModel
import com.bk.mmovies.locale.LocaleMonitor
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class TvSeriesMapper @Inject constructor(
        private val localeMonitor: LocaleMonitor
                                         ) {

    fun toModels(dtos: List<TvSeriesDto>): List<TvSeriesModel> = dtos.map { toModel(it) }

    fun toModel(dto: TvSeriesDto): TvSeriesModel = TvSeriesModel(
            id = dto.id ?: 0,
            title = dto.title ?: "",
            desc = dto.desc ?: "",
            imageUrl = dto.imageUrl?.let { getFullImageUrl(it) } ?: "",
            firstAirDate = dto.firstAirDate?.let { getFormattedDate(it) } ?: "",
            rating = dto.rating.toRatingPercent())

    private fun Double?.toRatingPercent(): Int = this?.let { (it * 10).toInt() } ?: 0

    private fun getFormattedDate(firstAirDate: String): String = try {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(firstAirDate)
        date?.let {
            SimpleDateFormat("MMMM d, yyyy", localeMonitor.currentLanguage.value.locale).format(it)
        } ?: firstAirDate
    } catch (e: ParseException) {
        firstAirDate
    }

    private fun getFullImageUrl(posterEndpoint: String): String {
        val stringBuilder = StringBuilder()
        stringBuilder.apply {
            append(TMDB_IMAGE_BASE_URL)
            append(POSTER_PATH_SIZE_SEGMENT_LIST_ITEM)
            append(posterEndpoint)
        }
        return stringBuilder.toString()
    }
}
