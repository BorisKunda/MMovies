package com.bk.mmovies.ui.screen.details.tvseriesdetails.preview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bk.mmovies.domain.model.CastMemberModel
import com.bk.mmovies.domain.model.SeasonModel
import com.bk.mmovies.domain.model.SeriesAirDateLabel
import com.bk.mmovies.domain.model.TvSeriesDetailsModel
import com.bk.mmovies.ui.screen.details.tvseriesdetails.screencomponents.TvSeriesDetailsScreenContent
import com.bk.mmovies.ui.theme.MMoviesTheme
import com.bk.mmovies.ui.theme.PhoneSizePreviews

private val previewCast = listOf(
        CastMemberModel(
                id = 1,
                name = "Bryan Cranston",
                character = "Walter White",
                profileUrl = "https://image.tmdb.org/t/p/w185/7Jahy5LZX2Fo8fGJltMreAI49hC.jpg"
                        ),
        CastMemberModel(
                id = 2,
                name = "Aaron Paul",
                character = "Jesse Pinkman",
                profileUrl = ""
                        ),
        CastMemberModel(
                id = 3,
                name = "A Very Long Cast Member Name",
                character = "A very long character description that should wrap",
                profileUrl = ""
                        )
                                 )

private val previewCreators = listOf(
        CastMemberModel(id = 10, name = "Vince Gilligan", character = "", profileUrl = "")
                                     )

private val previewSeasons = listOf(
        SeasonModel(
                id = 1,
                name = "Season 1",
                overview = "",
                posterUrl = "https://image.tmdb.org/t/p/w342/1BP4xYv9ZG4ZbnPLXwbb2usyq0T.jpg",
                airDate = "January 20, 2008",
                seasonNumber = 1,
                episodeCountLabel = "7 Episodes"
                   ),
        SeasonModel(
                id = 2,
                name = "Season 2",
                overview = "",
                posterUrl = "",
                airDate = "March 8, 2009",
                seasonNumber = 2,
                episodeCountLabel = "13 Episodes"
                   )
                                    )

private val previewTvSeriesDetails = TvSeriesDetailsModel(
        id = 1,
        title = "Breaking Bad",
        posterUrl = "https://image.tmdb.org/t/p/w342/ztkUQFLlC19CCMYHW9o1zWhJRNq.jpg",
        backdropUrl = "https://image.tmdb.org/t/p/w780/xlaY2zyzMfkhk0HSC5VUwzoZPU1.jpg",
        airDateLabel = SeriesAirDateLabel.Ended(yearRange = "2008 - 2013"),
        seasonsLabel = "5 Seasons",
        userScore = 88,
        genres = listOf("Drama", "Crime", "Thriller"),
        overview = "A high school chemistry teacher diagnosed with cancer turns to manufacturing " +
                "methamphetamine to secure his family's future.",
        cast = previewCast,
        creators = previewCreators,
        seasons = previewSeasons
                                                          )

@PhoneSizePreviews
@Composable
private fun TvSeriesDetailsScreenContentPreview() {
    TvSeriesDetailsScreenContentPreviewFrame(previewTvSeriesDetails)
}

@Preview(showBackground = true, name = "Multiple creators")
@Composable
private fun TvSeriesDetailsScreenContentMultipleCreatorsPreview() {
    TvSeriesDetailsScreenContentPreviewFrame(
            previewTvSeriesDetails.copy(
                    creators = previewCreators + CastMemberModel(
                            id = 11,
                            name = "Peter Gould",
                            character = "",
                            profileUrl = ""
                                                                 )
                                        )
                                             )
}

@Preview(showBackground = true, name = "Ongoing")
@Composable
private fun TvSeriesDetailsScreenContentOngoingPreview() {
    TvSeriesDetailsScreenContentPreviewFrame(
            previewTvSeriesDetails.copy(airDateLabel = SeriesAirDateLabel.Ongoing(text = "2023 - Ongoing"))
                                             )
}

@Preview(showBackground = true, name = "Upcoming - no data yet")
@Composable
private fun TvSeriesDetailsScreenContentUpcomingPreview() {
    TvSeriesDetailsScreenContentPreviewFrame(
            previewTvSeriesDetails.copy(
                    airDateLabel = SeriesAirDateLabel.Upcoming(
                            premiereLabel = "Premieres",
                            dateText = "October 12, 2026"
                                                               ),
                    userScore = 0,
                    backdropUrl = "",
                    overview = "",
                    cast = emptyList(),
                    creators = emptyList(),
                    seasons = emptyList()
                                        )
                                             )
}

/**
 * Everything the mapper can hand us as empty: no genres, no overview, no
 * score, no cast/creators/seasons, plus a title long enough to need
 * truncating.
 */
@Preview(showBackground = true, name = "Sparse data + long title")
@Composable
private fun TvSeriesDetailsScreenContentSparsePreview() {
    TvSeriesDetailsScreenContentPreviewFrame(
            previewTvSeriesDetails.copy(
                    title = "The Lord of the Rings: The Rings of Power - Extended Edition",
                    airDateLabel = SeriesAirDateLabel.Ended(yearRange = ""),
                    seasonsLabel = "",
                    userScore = 0,
                    genres = emptyList(),
                    overview = "",
                    cast = emptyList(),
                    creators = emptyList(),
                    seasons = emptyList()
                                        )
                                             )
}

@Preview(showBackground = true, name = "Favorited")
@Composable
private fun TvSeriesDetailsScreenContentFavoritedPreview() {
    TvSeriesDetailsScreenContentPreviewFrame(previewTvSeriesDetails.copy(isFavorite = true))
}

@Composable
private fun TvSeriesDetailsScreenContentPreviewFrame(tvSeriesDetails: TvSeriesDetailsModel) {
    MMoviesTheme {
        Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
            TvSeriesDetailsScreenContent(
                    tvSeriesDetails = tvSeriesDetails,
                    modifier = Modifier.padding(innerPadding),
                    showFavoriteStar = true,
                    onFavoriteClicked = {}
                                         )
        }
    }
}
