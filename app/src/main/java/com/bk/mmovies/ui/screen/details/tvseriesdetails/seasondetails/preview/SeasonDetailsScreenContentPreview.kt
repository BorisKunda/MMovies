package com.bk.mmovies.ui.screen.details.tvseriesdetails.seasondetails.preview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bk.mmovies.domain.model.EpisodeModel
import com.bk.mmovies.domain.model.SeasonModel
import com.bk.mmovies.ui.screen.details.tvseriesdetails.seasondetails.screencomponents.SeasonDetailsScreenContent
import com.bk.mmovies.ui.theme.MMoviesTheme
import com.bk.mmovies.ui.theme.PhoneSizePreviews

private val previewEpisodes = listOf(
        EpisodeModel(
                id = 1,
                name = "Pilot",
                overview = "A chemistry teacher turned drug dealer partners with a former student.",
                airDate = "January 20, 2008",
                episodeNumber = 1,
                seasonNumber = 1,
                stillUrl = "https://image.tmdb.org/t/p/w300/3P3iyPzjy5W7ZUdatlHRW9Vzp6c.jpg",
                runtime = "58m"
                    ),
        EpisodeModel(
                id = 2,
                name = "Cat's in the Bag...",
                overview = "",
                airDate = "January 27, 2008",
                episodeNumber = 2,
                seasonNumber = 1,
                stillUrl = "",
                runtime = ""
                    ),
        EpisodeModel(
                id = 3,
                name = "...And the Bag's in the River",
                overview = "Walt and Jesse attempt to dispose of a body while flashbacks reveal Walt's past.",
                airDate = "February 10, 2008",
                episodeNumber = 3,
                seasonNumber = 1,
                stillUrl = "https://image.tmdb.org/t/p/w300/xlaY2zyzMfkhk0HSC5VUwzoZPU1.jpg",
                runtime = "48m"
                    )
                                     )

private val previewSeason = SeasonModel(
        id = 1,
        name = "Season 1",
        overview = "Diagnosed with terminal cancer, a chemistry teacher starts cooking meth with a former student to secure his family's future.",
        posterUrl = "https://image.tmdb.org/t/p/w342/1BP4xYv9ZG4ZbnPLXwbb2usyq0T.jpg",
        airDate = "January 20, 2008",
        seasonNumber = 1,
        episodeCountLabel = "7 Episodes",
        episodes = previewEpisodes
                                        )

@PhoneSizePreviews
@Composable
private fun SeasonDetailsScreenContentPreview() {
    SeasonDetailsScreenContentPreviewFrame(previewSeason)
}

@Preview(showBackground = true, name = "No overview")
@Composable
private fun SeasonDetailsScreenContentNoOverviewPreview() {
    SeasonDetailsScreenContentPreviewFrame(previewSeason.copy(overview = ""))
}

@Preview(showBackground = true, name = "No episodes yet")
@Composable
private fun SeasonDetailsScreenContentNoEpisodesPreview() {
    SeasonDetailsScreenContentPreviewFrame(previewSeason.copy(episodes = emptyList()))
}

@Preview(showBackground = true, name = "Sparse data + long title")
@Composable
private fun SeasonDetailsScreenContentSparsePreview() {
    SeasonDetailsScreenContentPreviewFrame(
            previewSeason.copy(
                    name = "Season 1: The Complete Extended Director's Cut Edition",
                    overview = "",
                    airDate = "",
                    episodeCountLabel = "",
                    episodes = emptyList()
                               )
                                           )
}

@Composable
private fun SeasonDetailsScreenContentPreviewFrame(season: SeasonModel) {
    MMoviesTheme {
        Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
            SeasonDetailsScreenContent(
                    season = season,
                    modifier = Modifier.padding(innerPadding)
                                       )
        }
    }
}
