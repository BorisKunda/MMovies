package com.bk.mmovies.data.mapper

import com.bk.mmovies.data.source.remote.POSTER_PATH_SIZE_SEGMENT_LIST_ITEM
import com.bk.mmovies.data.source.remote.TMDB_IMAGE_BASE_URL
import com.bk.mmovies.data.source.remote.dto.MovieDto
import com.bk.mmovies.domain.model.MovieModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class MovieMapper @Inject constructor() {

    fun toModels(dtos: List<MovieDto>): List<MovieModel> = dtos.map { toModel(it) }

    fun toModel(dto: MovieDto): MovieModel = MovieModel(
            id = dto.id ?: 0,
            title = dto.title ?: "",
            desc = dto.desc ?: "",
            imageUrl = dto.imageUrl?.let { getFullImageUrl(it) } ?: "",
            releaseDate = dto.releaseDate?.let { getFormattedDate(it) } ?: "",
            rating = dto.rating.toRatingPercent())

    private fun Double?.toRatingPercent(): Int = this?.let { (it * 10).toInt() } ?: 0

    private fun getFormattedDate(releaseDate: String): String = try {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(releaseDate)
        date?.let { SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).format(it) } ?: releaseDate
    } catch (e: ParseException) {
        releaseDate
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