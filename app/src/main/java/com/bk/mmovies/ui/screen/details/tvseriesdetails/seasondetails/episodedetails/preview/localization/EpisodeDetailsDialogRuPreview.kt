package com.bk.mmovies.ui.screen.details.tvseriesdetails.seasondetails.episodedetails.preview.localization

import androidx.compose.runtime.Composable
import com.bk.mmovies.domain.model.CastMemberModel
import com.bk.mmovies.domain.model.EpisodeModel
import com.bk.mmovies.ui.screen.details.tvseriesdetails.seasondetails.episodedetails.EpisodeDetailsDialog
import com.bk.mmovies.ui.theme.MMoviesTheme
import com.bk.mmovies.ui.theme.RussianPhoneSizePreviews

// Russian content too, since the point of this one is length rather than
// direction: the title, meta line and crew rows are the tightest fits in
// this dialog.
private val previewDirectorRussian = CastMemberModel(
        id = 1,
        name = "Винс Гиллиган",
        character = "",
        profileUrl = ""
                                                      )

private val previewWritersRussian = listOf(
        CastMemberModel(id = 2, name = "Винс Гиллиган", character = "", profileUrl = ""),
        CastMemberModel(id = 3, name = "Питер Гулд", character = "", profileUrl = "")
                                           )

private val previewEpisodeRussian = EpisodeModel(
        id = 1,
        name = "Пилот",
        overview = "Учитель химии, больной раком, начинает готовить метамфетамин, чтобы " +
                "обеспечить будущее своей семьи.",
        airDate = "20 января 2008",
        episodeNumber = 1,
        seasonNumber = 1,
        stillUrl = "https://image.tmdb.org/t/p/w780/3P3iyPzjy5W7ZUdatlHRW9Vzp6c.jpg",
        rating = 82,
        runtime = "58м",
        director = previewDirectorRussian,
        writers = previewWritersRussian
                                                 )

@RussianPhoneSizePreviews
@Composable
private fun EpisodeDetailsDialogPreviewRussian() {
    MMoviesTheme {
        EpisodeDetailsDialog(episode = previewEpisodeRussian, onDismiss = {})
    }
}
