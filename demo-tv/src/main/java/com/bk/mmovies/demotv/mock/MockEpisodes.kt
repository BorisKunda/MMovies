package com.bk.mmovies.demotv.mock

import android.content.res.Resources
import com.bk.mmovies.demotv.R
import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.domain.model.EpisodeModel
import java.time.LocalDate

private const val MOCK_EPISODE_IMAGE_URI_PREFIX = "android.resource://com.bk.mmovies.demotv/drawable/mock_episode_"
private val episodeRuntimesInMinutes = listOf(44, 47, 42, 51, 45, 49, 43, 52, 46, 48)
private const val EPISODES_PER_WEEK_OFFSET = 1L
private const val MIN_EPISODE_RATING = 60
private const val EPISODE_RATING_SPREAD = 36

private data class MockEpisodeTexts(val nameRes: Int, val overviewRes: Int)

private val mockEpisodeTextsBySeriesSlug: Map<String, List<MockEpisodeTexts>> = mapOf(
    "harbor_secrets" to listOf(
        MockEpisodeTexts(R.string.mock_episode_name_harbor_secrets_1_01, R.string.mock_episode_overview_harbor_secrets_1_01),
        MockEpisodeTexts(R.string.mock_episode_name_harbor_secrets_1_02, R.string.mock_episode_overview_harbor_secrets_1_02),
        MockEpisodeTexts(R.string.mock_episode_name_harbor_secrets_1_03, R.string.mock_episode_overview_harbor_secrets_1_03),
        MockEpisodeTexts(R.string.mock_episode_name_harbor_secrets_1_04, R.string.mock_episode_overview_harbor_secrets_1_04),
        MockEpisodeTexts(R.string.mock_episode_name_harbor_secrets_1_05, R.string.mock_episode_overview_harbor_secrets_1_05),
        MockEpisodeTexts(R.string.mock_episode_name_harbor_secrets_1_06, R.string.mock_episode_overview_harbor_secrets_1_06),
        MockEpisodeTexts(R.string.mock_episode_name_harbor_secrets_1_07, R.string.mock_episode_overview_harbor_secrets_1_07),
        MockEpisodeTexts(R.string.mock_episode_name_harbor_secrets_1_08, R.string.mock_episode_overview_harbor_secrets_1_08),
        MockEpisodeTexts(R.string.mock_episode_name_harbor_secrets_1_09, R.string.mock_episode_overview_harbor_secrets_1_09),
        MockEpisodeTexts(R.string.mock_episode_name_harbor_secrets_1_10, R.string.mock_episode_overview_harbor_secrets_1_10)
    ),
    "colony_nine" to listOf(
        MockEpisodeTexts(R.string.mock_episode_name_colony_nine_1_01, R.string.mock_episode_overview_colony_nine_1_01),
        MockEpisodeTexts(R.string.mock_episode_name_colony_nine_1_02, R.string.mock_episode_overview_colony_nine_1_02),
        MockEpisodeTexts(R.string.mock_episode_name_colony_nine_1_03, R.string.mock_episode_overview_colony_nine_1_03),
        MockEpisodeTexts(R.string.mock_episode_name_colony_nine_1_04, R.string.mock_episode_overview_colony_nine_1_04),
        MockEpisodeTexts(R.string.mock_episode_name_colony_nine_1_05, R.string.mock_episode_overview_colony_nine_1_05),
        MockEpisodeTexts(R.string.mock_episode_name_colony_nine_1_06, R.string.mock_episode_overview_colony_nine_1_06),
        MockEpisodeTexts(R.string.mock_episode_name_colony_nine_1_07, R.string.mock_episode_overview_colony_nine_1_07),
        MockEpisodeTexts(R.string.mock_episode_name_colony_nine_1_08, R.string.mock_episode_overview_colony_nine_1_08),
        MockEpisodeTexts(R.string.mock_episode_name_colony_nine_1_09, R.string.mock_episode_overview_colony_nine_1_09),
        MockEpisodeTexts(R.string.mock_episode_name_colony_nine_1_10, R.string.mock_episode_overview_colony_nine_1_10)
    ),
    "the_last_dynasty" to listOf(
        MockEpisodeTexts(R.string.mock_episode_name_the_last_dynasty_1_01, R.string.mock_episode_overview_the_last_dynasty_1_01),
        MockEpisodeTexts(R.string.mock_episode_name_the_last_dynasty_1_02, R.string.mock_episode_overview_the_last_dynasty_1_02),
        MockEpisodeTexts(R.string.mock_episode_name_the_last_dynasty_1_03, R.string.mock_episode_overview_the_last_dynasty_1_03),
        MockEpisodeTexts(R.string.mock_episode_name_the_last_dynasty_1_04, R.string.mock_episode_overview_the_last_dynasty_1_04),
        MockEpisodeTexts(R.string.mock_episode_name_the_last_dynasty_1_05, R.string.mock_episode_overview_the_last_dynasty_1_05),
        MockEpisodeTexts(R.string.mock_episode_name_the_last_dynasty_1_06, R.string.mock_episode_overview_the_last_dynasty_1_06),
        MockEpisodeTexts(R.string.mock_episode_name_the_last_dynasty_1_07, R.string.mock_episode_overview_the_last_dynasty_1_07),
        MockEpisodeTexts(R.string.mock_episode_name_the_last_dynasty_1_08, R.string.mock_episode_overview_the_last_dynasty_1_08),
        MockEpisodeTexts(R.string.mock_episode_name_the_last_dynasty_1_09, R.string.mock_episode_overview_the_last_dynasty_1_09),
        MockEpisodeTexts(R.string.mock_episode_name_the_last_dynasty_1_10, R.string.mock_episode_overview_the_last_dynasty_1_10)
    ),
    "apartment_404" to listOf(
        MockEpisodeTexts(R.string.mock_episode_name_apartment_404_1_01, R.string.mock_episode_overview_apartment_404_1_01),
        MockEpisodeTexts(R.string.mock_episode_name_apartment_404_1_02, R.string.mock_episode_overview_apartment_404_1_02),
        MockEpisodeTexts(R.string.mock_episode_name_apartment_404_1_03, R.string.mock_episode_overview_apartment_404_1_03),
        MockEpisodeTexts(R.string.mock_episode_name_apartment_404_1_04, R.string.mock_episode_overview_apartment_404_1_04),
        MockEpisodeTexts(R.string.mock_episode_name_apartment_404_1_05, R.string.mock_episode_overview_apartment_404_1_05),
        MockEpisodeTexts(R.string.mock_episode_name_apartment_404_1_06, R.string.mock_episode_overview_apartment_404_1_06),
        MockEpisodeTexts(R.string.mock_episode_name_apartment_404_1_07, R.string.mock_episode_overview_apartment_404_1_07),
        MockEpisodeTexts(R.string.mock_episode_name_apartment_404_1_08, R.string.mock_episode_overview_apartment_404_1_08),
        MockEpisodeTexts(R.string.mock_episode_name_apartment_404_1_09, R.string.mock_episode_overview_apartment_404_1_09),
        MockEpisodeTexts(R.string.mock_episode_name_apartment_404_1_10, R.string.mock_episode_overview_apartment_404_1_10)
    ),
    "wildwood_academy" to listOf(
        MockEpisodeTexts(R.string.mock_episode_name_wildwood_academy_1_01, R.string.mock_episode_overview_wildwood_academy_1_01),
        MockEpisodeTexts(R.string.mock_episode_name_wildwood_academy_1_02, R.string.mock_episode_overview_wildwood_academy_1_02),
        MockEpisodeTexts(R.string.mock_episode_name_wildwood_academy_1_03, R.string.mock_episode_overview_wildwood_academy_1_03),
        MockEpisodeTexts(R.string.mock_episode_name_wildwood_academy_1_04, R.string.mock_episode_overview_wildwood_academy_1_04),
        MockEpisodeTexts(R.string.mock_episode_name_wildwood_academy_1_05, R.string.mock_episode_overview_wildwood_academy_1_05),
        MockEpisodeTexts(R.string.mock_episode_name_wildwood_academy_1_06, R.string.mock_episode_overview_wildwood_academy_1_06),
        MockEpisodeTexts(R.string.mock_episode_name_wildwood_academy_1_07, R.string.mock_episode_overview_wildwood_academy_1_07),
        MockEpisodeTexts(R.string.mock_episode_name_wildwood_academy_1_08, R.string.mock_episode_overview_wildwood_academy_1_08),
        MockEpisodeTexts(R.string.mock_episode_name_wildwood_academy_1_09, R.string.mock_episode_overview_wildwood_academy_1_09),
        MockEpisodeTexts(R.string.mock_episode_name_wildwood_academy_1_10, R.string.mock_episode_overview_wildwood_academy_1_10)
    ),
    "code_of_silence" to listOf(
        MockEpisodeTexts(R.string.mock_episode_name_code_of_silence_1_01, R.string.mock_episode_overview_code_of_silence_1_01),
        MockEpisodeTexts(R.string.mock_episode_name_code_of_silence_1_02, R.string.mock_episode_overview_code_of_silence_1_02),
        MockEpisodeTexts(R.string.mock_episode_name_code_of_silence_1_03, R.string.mock_episode_overview_code_of_silence_1_03),
        MockEpisodeTexts(R.string.mock_episode_name_code_of_silence_1_04, R.string.mock_episode_overview_code_of_silence_1_04),
        MockEpisodeTexts(R.string.mock_episode_name_code_of_silence_1_05, R.string.mock_episode_overview_code_of_silence_1_05),
        MockEpisodeTexts(R.string.mock_episode_name_code_of_silence_1_06, R.string.mock_episode_overview_code_of_silence_1_06),
        MockEpisodeTexts(R.string.mock_episode_name_code_of_silence_1_07, R.string.mock_episode_overview_code_of_silence_1_07),
        MockEpisodeTexts(R.string.mock_episode_name_code_of_silence_1_08, R.string.mock_episode_overview_code_of_silence_1_08),
        MockEpisodeTexts(R.string.mock_episode_name_code_of_silence_1_09, R.string.mock_episode_overview_code_of_silence_1_09),
        MockEpisodeTexts(R.string.mock_episode_name_code_of_silence_1_10, R.string.mock_episode_overview_code_of_silence_1_10)
    ),
    "second_shift" to listOf(
        MockEpisodeTexts(R.string.mock_episode_name_second_shift_1_01, R.string.mock_episode_overview_second_shift_1_01),
        MockEpisodeTexts(R.string.mock_episode_name_second_shift_1_02, R.string.mock_episode_overview_second_shift_1_02),
        MockEpisodeTexts(R.string.mock_episode_name_second_shift_1_03, R.string.mock_episode_overview_second_shift_1_03),
        MockEpisodeTexts(R.string.mock_episode_name_second_shift_1_04, R.string.mock_episode_overview_second_shift_1_04),
        MockEpisodeTexts(R.string.mock_episode_name_second_shift_1_05, R.string.mock_episode_overview_second_shift_1_05),
        MockEpisodeTexts(R.string.mock_episode_name_second_shift_1_06, R.string.mock_episode_overview_second_shift_1_06),
        MockEpisodeTexts(R.string.mock_episode_name_second_shift_1_07, R.string.mock_episode_overview_second_shift_1_07),
        MockEpisodeTexts(R.string.mock_episode_name_second_shift_1_08, R.string.mock_episode_overview_second_shift_1_08),
        MockEpisodeTexts(R.string.mock_episode_name_second_shift_1_09, R.string.mock_episode_overview_second_shift_1_09),
        MockEpisodeTexts(R.string.mock_episode_name_second_shift_1_10, R.string.mock_episode_overview_second_shift_1_10)
    ),
    "beyond_the_ridge" to listOf(
        MockEpisodeTexts(R.string.mock_episode_name_beyond_the_ridge_1_01, R.string.mock_episode_overview_beyond_the_ridge_1_01),
        MockEpisodeTexts(R.string.mock_episode_name_beyond_the_ridge_1_02, R.string.mock_episode_overview_beyond_the_ridge_1_02),
        MockEpisodeTexts(R.string.mock_episode_name_beyond_the_ridge_1_03, R.string.mock_episode_overview_beyond_the_ridge_1_03),
        MockEpisodeTexts(R.string.mock_episode_name_beyond_the_ridge_1_04, R.string.mock_episode_overview_beyond_the_ridge_1_04),
        MockEpisodeTexts(R.string.mock_episode_name_beyond_the_ridge_1_05, R.string.mock_episode_overview_beyond_the_ridge_1_05),
        MockEpisodeTexts(R.string.mock_episode_name_beyond_the_ridge_1_06, R.string.mock_episode_overview_beyond_the_ridge_1_06),
        MockEpisodeTexts(R.string.mock_episode_name_beyond_the_ridge_1_07, R.string.mock_episode_overview_beyond_the_ridge_1_07),
        MockEpisodeTexts(R.string.mock_episode_name_beyond_the_ridge_1_08, R.string.mock_episode_overview_beyond_the_ridge_1_08),
        MockEpisodeTexts(R.string.mock_episode_name_beyond_the_ridge_1_09, R.string.mock_episode_overview_beyond_the_ridge_1_09),
        MockEpisodeTexts(R.string.mock_episode_name_beyond_the_ridge_1_10, R.string.mock_episode_overview_beyond_the_ridge_1_10)
    ),
    "starlight_diner" to listOf(
        MockEpisodeTexts(R.string.mock_episode_name_starlight_diner_1_01, R.string.mock_episode_overview_starlight_diner_1_01),
        MockEpisodeTexts(R.string.mock_episode_name_starlight_diner_1_02, R.string.mock_episode_overview_starlight_diner_1_02),
        MockEpisodeTexts(R.string.mock_episode_name_starlight_diner_1_03, R.string.mock_episode_overview_starlight_diner_1_03),
        MockEpisodeTexts(R.string.mock_episode_name_starlight_diner_1_04, R.string.mock_episode_overview_starlight_diner_1_04),
        MockEpisodeTexts(R.string.mock_episode_name_starlight_diner_1_05, R.string.mock_episode_overview_starlight_diner_1_05),
        MockEpisodeTexts(R.string.mock_episode_name_starlight_diner_1_06, R.string.mock_episode_overview_starlight_diner_1_06),
        MockEpisodeTexts(R.string.mock_episode_name_starlight_diner_1_07, R.string.mock_episode_overview_starlight_diner_1_07),
        MockEpisodeTexts(R.string.mock_episode_name_starlight_diner_1_08, R.string.mock_episode_overview_starlight_diner_1_08),
        MockEpisodeTexts(R.string.mock_episode_name_starlight_diner_1_09, R.string.mock_episode_overview_starlight_diner_1_09),
        MockEpisodeTexts(R.string.mock_episode_name_starlight_diner_1_10, R.string.mock_episode_overview_starlight_diner_1_10)
    ),
    "the_clockmaker" to listOf(
        MockEpisodeTexts(R.string.mock_episode_name_the_clockmaker_1_01, R.string.mock_episode_overview_the_clockmaker_1_01),
        MockEpisodeTexts(R.string.mock_episode_name_the_clockmaker_1_02, R.string.mock_episode_overview_the_clockmaker_1_02),
        MockEpisodeTexts(R.string.mock_episode_name_the_clockmaker_1_03, R.string.mock_episode_overview_the_clockmaker_1_03),
        MockEpisodeTexts(R.string.mock_episode_name_the_clockmaker_1_04, R.string.mock_episode_overview_the_clockmaker_1_04),
        MockEpisodeTexts(R.string.mock_episode_name_the_clockmaker_1_05, R.string.mock_episode_overview_the_clockmaker_1_05),
        MockEpisodeTexts(R.string.mock_episode_name_the_clockmaker_1_06, R.string.mock_episode_overview_the_clockmaker_1_06),
        MockEpisodeTexts(R.string.mock_episode_name_the_clockmaker_1_07, R.string.mock_episode_overview_the_clockmaker_1_07),
        MockEpisodeTexts(R.string.mock_episode_name_the_clockmaker_1_08, R.string.mock_episode_overview_the_clockmaker_1_08),
        MockEpisodeTexts(R.string.mock_episode_name_the_clockmaker_1_09, R.string.mock_episode_overview_the_clockmaker_1_09),
        MockEpisodeTexts(R.string.mock_episode_name_the_clockmaker_1_10, R.string.mock_episode_overview_the_clockmaker_1_10)
    )
)

