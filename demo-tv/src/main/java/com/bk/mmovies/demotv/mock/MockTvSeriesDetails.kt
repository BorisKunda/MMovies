package com.bk.mmovies.demotv.mock

import android.content.res.Resources
import com.bk.mmovies.core.R as CoreR
import com.bk.mmovies.demotv.R
import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.domain.model.SeasonModel
import com.bk.mmovies.domain.model.SeriesAirDateLabel
import com.bk.mmovies.domain.model.TvSeriesDetailsModel

private const val MOCK_SEASON_IMAGE_URI_PREFIX = "android.resource://com.bk.mmovies.demotv/drawable/mock_season_"
private const val MOCK_EPISODE_COUNT = 10
private const val FIRST_SEASON_NUMBER = 1

fun mockTvSeriesDetails(resources: Resources): List<TvSeriesDetailsModel> = listOf(
    mockTvSeries(
        resources = resources,
        item = mockTvSeriesCatalogItems[0],
        slug = "harbor_secrets",
        overview = resources.getString(R.string.mock_overview_harbor_secrets),
        seasonOverview = resources.getString(R.string.mock_season_overview_harbor_secrets_1),
        genres = listOf("Crime", "Drama", "Mystery"),
        cast = listOf(
            4 to "Detective Mara Lind",
            5 to "Harbor Inspector Tom Reyes",
            8 to "Lighthouse Keeper Edna",
            1 to "Fisherman Jonas"
        ),
        creatorIds = listOf(6, 3)
    ),
    mockTvSeries(
        resources = resources,
        item = mockTvSeriesCatalogItems[1],
        slug = "colony_nine",
        overview = resources.getString(R.string.mock_overview_colony_nine),
        seasonOverview = resources.getString(R.string.mock_season_overview_colony_nine_1),
        genres = listOf("Science Fiction", "Drama", "Mystery"),
        cast = listOf(
            10 to "Commander Ysolde Park",
            7 to "Survey Lead Kai Moreno",
            2 to "Dr. Imani Cole",
            9 to "Engineer Rafi Singh"
        ),
        creatorIds = listOf(5, 4)
    ),
    mockTvSeries(
        resources = resources,
        item = mockTvSeriesCatalogItems[2],
        slug = "the_last_dynasty",
        overview = resources.getString(R.string.mock_overview_the_last_dynasty),
        seasonOverview = resources.getString(R.string.mock_season_overview_the_last_dynasty_1),
        genres = listOf("Drama", "History"),
        cast = listOf(
            2 to "Queen Elara",
            3 to "Chancellor Aldric",
            4 to "Lady Isolde",
            9 to "Prince Cassian"
        ),
        creatorIds = listOf(8)
    ),
    mockTvSeries(
        resources = resources,
        item = mockTvSeriesCatalogItems[3],
        slug = "apartment_404",
        overview = resources.getString(R.string.mock_overview_apartment_404),
        seasonOverview = resources.getString(R.string.mock_season_overview_apartment_404_1),
        genres = listOf("Comedy", "Romance"),
        cast = listOf(
            4 to "Priya Anand",
            1 to "Noah Webb",
            6 to "Tess Okoye",
            7 to "Marcus Bell"
        ),
        creatorIds = listOf(10, 5)
    ),
    mockTvSeries(
        resources = resources,
        item = mockTvSeriesCatalogItems[4],
        slug = "wildwood_academy",
        overview = resources.getString(R.string.mock_overview_wildwood_academy),
        seasonOverview = resources.getString(R.string.mock_season_overview_wildwood_academy_1),
        genres = listOf("Fantasy", "Adventure", "Family"),
        cast = listOf(
            8 to "Wren Hollow",
            3 to "Finn Alder",
            10 to "Sage Birch",
            5 to "Headmaster Oakley"
        ),
        creatorIds = listOf(2)
    ),
    mockTvSeries(
        resources = resources,
        item = mockTvSeriesCatalogItems[5],
        slug = "code_of_silence",
        overview = resources.getString(R.string.mock_overview_code_of_silence),
        seasonOverview = resources.getString(R.string.mock_season_overview_code_of_silence_1),
        genres = listOf("Crime", "Thriller", "Drama"),
        cast = listOf(
            6 to "Analyst Nadia Rao",
            1 to "Investigator Cole Marsh",
            4 to "Chief Elise Moran",
            9 to "Technician Dev Patel"
        ),
        creatorIds = listOf(7, 8)
    ),
    mockTvSeries(
        resources = resources,
        item = mockTvSeriesCatalogItems[6],
        slug = "second_shift",
        overview = resources.getString(R.string.mock_overview_second_shift),
        seasonOverview = resources.getString(R.string.mock_season_overview_second_shift_1),
        genres = listOf("Drama"),
        cast = listOf(
            2 to "Dr. Hana Brooks",
            5 to "Dr. Elias Grant",
            4 to "Nurse Rosa Vega",
            7 to "Paramedic Liam Cole"
        ),
        creatorIds = listOf(10)
    ),
    mockTvSeries(
        resources = resources,
        item = mockTvSeriesCatalogItems[7],
        slug = "beyond_the_ridge",
        overview = resources.getString(R.string.mock_overview_beyond_the_ridge),
        seasonOverview = resources.getString(R.string.mock_season_overview_beyond_the_ridge_1),
        genres = listOf("Adventure", "Drama"),
        cast = listOf(
            6 to "Ada Brandt",
            9 to "Silas Brandt",
            8 to "Ranger Joy Whitaker",
            3 to "Hiker Mateo Cruz"
        ),
        creatorIds = listOf(1, 4)
    ),
    mockTvSeries(
        resources = resources,
        item = mockTvSeriesCatalogItems[8],
        slug = "starlight_diner",
        overview = resources.getString(R.string.mock_overview_starlight_diner),
        seasonOverview = resources.getString(R.string.mock_season_overview_starlight_diner_1),
        genres = listOf("Comedy", "Drama"),
        cast = listOf(
            10 to "Waitress Lucy Marlow",
            1 to "Cook Ray Dalton",
            8 to "Regular Dottie Frank",
            5 to "Sheriff Walt Hodge"
        ),
        creatorIds = listOf(2, 7)
    ),
    mockTvSeries(
        resources = resources,
        item = mockTvSeriesCatalogItems[9],
        slug = "the_clockmaker",
        overview = resources.getString(R.string.mock_overview_the_clockmaker),
        seasonOverview = resources.getString(R.string.mock_season_overview_the_clockmaker_1),
        genres = listOf("Mystery", "Fantasy", "Drama"),
        cast = listOf(
            3 to "Clockmaker Anselm Roche",
            4 to "Journalist Vera Holt",
            7 to "Inspector Pell",
            6 to "Madame Orla"
        ),
        creatorIds = listOf(9)
    )
)

