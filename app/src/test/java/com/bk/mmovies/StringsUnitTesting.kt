package com.bk.mmovies

import com.bk.mmovies.data.source.remote.POSTER_PATH_SIZE_SEGMENT_LIST_ITEM
import com.bk.mmovies.data.source.remote.TMDB_IMAGE_BASE_URL
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale


class StringsUnitTesting {
    @Test
    fun testGetFormattedDate() {
        assertEquals("May 21, 2026", getFormattedDate("2026-05-21"))
    }

    @Test
    fun linkIsCorrect() {
        val fullPosterPath = "https://image.tmdb.org/t/p/w342/qvktm0BHcnmDpul4Hz01GIazWPr.jpg"
        assertEquals(
            fullPosterPath,
            getFullMoviePosterPath("/qvktm0BHcnmDpul4Hz01GIazWPr.jpg")
        )
    }

    private fun getFullMoviePosterPath(posterEndpoint: String): String {
        val stringBuilder = StringBuilder()
        stringBuilder.apply {
            append(TMDB_IMAGE_BASE_URL)
            append(POSTER_PATH_SIZE_SEGMENT_LIST_ITEM)
            append(posterEndpoint)
        }
        return stringBuilder.toString()
    }

    private fun getFormattedDate(releaseDate: String): String {
        val stringParts: List<String> = releaseDate.split("-")
        val year = stringParts[0]
        val month = stringParts[1]
        var monthName = ""

        try {
            monthName = Month.of(month.toInt()).getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        } catch (e: Exception) {
            print(e.message ?: "failed to parse month number")
        }

        val day = stringParts[2]
        val stringBuilder = StringBuilder()
        stringBuilder.append(monthName).append(" ").append(day).append(", ").append(year)
        return stringBuilder.toString()
    }

}