package com.bk.mmovies.data.source.remote

import com.bk.mmovies.core.BuildConfig

const val NEWS_API_WEBPAGE_ACCOUNT = "https://newsapi.org/account"
const val NEWS_API_WEBPAGE_GET_STARTED = "https://newsapi.org/docs/get-started"
const val NEWS_API_BASE_URL = "https://newsapi.org/v2/"

// Supplied at build time from the gitignored newsapi.properties (see
// core/build.gradle.kts) rather than committed as a literal.
val NEWS_API_KEY: String = BuildConfig.NEWS_API_KEY
const val NEWS_TOP_HEADLINES_ENDPOINT = "top-headlines"
const val NEWS_EVERYTHING_ENDPOINT = "everything"
const val QUERY_PARAM_NEWS_COUNTRY = "country"
const val QUERY_PARAM_NEWS_LANGUAGE = "language"
const val QUERY_PARAM_NEWS_QUERY = "q"
const val QUERY_PARAM_NEWS_SORT_BY = "sortBy"
const val QUERY_PARAM_NEWS_CATEGORY = "category"
const val QUERY_PARAM_NEWS_API_KEY = "apiKey"
const val QUERY_PARAM_NEWS_PAGE = "page"
const val QUERY_PARAM_NEWS_PAGE_SIZE = "pageSize"

const val NEWS_DEFAULT_COUNTRY = "us"
const val NEWS_CATEGORY_ENTERTAINMENT = "entertainment"
const val NEWS_PAGE_SIZE = 20
const val NEWS_MAX_RESULTS = 100
const val NEWS_SORT_BY_PUBLISHED_AT = "publishedAt"

// top-headlines has no `language` parameter (only /everything does), so
// localized news goes through /everything with an entertainment query per
// language instead of category=entertainment.
const val NEWS_QUERY_ENGLISH = "movies OR film OR \"TV series\" OR actor"
const val NEWS_QUERY_RUSSIAN = "кино OR фильм OR сериал OR актер"
const val NEWS_QUERY_HEBREW = "סרט OR סדרה OR שחקן OR קולנוע"
