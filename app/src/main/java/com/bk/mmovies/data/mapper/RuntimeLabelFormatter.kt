package com.bk.mmovies.data.mapper

import android.content.Context
import com.bk.mmovies.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Shared by [MovieDetailsMapper] and [SeasonMapper] so movie and episode
 * runtimes are formatted identically. Backed by the runtime_hours/
 * runtime_minutes plurals in strings.xml, whose per-language quantity
 * buckets (e.g. Russian one/few/many, Hebrew one/two/other) already encode
 * the grammar rules this used to hardcode in Kotlin.
 */
class RuntimeLabelFormatter @Inject constructor(
        @ApplicationContext private val context: Context
                                                ) {

    // A sub-hour (or exactly-N-hour) runtime has to drop the empty component
    // rather than render a literal "0h 45m" / "1h 0m".
    fun format(totalMinutes: Int?): String {
        val minutesOnly = totalMinutes?.takeIf { it > 0 } ?: return ""
        val hours = minutesOnly / 60
        val minutes = minutesOnly % 60
        return when {
            hours == 0   -> formatMinutes(minutes)
            minutes == 0 -> formatHours(hours)
            else         -> "${formatHours(hours)} ${formatMinutes(minutes)}"
        }
    }

    private fun formatHours(hours: Int): String =
            context.resources.getQuantityString(R.plurals.runtime_hours, hours, hours)

    private fun formatMinutes(minutes: Int): String =
            context.resources.getQuantityString(R.plurals.runtime_minutes, minutes, minutes)
}
