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
import com.bk.mmovies.ui.theme.MMoviesTheme
import com.bk.mmovies.ui.theme.RussianPhoneSizePreviews

// Russian content too, since the point of this one is length rather than
// direction: creator/cast names, genre chips and the season meta line are
// the tightest fits on this screen.
private val previewCastRussian = listOf(
        CastMemberModel(
                id = 1,
                name = "Брайан Крэнстон",
                character = "Уолтер Уайт",
                profileUrl = "https://image.tmdb.org/t/p/w185/7Jahy5LZX2Fo8fGJltMreAI49hC.jpg"
                        ),
        CastMemberModel(
                id = 2,
                name = "Аарон Пол",
                character = "Джесси Пинкман",
                profileUrl = ""
                        )
                                        )

private val previewCreatorsRussian = listOf(
        CastMemberModel(id = 10, name = "Винс Гиллиган", character = "", profileUrl = "")
                                            )

private val previewSeasonsRussian = listOf(
        SeasonModel(
                id = 1,
                name = "Сезон 1",
                overview = "",
                posterUrl = "https://image.tmdb.org/t/p/w342/1BP4xYv9ZG4ZbnPLXwbb2usyq0T.jpg",
                airDate = "20 января 2008",
                seasonNumber = 1,
                episodeCountLabel = "7 эпизодов"
                   ),
        SeasonModel(
                id = 2,
                name = "Сезон 2",
                overview = "",
                posterUrl = "",
                airDate = "8 марта 2009",
                seasonNumber = 2,
                episodeCountLabel = "13 эпизодов"
                   )
                                           )

private val previewTvSeriesDetailsRussian = TvSeriesDetailsModel(
        id = 1,
        title = "Во все тяжкие",
        posterUrl = "https://image.tmdb.org/t/p/w342/ztkUQFLlC19CCMYHW9o1zWhJRNq.jpg",
        backdropUrl = "https://image.tmdb.org/t/p/w780/xlaY2zyzMfkhk0HSC5VUwzoZPU1.jpg",
        airDateLabel = SeriesAirDateLabel.Ended(yearRange = "2008 - 2013"),
        seasonsLabel = "5 сезонов",
        userScore = 88,
        genres = listOf("Драма", "Криминал", "Триллер"),
        overview = "Школьный учитель химии, узнав о своём диагнозе, начинает производить " +
                "метамфетамин, чтобы обеспечить будущее своей семьи.",
        cast = previewCastRussian,
        creators = previewCreatorsRussian,
        seasons = previewSeasonsRussian
                                                                  )

@RussianPhoneSizePreviews
@Composable
private fun TvSeriesDetailsScreenContentPreviewRussian() {
    MMoviesTheme {
        Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
            TvSeriesDetailsScreenContent(
                    tvSeriesDetails = previewTvSeriesDetailsRussian,
                    modifier = Modifier.padding(innerPadding),
                    showFavoriteStar = true,
                    onFavoriteClicked = {}
                                         )
        }
    }
}
