package com.bk.mmovies.ui.screen.details.tvseriesdetails.preview.localization

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bk.mmovies.domain.model.CastMemberModel
import com.bk.mmovies.domain.model.SeasonModel
import com.bk.mmovies.domain.model.SeriesAirDateLabel
import com.bk.mmovies.domain.model.TvSeriesDetailsModel
import com.bk.mmovies.ui.screen.details.tvseriesdetails.screencomponents.TvSeriesDetailsScreenContent
import com.bk.mmovies.ui.theme.HebrewPhoneSizePreviews
import com.bk.mmovies.ui.theme.MMoviesTheme

// Hebrew content too (not just the UI chrome) so the preview exercises real
// RTL text shaping and bidi mixing with the Latin score numerals and season
// meta line.
private val previewCastHebrew = listOf(
        CastMemberModel(
                id = 1,
                name = "בריאן קרנסטון",
                character = "וולטר וייט",
                profileUrl = "https://image.tmdb.org/t/p/w185/7Jahy5LZX2Fo8fGJltMreAI49hC.jpg"
                        ),
        CastMemberModel(
                id = 2,
                name = "אהרון פול",
                character = "ג'סי פינקמן",
                profileUrl = ""
                        )
                                       )

private val previewCreatorsHebrew = listOf(
        CastMemberModel(id = 10, name = "וינס גיליגן", character = "", profileUrl = "")
                                           )

private val previewSeasonsHebrew = listOf(
        SeasonModel(
                id = 1,
                name = "עונה 1",
                overview = "",
                posterUrl = "https://image.tmdb.org/t/p/w342/1BP4xYv9ZG4ZbnPLXwbb2usyq0T.jpg",
                airDate = "20 בינואר 2008",
                seasonNumber = 1,
                episodeCountLabel = "7 פרקים"
                   ),
        SeasonModel(
                id = 2,
                name = "עונה 2",
                overview = "",
                posterUrl = "",
                airDate = "8 במרץ 2009",
                seasonNumber = 2,
                episodeCountLabel = "13 פרקים"
                   )
                                          )

private val previewTvSeriesDetailsHebrew = TvSeriesDetailsModel(
        id = 1,
        title = "פורצי חוק",
        posterUrl = "https://image.tmdb.org/t/p/w342/ztkUQFLlC19CCMYHW9o1zWhJRNq.jpg",
        backdropUrl = "https://image.tmdb.org/t/p/w780/xlaY2zyzMfkhk0HSC5VUwzoZPU1.jpg",
        airDateLabel = SeriesAirDateLabel.Ended(yearRange = "2008 - 2013"),
        seasonsLabel = "5 עונות",
        userScore = 88,
        genres = listOf("דרמה", "פשע", "מותחן"),
        overview = "מורה לכימיה בתיכון שאובחן עם סרטן פונה לייצור מתאמפטמין כדי להבטיח את עתיד משפחתו.",
        cast = previewCastHebrew,
        creators = previewCreatorsHebrew,
        seasons = previewSeasonsHebrew
                                                                 )

@HebrewPhoneSizePreviews
@Composable
private fun TvSeriesDetailsScreenContentPreviewHebrew() {
    MMoviesTheme {
        Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
            TvSeriesDetailsScreenContent(
                    tvSeriesDetails = previewTvSeriesDetailsHebrew,
                    modifier = Modifier.padding(innerPadding),
                    showFavoriteStar = true,
                    onFavoriteClicked = {}
                                         )
        }
    }
}
