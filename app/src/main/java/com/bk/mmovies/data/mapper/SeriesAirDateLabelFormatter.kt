package com.bk.mmovies.data.mapper

import android.content.Context
import androidx.core.text.BidiFormatter
import com.bk.mmovies.R
import com.bk.mmovies.domain.model.SeriesAirDateLabel
import com.bk.mmovies.locale.LocaleMonitor
import dagger.hilt.android.qualifiers.ApplicationContext
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
        private val localeMonitor: LocaleMonitor,
        @ApplicationContext private val context: Context
                                                       ) {

    fun format(status: String?, firstAirDate: String?, lastAirDate: String?): SeriesAirDateLabel {
        val startYear = firstAirDate.toYear()
        return when {
            status in ENDED_STATUSES -> SeriesAirDateLabel.Ended(
                    // Unicode-isolated: "2008 - 2015" is an LTR unit even
                    // inside an RTL (Hebrew) paragraph. Without this, the
                    // bidi algorithm visually reorders the two years around
                    // the "-" to "2015 - 2008" in RTL locales.
                    yearRange = BidiFormatter.getInstance().unicodeWrap(
                            listOf(startYear, lastAirDate.toYear())
                                    .filter { it.isNotBlank() }
                                    .joinToString(" - ")
                                                                       )
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

    private fun seriesPremiereLabel(): String = context.getString(R.string.series_premiere_label)

    private fun ongoingLabel(): String = context.getString(R.string.series_ongoing_label)

    private fun tbaLabel(): String = context.getString(R.string.series_tba_label)

    private fun getFormattedDate(date: String): String =
            formatTmdbDate(date, localeMonitor.currentLanguage.value.locale) ?: date
}