private fun mockTvSeries(
    resources: Resources,
    item: CatalogItem,
    slug: String,
    overview: String,
    seasonOverview: String,
    genres: List<String>,
    cast: List<Pair<Int, String>>,
    creatorIds: List<Int>
): TvSeriesDetailsModel {
    val startYear = item.releaseDate.take(4)
    val creatorRole = resources.getString(CoreR.string.details_creator_role)
    val season = SeasonModel(
        id = item.id * 10 + FIRST_SEASON_NUMBER,
        name = resources.getString(R.string.mock_season_name, FIRST_SEASON_NUMBER),
        overview = seasonOverview,
        posterUrl = MOCK_SEASON_IMAGE_URI_PREFIX + slug + "_" + FIRST_SEASON_NUMBER,
        airDate = item.releaseDate,
        seasonNumber = FIRST_SEASON_NUMBER,
        episodeCountLabel = resources.getQuantityString(
                CoreR.plurals.episode_count,
                MOCK_EPISODE_COUNT,
                MOCK_EPISODE_COUNT
                                                       ),
        episodes = mockEpisodes(
                resources = resources,
                item = item,
                slug = slug,
                seasonNumber = FIRST_SEASON_NUMBER,
                seriesIndex = mockTvSeriesCatalogItems.indexOf(item)
                               )
                            )

    return TvSeriesDetailsModel(
        id = item.id,
        title = item.title,
        posterUrl = item.imageUrl,
        backdropUrl = item.backdropUrl,
        airDateLabel = SeriesAirDateLabel.Ongoing(
                "$startYear - ${resources.getString(CoreR.string.series_ongoing_label)}"
                                                 ),
        seasonsLabel = resources.getQuantityString(R.plurals.mock_season_count, 1, 1),
        userScore = item.rating,
        genres = genres,
        overview = overview,
        cast = cast.map { (personId, character) -> mockCastMember(resources, personId, character) },
        creators = creatorIds.map { personId -> mockCastMember(resources, personId, creatorRole) },
        seasons = listOf(season),
        isFavorite = false,
        trailerUrl = null
                             )
}
