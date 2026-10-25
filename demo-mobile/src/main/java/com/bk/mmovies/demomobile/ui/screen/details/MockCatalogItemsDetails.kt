package com.bk.mmovies.demomobile.ui.screen.details

import android.content.res.Resources
import com.bk.mmovies.demomobile.R
import com.bk.mmovies.demomobile.ui.screen.catalog.mockMovieCatalogItems
import com.bk.mmovies.domain.model.CastMemberModel
import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.domain.model.MovieDetailsModel

fun mockCatalogItemsDetails(resources: Resources): List<MovieDetailsModel> = listOf(
    mockMovieDetails(
        item = mockMovieCatalogItems[0],
        runtimeMinutes = 142,
        overview = resources.getString(R.string.mock_overview_last_orbit),
        genres = listOf("Science Fiction", "Drama", "Thriller"),
        cast = listOf(
            resources.person(2, "Commander Iris Vale"),
            resources.person(1, "Flight Engineer Dario Cruz"),
            resources.person(6, "Mission Control Lead Nia"),
            resources.person(5, "Dr. Anton Berg")
        ),
        director = resources.person(3, "Director"),
        writers = listOf(
            resources.person(10, "Screenplay"),
            resources.person(7, "Story")
        )
    ),
    mockMovieDetails(
        item = mockMovieCatalogItems[1],
        runtimeMinutes = 108,
        overview = resources.getString(R.string.mock_overview_midnight_rain),
        genres = listOf("Crime", "Mystery", "Thriller"),
        cast = listOf(
            resources.person(3, "Detective Eli Strand"),
            resources.person(4, "Club Owner Lena Park"),
            resources.person(9, "Missing Musician Kai Ono"),
            resources.person(8, "Journalist Dana Cole")
        ),
        director = resources.person(1, "Director"),
        writers = listOf(
            resources.person(2, "Screenplay")
        )
    ),
    mockMovieDetails(
        item = mockMovieCatalogItems[2],
        runtimeMinutes = 165,
        overview = resources.getString(R.string.mock_overview_the_emerald_gate),
        genres = listOf("Fantasy", "Adventure", "Family"),
        cast = listOf(
            resources.person(10, "Mira the Mapmaker"),
            resources.person(7, "Warden of the Gate"),
            resources.person(6, "Queen of Echoes"),
            resources.person(5, "Village Elder Bram")
        ),
        director = resources.person(4, "Director"),
        writers = listOf(
            resources.person(8, "Screenplay"),
            resources.person(3, "Based on the novel")
        )
    ),
    mockMovieDetails(
        item = mockMovieCatalogItems[3],
        runtimeMinutes = 97,
        overview = resources.getString(R.string.mock_overview_summer_in_bloom),
        genres = listOf("Romance", "Comedy", "Drama"),
        cast = listOf(
            resources.person(4, "Architect Nora Vance"),
            resources.person(9, "Gardener Theo Marsh"),
            resources.person(8, "Grandmother Ada"),
            resources.person(3, "Mayor Felix Orr")
        ),
        director = resources.person(2, "Director"),
        writers = listOf(
            resources.person(6, "Screenplay")
        )
    ),
    mockMovieDetails(
        item = mockMovieCatalogItems[4],
        runtimeMinutes = 124,
        overview = resources.getString(R.string.mock_overview_below_the_surface),
        genres = listOf("Horror", "Science Fiction", "Mystery"),
        cast = listOf(
            resources.person(10, "Rescue Diver Maren Cole"),
            resources.person(7, "Expedition Lead Dr. Abel Ross"),
            resources.person(6, "Sonar Technician Lise"),
            resources.person(5, "Captain Hollis Pike")
        ),
        director = resources.person(9, "Director"),
        writers = listOf(
            resources.person(2, "Screenplay"),
            resources.person(1, "Story")
        )
    ),
    mockMovieDetails(
        item = mockMovieCatalogItems[5],
        runtimeMinutes = 131,
        overview = resources.getString(R.string.mock_overview_dust_and_thunder),
        genres = listOf("Western", "Action", "Drama"),
        cast = listOf(
            resources.person(5, "Rider Cal Dunmore"),
            resources.person(4, "Witness Rosa Vega"),
            resources.person(1, "Landowner Silas Crane"),
            resources.person(8, "Judge Abigail Moore")
        ),
        director = resources.person(7, "Director"),
        writers = listOf(
            resources.person(10, "Screenplay")
        )
    ),
    mockMovieDetails(
        item = mockMovieCatalogItems[6],
        runtimeMinutes = 76,
        overview = resources.getString(R.string.mock_overview_little_cloud),
        genres = listOf("Animation", "Family", "Adventure"),
        cast = listOf(
            resources.person(2, "Little Cloud (voice)"),
            resources.person(9, "Gust the Wind (voice)"),
            resources.person(5, "Old Thunder (voice)"),
            resources.person(4, "Village Mayor (voice)")
        ),
        director = resources.person(8, "Director"),
        writers = listOf(
            resources.person(7, "Screenplay"),
            resources.person(1, "Story")
        )
    ),
    mockMovieDetails(
        item = mockMovieCatalogItems[7],
        runtimeMinutes = 119,
        overview = resources.getString(R.string.mock_overview_the_final_note),
        genres = listOf("Drama", "Music"),
        cast = listOf(
            resources.person(7, "Pianist Julian Frey"),
            resources.person(8, "Mentor Helena Voss"),
            resources.person(3, "Concert Manager Paul"),
            resources.person(10, "Young Julian's Mother")
        ),
        director = resources.person(5, "Director"),
        writers = listOf(
            resources.person(4, "Screenplay")
        )
    ),
    mockMovieDetails(
        item = mockMovieCatalogItems[8],
        runtimeMinutes = 153,
        overview = resources.getString(R.string.mock_overview_frozen_signal),
        genres = listOf("Science Fiction", "Mystery", "Thriller"),
        cast = listOf(
            resources.person(6, "Radio Engineer Vera Lund"),
            resources.person(1, "Station Chief Gabe Holt"),
            resources.person(2, "Meteorologist Irina"),
            resources.person(9, "Technician Rhys Dale")
        ),
        director = resources.person(10, "Director"),
        writers = listOf(
            resources.person(3, "Screenplay"),
            resources.person(4, "Story")
        )
    ),
    mockMovieDetails(
        item = mockMovieCatalogItems[9],
        runtimeMinutes = 89,
        overview = resources.getString(R.string.mock_overview_run_at_dawn),
        genres = listOf("Action", "Thriller", "Crime"),
        cast = listOf(
            resources.person(8, "Courier Jess Rivera"),
            resources.person(7, "Journalist Omar Reyes"),
            resources.person(5, "Assassin Stefan Kord"),
            resources.person(4, "Detective Mia Sorel")
        ),
        director = resources.person(9, "Director"),
        writers = listOf(
            resources.person(6, "Screenplay")
        )
    )
)

private fun mockMovieDetails(
    item: CatalogItem,
    runtimeMinutes: Int,
    overview: String,
    genres: List<String>,
    cast: List<CastMemberModel>,
    director: CastMemberModel,
    writers: List<CastMemberModel>
): MovieDetailsModel = MovieDetailsModel(
    id = item.id,
    title = item.title,
    posterUrl = item.imageUrl,
    backdropUrl = item.backdropUrl,
    releaseDate = item.releaseDate,
    runtime = formatRuntime(runtimeMinutes),
    userScore = item.rating,
    genres = genres,
    overview = overview,
    cast = cast,
    director = director,
    writers = writers,
    isFavorite = false,
    trailerUrl = null
)

private fun Resources.person(personId: Int, character: String): CastMemberModel =
    mockCastMember(this, personId, character)

private fun formatRuntime(totalMinutes: Int): String {
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return if (minutes == 0) "${hours}h" else "${hours}h ${minutes}m"
}
