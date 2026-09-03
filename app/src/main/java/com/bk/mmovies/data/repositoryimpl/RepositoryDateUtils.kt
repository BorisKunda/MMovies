package com.bk.mmovies.data.repositoryimpl

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val TOMORROW_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)

// Shared by MovieRepositoryImpl and TvSeriesRepositoryImpl: TMDB's
// "upcoming" category is defined as anything releasing/airing from tomorrow
// onward, so both use this as the *_date_gte query bound.
internal fun tomorrowDate(): String = LocalDate.now().plusDays(1).format(TOMORROW_DATE_FORMATTER)
