package com.bk.mmovies.ui.screen.details.tvseriesdetails.seasondetails.episodedetails.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.bk.mmovies.domain.model.CastMemberModel
import com.bk.mmovies.domain.model.EpisodeModel
import com.bk.mmovies.ui.screen.details.tvseriesdetails.seasondetails.episodedetails.EpisodeDetailsDialog
import com.bk.mmovies.ui.theme.MMoviesTheme
import com.bk.mmovies.ui.theme.PhoneSizePreviews

private val previewDirector = CastMemberModel(
        id = 1,
        name = "Vince Gilligan",
        character = "",
        profileUrl = ""
                                              )

private val previewWriters = listOf(
        CastMemberModel(id = 2, name = "Vince Gilligan", character = "", profileUrl = ""),
        CastMemberModel(id = 3, name = "Peter Gould", character = "", profileUrl = "")
                                    )

private val previewEpisode = EpisodeModel(
        id = 1,
        name = "Pilot",
        overview = "A high school chemistry teacher diagnosed with cancer turns to " +
                "manufacturing methamphetamine to secure his family's future.",
        airDate = "January 20, 2008",
        episodeNumber = 1,
        seasonNumber = 1,
        stillUrl = "https://image.tmdb.org/t/p/w780/3P3iyPzjy5W7ZUdatlHRW9Vzp6c.jpg",
        rating = 82,
        runtime = "58m",
        director = previewDirector,
        writers = previewWriters
                                          )

@PhoneSizePreviews
@Composable
private fun EpisodeDetailsDialogPreview() {
    EpisodeDetailsDialogPreviewFrame(previewEpisode)
}

@Preview(showBackground = true, name = "No score, no crew")
@Composable
private fun EpisodeDetailsDialogNoCrewPreview() {
    EpisodeDetailsDialogPreviewFrame(
            previewEpisode.copy(rating = 0, director = null, writers = emptyList())
                                     )
}

@Preview(showBackground = true, name = "Sparse data + long title")
@Composable
private fun EpisodeDetailsDialogSparsePreview() {
    EpisodeDetailsDialogPreviewFrame(
            previewEpisode.copy(
                    name = "A Very Long Episode Title That Should Still Read Clearly In The Dialog",
                    overview = "",
                    airDate = "",
                    runtime = "",
                    rating = 0,
                    stillUrl = "",
                    director = null,
                    writers = emptyList()
                                )
                                     )
}

@Composable
private fun EpisodeDetailsDialogPreviewFrame(episode: EpisodeModel) {
    MMoviesTheme {
        EpisodeDetailsDialog(episode = episode, onDismiss = {})
    }
}
