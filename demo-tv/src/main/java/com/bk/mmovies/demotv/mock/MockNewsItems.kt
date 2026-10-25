package com.bk.mmovies.demotv.mock

import android.content.res.Resources
import com.bk.mmovies.data.source.remote.dto.ArticleDto
import com.bk.mmovies.demotv.R
import com.bk.mmovies.domain.model.NewsItem

private const val MOCK_ARTICLE_IMAGE_URI_PREFIX = "android.resource://com.bk.mmovies.demotv/drawable/mock_article_"
private const val MOCK_ARTICLE_URL_PREFIX = "https://example.com/mock-articles/"

fun mockArticles(resources: Resources): List<NewsItem> = listOf(
    mockArticle(
        resources = resources,
        slug = "last_orbit_first_look",
        titleRes = R.string.mock_article_title_last_orbit_first_look,
        descriptionRes = R.string.mock_article_description_last_orbit_first_look,
        contentRes = R.string.mock_article_content_last_orbit_first_look,
        sourceName = "Demo Screen Daily",
        author = "Jordan Ellis",
        publishedAt = "October 23, 2026",
        publishedAtIso = "2026-10-23T08:30:00Z"
    ),
    mockArticle(
        resources = resources,
        slug = "harbor_secrets_season_two",
        titleRes = R.string.mock_article_title_harbor_secrets_season_two,
        descriptionRes = R.string.mock_article_description_harbor_secrets_season_two,
        contentRes = R.string.mock_article_content_harbor_secrets_season_two,
        sourceName = "Mock Film Weekly",
        author = "Riley Hart",
        publishedAt = "October 21, 2026",
        publishedAtIso = "2026-10-21T09:30:00Z"
    ),
    mockArticle(
        resources = resources,
        slug = "animation_miniature_worlds",
        titleRes = R.string.mock_article_title_animation_miniature_worlds,
        descriptionRes = R.string.mock_article_description_animation_miniature_worlds,
        contentRes = R.string.mock_article_content_animation_miniature_worlds,
        sourceName = "Demo Screen Daily",
        author = "Morgan Blake",
        publishedAt = "October 19, 2026",
        publishedAtIso = "2026-10-19T10:30:00Z"
    ),
    mockArticle(
        resources = resources,
        slug = "film_festival_opening_night",
        titleRes = R.string.mock_article_title_film_festival_opening_night,
        descriptionRes = R.string.mock_article_description_film_festival_opening_night,
        contentRes = R.string.mock_article_content_film_festival_opening_night,
        sourceName = "Mock Film Weekly",
        author = "Casey Rowan",
        publishedAt = "October 17, 2026",
        publishedAtIso = "2026-10-17T11:30:00Z"
    ),
    mockArticle(
        resources = resources,
        slug = "the_final_note_recording",
        titleRes = R.string.mock_article_title_the_final_note_recording,
        descriptionRes = R.string.mock_article_description_the_final_note_recording,
        contentRes = R.string.mock_article_content_the_final_note_recording,
        sourceName = "Demo Screen Daily",
        author = "Avery Quinn",
        publishedAt = "October 15, 2026",
        publishedAtIso = "2026-10-15T12:30:00Z"
    ),
    mockArticle(
        resources = resources,
        slug = "practical_effects_workshop",
        titleRes = R.string.mock_article_title_practical_effects_workshop,
        descriptionRes = R.string.mock_article_description_practical_effects_workshop,
        contentRes = R.string.mock_article_content_practical_effects_workshop,
        sourceName = "Mock Film Weekly",
        author = "Taylor Brooks",
        publishedAt = "October 13, 2026",
        publishedAtIso = "2026-10-13T13:30:00Z"
    ),
    mockArticle(
        resources = resources,
        slug = "apartment_404_on_set",
        titleRes = R.string.mock_article_title_apartment_404_on_set,
        descriptionRes = R.string.mock_article_description_apartment_404_on_set,
        contentRes = R.string.mock_article_content_apartment_404_on_set,
        sourceName = "Demo Screen Daily",
        author = "Jamie Lowe",
        publishedAt = "October 11, 2026",
        publishedAtIso = "2026-10-11T14:30:00Z"
    ),
    mockArticle(
        resources = resources,
        slug = "restoring_classic_cinema",
        titleRes = R.string.mock_article_title_restoring_classic_cinema,
        descriptionRes = R.string.mock_article_description_restoring_classic_cinema,
        contentRes = R.string.mock_article_content_restoring_classic_cinema,
        sourceName = "Mock Film Weekly",
        author = "Sam Fletcher",
        publishedAt = "October 9, 2026",
        publishedAtIso = "2026-10-09T15:30:00Z"
    ),
    mockArticle(
        resources = resources,
        slug = "rooftop_stunts",
        titleRes = R.string.mock_article_title_rooftop_stunts,
        descriptionRes = R.string.mock_article_description_rooftop_stunts,
        contentRes = R.string.mock_article_content_rooftop_stunts,
        sourceName = "Demo Screen Daily",
        author = "Drew Sutton",
        publishedAt = "October 7, 2026",
        publishedAtIso = "2026-10-07T16:30:00Z"
    ),
    mockArticle(
        resources = resources,
        slug = "starlight_diner_production_design",
        titleRes = R.string.mock_article_title_starlight_diner_production_design,
        descriptionRes = R.string.mock_article_description_starlight_diner_production_design,
        contentRes = R.string.mock_article_content_starlight_diner_production_design,
        sourceName = "Mock Film Weekly",
        author = "Robin Hale",
        publishedAt = "October 5, 2026",
        publishedAtIso = "2026-10-05T17:30:00Z"
    )
)

private fun mockArticle(
    resources: Resources,
    slug: String,
    titleRes: Int,
    descriptionRes: Int,
    contentRes: Int,
    sourceName: String,
    author: String,
    publishedAt: String,
    publishedAtIso: String
): NewsItem = NewsItem(
    title = resources.getString(titleRes),
    description = resources.getString(descriptionRes),
    articleUrl = MOCK_ARTICLE_URL_PREFIX + slug,
    imageUrl = MOCK_ARTICLE_IMAGE_URI_PREFIX + slug,
    sourceName = sourceName,
    author = author,
    publishedAt = publishedAt,
    publishedAtIso = publishedAtIso,
    content = resources.getString(R.string.mock_article_disclaimer) + "\n\n" + resources.getString(contentRes)
)
