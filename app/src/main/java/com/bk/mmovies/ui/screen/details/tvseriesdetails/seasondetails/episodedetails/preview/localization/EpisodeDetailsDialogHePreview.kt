package com.bk.mmovies.ui.screen.details.tvseriesdetails.seasondetails.episodedetails.preview.localization

import androidx.compose.runtime.Composable
import com.bk.mmovies.domain.model.CastMemberModel
import com.bk.mmovies.domain.model.EpisodeModel
import com.bk.mmovies.ui.screen.details.tvseriesdetails.seasondetails.episodedetails.EpisodeDetailsDialog
import com.bk.mmovies.ui.theme.HebrewPhoneSizePreviews
import com.bk.mmovies.ui.theme.MMoviesTheme

// Hebrew content too (not just the UI chrome) so the preview exercises real
// RTL text shaping and bidi mixing with the Latin runtime/score numerals.
private val previewDirectorHebrew = CastMemberModel(
        id = 1,
        name = "וינס גיליגן",
        character = "",
        profileUrl = ""
                                                     )

private val previewWritersHebrew = listOf(
        CastMemberModel(id = 2, name = "וינס גיליגן", character = "", profileUrl = ""),
        CastMemberModel(id = 3, name = "פיטר גולד", character = "", profileUrl = "")
                                          )

private val previewEpisodeHebrew = EpisodeModel(
        id = 1,
        name = "פיילוט",
        overview = "מורה לכימיה בתיכון שאובחן עם סרטן פונה לייצור מתאמפטמין כדי להבטיח את עתיד משפחתו.",
        airDate = "20 בינואר 2008",
        episodeNumber = 1,
        seasonNumber = 1,
        stillUrl = "https://image.tmdb.org/t/p/w780/3P3iyPzjy5W7ZUdatlHRW9Vzp6c.jpg",
        rating = 82,
        runtime = "58ד",
        director = previewDirectorHebrew,
        writers = previewWritersHebrew
                                                )

@HebrewPhoneSizePreviews
@Composable
private fun EpisodeDetailsDialogPreviewHebrew() {
    MMoviesTheme {
        EpisodeDetailsDialog(episode = previewEpisodeHebrew, onDismiss = {})
    }
}
