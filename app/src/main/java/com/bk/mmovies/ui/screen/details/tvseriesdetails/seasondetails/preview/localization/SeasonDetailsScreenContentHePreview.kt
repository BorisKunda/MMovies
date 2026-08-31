package com.bk.mmovies.ui.screen.details.tvseriesdetails.seasondetails.preview.localization

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bk.mmovies.domain.model.EpisodeModel
import com.bk.mmovies.domain.model.SeasonModel
import com.bk.mmovies.ui.screen.details.tvseriesdetails.seasondetails.screencomponents.SeasonDetailsScreenContent
import com.bk.mmovies.ui.theme.HebrewPhoneSizePreviews
import com.bk.mmovies.ui.theme.MMoviesTheme

// Hebrew content too (not just the UI chrome) so the preview exercises real
// RTL text shaping and bidi mixing with the Latin runtime/score numerals.
private val previewEpisodesHebrew = listOf(
        EpisodeModel(
                id = 1,
                name = "פיילוט",
                overview = "מורה לכימיה הופך למבשל סמים ומשתף פעולה עם תלמיד לשעבר.",
                airDate = "20 בינואר 2008",
                episodeNumber = 1,
                seasonNumber = 1,
                stillUrl = "https://image.tmdb.org/t/p/w300/3P3iyPzjy5W7ZUdatlHRW9Vzp6c.jpg",
                runtime = "58ד"
                    ),
        EpisodeModel(
                id = 2,
                name = "החתול בשק...",
                overview = "",
                airDate = "27 בינואר 2008",
                episodeNumber = 2,
                seasonNumber = 1,
                stillUrl = "",
                runtime = ""
                    ),
        EpisodeModel(
                id = 3,
                name = "...והשק בנהר",
                overview = "וולט וג'סי מנסים להיפטר מגופה בזמן שפלאשבקים חושפים את עברו של וולט.",
                airDate = "10 בפברואר 2008",
                episodeNumber = 3,
                seasonNumber = 1,
                stillUrl = "https://image.tmdb.org/t/p/w300/xlaY2zyzMfkhk0HSC5VUwzoZPU1.jpg",
                runtime = "48ד"
                    )
                                           )

private val previewSeasonHebrew = SeasonModel(
        id = 1,
        name = "עונה 1",
        overview = "לאחר שאובחן כחולה סרטן סופני, מורה לכימיה מתחיל לבשל סמים עם תלמיד לשעבר כדי להבטיח את עתיד משפחתו.",
        posterUrl = "https://image.tmdb.org/t/p/w342/1BP4xYv9ZG4ZbnPLXwbb2usyq0T.jpg",
        airDate = "20 בינואר 2008",
        seasonNumber = 1,
        episodeCountLabel = "7 פרקים",
        episodes = previewEpisodesHebrew
                                              )

@HebrewPhoneSizePreviews
@Composable
private fun SeasonDetailsScreenContentPreviewHebrew() {
    MMoviesTheme {
        Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
            SeasonDetailsScreenContent(
                    season = previewSeasonHebrew,
                    modifier = Modifier.padding(innerPadding)
                                       )
        }
    }
}
