package com.bk.mmovies

import com.bk.mmovies.data.source.remote.POSTER_PATH_SIZE_SEGMENT_LIST_ITEM
import com.bk.mmovies.data.source.remote.TMDB_IMAGE_BASE_URL
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


class StringsUnitTesting {
    @Test
    fun testGetFormattedDate() {
        assertEquals(
                "May 21, 2026",
                getFormattedDate("2026-05-21")
                    )
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
        return runCatching {
            val date = LocalDate.parse(
                    releaseDate,
                    DateTimeFormatter.ISO_LOCAL_DATE
                                      )

            date.format(
                    DateTimeFormatter.ofPattern(
                            "MMMM d, yyyy",
                            Locale.ENGLISH
                                               )
                       )
        }.getOrDefault(releaseDate)
    }

}