fun mockEpisodes(
    resources: Resources,
    item: CatalogItem,
    slug: String,
    seasonNumber: Int,
    seriesIndex: Int
): List<EpisodeModel> {
    val firstAirDate = LocalDate.parse(item.releaseDate)
    return mockEpisodeTextsBySeriesSlug.getValue(slug).mapIndexed { index, texts ->
        val episodeNumber = index + 1
        val stillName = "%02d".format(episodeNumber)
        EpisodeModel(
            id = item.id * 1000 + seasonNumber * 100 + episodeNumber,
            name = resources.getString(texts.nameRes),
            overview = resources.getString(texts.overviewRes),
            airDate = firstAirDate.plusWeeks(index * EPISODES_PER_WEEK_OFFSET).toString(),
            episodeNumber = episodeNumber,
            seasonNumber = seasonNumber,
            stillUrl = MOCK_EPISODE_IMAGE_URI_PREFIX + slug + "_" + seasonNumber + "_" + stillName,
            rating = MIN_EPISODE_RATING + (seriesIndex * 7 + episodeNumber * 13) % EPISODE_RATING_SPREAD,
            runtime = "${episodeRuntimesInMinutes[index % episodeRuntimesInMinutes.size]}m",
            director = mockCastMember(resources, (seriesIndex + episodeNumber) % mockPeople.size + 1, resources.getString(R.string.details_director_role)),
            writers = listOf(
                mockCastMember(resources, (seriesIndex + episodeNumber + 3) % mockPeople.size + 1, resources.getString(R.string.details_writer_role))
            )
        )
    }
}
