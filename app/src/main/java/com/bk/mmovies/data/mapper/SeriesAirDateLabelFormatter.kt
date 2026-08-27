package com.bk.mmovies.data.mapper

import com.bk.mmovies.domain.model.SeriesAirDateLabel
import com.bk.mmovies.locale.AppLanguage
import com.bk.mmovies.locale.LocaleMonitor
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

private val ENDED_STATUSES = setOf("Ended", "Canceled")
private const val ONGOING_STATUS = "Returning Series"

/**
 * Single source of truth for how a TV series' air date is presented, shared
 * by the catalog list row and the details screen so the two can't drift
 * apart. Both call this with the same three raw TMDB fields — the catalog
 * row fetches them lazily per item since list endpoints don't include
 * status/last_air_date, the details screen already has them from the full
 * series payload.
 */
class SeriesAirDateLabelFormatter @Inject constructor(
        private val localeMonitor: LocaleMonitor
                                                       ) {

    fun format(status: String?, firstAirDate: String?, lastAirDate: String?): SeriesAirDateLabel {
        val startYear = firstAirDate.toYear()
        return when {
            status in ENDED_STATUSES -> SeriesAirDateLabel.Ended(
                    yearRange = listOf(startYear, lastAirDate.toYear())
                            .filter { it.isNotBlank() }
                            .joinToString(" - ")
                                                                 )
            status == ONGOING_STATUS -> SeriesAirDateLabel.Ongoing(
                    text = if (startYear.isBlank()) "" else "$startYear - ${ongoingLabel()}"
                                                                    )
            // Planned/In Production/Pilot, or any other/unknown status —
            // treated as "not aired yet" per TMDB's own semantics for those.
            else                      -> SeriesAirDateLabel.Upcoming(
                    premiereLabel = seriesPremiereLabel(),
                    dateText = firstAirDate?.let { getFormattedDate(it) } ?: tbaLabel()
                                                                      )
        }
    }

    private fun String?.toYear(): String = this?.take(4).orEmpty()

    private fun seriesPremiereLabel(): String = when (localeMonitor.currentLanguage.value) {
        AppLanguage.RUSSIAN -> "Премьера сериала"
        AppLanguage.HEBREW  -> "בכורת הסדרה"
        AppLanguage.ENGLISH -> "Series Premiere"
    }

    private fun ongoingLabel(): String = when (localeMonitor.currentLanguage.value) {
        AppLanguage.RUSSIAN -> "Идёт показ"
        AppLanguage.HEBREW  -> "בשידור"
        AppLanguage.ENGLISH -> "Ongoing"
    }

    private fun tbaLabel(): String = when (localeMonitor.currentLanguage.value) {
        AppLanguage.RUSSIAN -> "Дата уточняется"
        AppLanguage.HEBREW  -> "טרם נקבע"
        AppLanguage.ENGLISH -> "TBA"
    }

    private fun getFormattedDate(date: String): String = try {
        val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date)
        parsed?.let {
            SimpleDateFormat("MMMM d, yyyy", localeMonitor.currentLanguage.value.locale).format(it)
        } ?: date
    } catch (e: ParseException) {
        date
    }
}
