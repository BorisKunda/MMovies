package com.bk.mmovies.domain.model

/**
 * How a TV series' air date reads, derived from its TMDB status rather than
 * the raw date alone — Ended/Canceled gets a year range, Returning Series an
 * open-ended start year, and everything else (Planned/In Production/Pilot,
 * or simply no air date yet) a "premiere" callout.
 */
sealed class SeriesAirDateLabel {
    data class Ended(val yearRange: String) : SeriesAirDateLabel()
    data class Ongoing(val text: String) : SeriesAirDateLabel()
    data class Upcoming(val premiereLabel: String, val dateText: String) : SeriesAirDateLabel()
}
