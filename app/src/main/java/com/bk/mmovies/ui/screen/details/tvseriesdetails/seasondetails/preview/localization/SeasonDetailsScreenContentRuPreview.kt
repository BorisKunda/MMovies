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
import com.bk.mmovies.ui.theme.MMoviesTheme
import com.bk.mmovies.ui.theme.RussianPhoneSizePreviews

// Russian content too, since the point of this one is length rather than
// direction: episode titles and the meta line are the tightest fits on
// this screen.
private val previewEpisodesRussian = listOf(
        EpisodeModel(
                id = 1,
                name = "Пилот",
                overview = "Учитель химии, больной раком, начинает готовить метамфетамин вместе с бывшим учеником.",
                airDate = "20 января 2008",
                episodeNumber = 1,
                seasonNumber = 1,
                stillUrl = "https://image.tmdb.org/t/p/w300/3P3iyPzjy5W7ZUdatlHRW9Vzp6c.jpg",
                runtime = "58м"
                    ),
        EpisodeModel(
                id = 2,
                name = "Кот в мешке...",
                overview = "",
                airDate = "27 января 2008",
                episodeNumber = 2,
                seasonNumber = 1,
                stillUrl = "",
                runtime = ""
                    ),
        EpisodeModel(
                id = 3,
                name = "...И мешок в реке",
                overview = "Уолт и Джесси пытаются избавиться от тела, пока флешбэки раскрывают прошлое Уолта.",
                airDate = "10 февраля 2008",
                episodeNumber = 3,
                seasonNumber = 1,
                stillUrl = "https://image.tmdb.org/t/p/w300/xlaY2zyzMfkhk0HSC5VUwzoZPU1.jpg",
                runtime = "48м"
                    )
                                            )

private val previewSeasonRussian = SeasonModel(
        id = 1,
        name = "Сезон 1",
        overview = "Узнав о неизлечимом раке, учитель химии начинает готовить метамфетамин с бывшим учеником, чтобы обеспечить будущее своей семьи.",
        posterUrl = "https://image.tmdb.org/t/p/w342/1BP4xYv9ZG4ZbnPLXwbb2usyq0T.jpg",
        airDate = "20 января 2008",
        seasonNumber = 1,
        episodeCountLabel = "7 эпизодов",
        episodes = previewEpisodesRussian
                                               )

@RussianPhoneSizePreviews
@Composable
private fun SeasonDetailsScreenContentPreviewRussian() {
    MMoviesTheme {
        Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
            SeasonDetailsScreenContent(
                    season = previewSeasonRussian,
                    modifier = Modifier.padding(innerPadding)
                                       )
        }
    }
}
