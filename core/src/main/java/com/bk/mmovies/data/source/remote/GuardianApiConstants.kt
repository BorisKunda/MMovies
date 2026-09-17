package com.bk.mmovies.data.source.remote

import com.bk.mmovies.core.BuildConfig

const val GUARDIAN_WEBPAGE_GET_STARTED = "https://open-platform.theguardian.com/access/"
const val GUARDIAN_API_BASE_URL = "https://content.guardianapis.com/"

// Supplied at build time from the gitignored guardian.properties (see
// core/build.gradle.kts) rather than committed as a literal.
val GUARDIAN_API_KEY: String = BuildConfig.GUARDIAN_API_KEY
const val GUARDIAN_SEARCH_ENDPOINT = "search"
const val QUERY_PARAM_GUARDIAN_SECTION = "section"
const val QUERY_PARAM_GUARDIAN_PAGE = "page"
const val QUERY_PARAM_GUARDIAN_PAGE_SIZE = "page-size"
const val QUERY_PARAM_GUARDIAN_SHOW_FIELDS = "show-fields"
const val QUERY_PARAM_GUARDIAN_ORDER_BY = "order-by"
const val QUERY_PARAM_GUARDIAN_API_KEY = "api-key"

// Movie/TV news specifically. Guardian's "film" section is movies-only and
// misses TV/actor coverage, which lives under the separate "tv-and-radio"
// section - the search API ORs multiple section values with "|", so this
// covers both, same role NewsAPI's category=entertainment plays for :app.
const val GUARDIAN_SECTIONS_FILM_AND_TV = "film|tv-and-radio"
const val GUARDIAN_ORDER_BY_NEWEST = "newest"
// thumbnail/trailText/byline are enough for the news row; the full "body"
// field is only requested by the single-item endpoint (getArticle), once a
// user actually opens one article for NewsDetailsTvScreen.
const val GUARDIAN_SHOW_FIELDS = "thumbnail,trailText,byline"
const val GUARDIAN_PAGE_SIZE = 20

// Requested only by the single-item endpoint (getArticle), when a user
// actually opens one article - see NewsRepository.getNewsArticleContent.
// fields.thumbnail (used for the news row card) is a small ~140px crop -
// show-elements=image returns the same picture at every size Guardian's own
// site uses, so a much larger one can be picked for the details screen's
// hero image instead of upscaling that thumbnail.
const val GUARDIAN_SHOW_FIELDS_BODY = "body"
const val QUERY_PARAM_GUARDIAN_SHOW_ELEMENTS = "show-elements"
const val GUARDIAN_SHOW_ELEMENTS_IMAGE = "image"
const val GUARDIAN_ELEMENT_TYPE_IMAGE = "image"
const val PATH_PARAM_GUARDIAN_ARTICLE_ID = "id"
