package com.bk.mmovies.data.repositoryimpl

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Shared by MovieRepositoryImpl and TvSeriesRepositoryImpl: TMDB's
// "upcoming" category is defined as anything releasing/airing from tomorrow
// onward, so both use this as the *_date_gte query bound.
internal fun tomorrowDate(): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, 1)
    return SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(calendar.time)
}
