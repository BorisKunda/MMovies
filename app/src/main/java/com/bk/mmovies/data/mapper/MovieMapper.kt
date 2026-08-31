package com.bk.mmovies.data.mapper

import com.bk.mmovies.data.source.remote.POSTER_PATH_SIZE_SEGMENT_LIST_ITEM
import com.bk.mmovies.data.source.remote.dto.MovieDto
import com.bk.mmovies.domain.model.MovieModel
import com.bk.mmovies.locale.LocaleMonitor
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class MovieMapper @Inject constructor(
        private val localeMonitor: LocaleMonitor
                                     ) {

    // A null id can't back a stable LazyColumn key, so an item TMDB somehow
    // sends without one is dropped rather than collapsed onto id 0 and
    // risking a key collision with a real movie.
    fun toModels(dtos: List<MovieDto>): List<MovieModel> = dtos.mapNotNull { toModel(it) }

    fun toModel(dto: MovieDto): MovieModel? {
        val id = dto.id ?: return null
        return MovieModel(
                id = id,
                title = dto.title ?: "",
                desc = dto.desc ?: "",
                imageUrl = dto.imageUrl?.let { getFullImageUrl(POSTER_PATH_SIZE_SEGMENT_LIST_ITEM, it) } ?: "",
                releaseDate = dto.releaseDate?.let { getFormattedDate(it) } ?: "",
                rating = dto.rating.toRatingPercent())
    }

    private fun getFormattedDate(releaseDate: String): String = try {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(releaseDate)
        date?.let {
            SimpleDateFormat("MMMM d, yyyy", localeMonitor.currentLanguage.value.locale).format(it)
        } ?: releaseDate
    } catch (e: ParseException) {
        releaseDate
    }
}