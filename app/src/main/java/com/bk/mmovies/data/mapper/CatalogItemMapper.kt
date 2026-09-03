package com.bk.mmovies.data.mapper

import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.domain.model.CatalogMediaType
import com.bk.mmovies.domain.model.MovieModel
import com.bk.mmovies.domain.model.TvSeriesModel
import javax.inject.Inject

class CatalogItemMapper @Inject constructor() {

    fun fromMovies(movies: List<MovieModel>): List<CatalogItem> = movies.map { fromMovie(it) }

    fun fromMovie(movie: MovieModel): CatalogItem = CatalogItem(
            id = movie.id,
            title = movie.title,
            imageUrl = movie.imageUrl,
            releaseDate = movie.releaseDate,
            releaseDateIso = movie.releaseDateIso,
            mediaType = CatalogMediaType.MOVIE,
            rating = movie.rating,
            isFavorite = movie.isFavorite)

    fun fromTvSeries(tvSeries: List<TvSeriesModel>): List<CatalogItem> = tvSeries.map { fromTvSeries(it) }

    fun fromTvSeries(tvSeries: TvSeriesModel): CatalogItem = CatalogItem(
            id = tvSeries.id,
            title = tvSeries.title,
            imageUrl = tvSeries.imageUrl,
            releaseDate = tvSeries.firstAirDate,
            releaseDateIso = tvSeries.firstAirDateIso,
            mediaType = CatalogMediaType.TV_SERIES,
            rating = tvSeries.rating,
            isFavorite = tvSeries.isFavorite)
}
