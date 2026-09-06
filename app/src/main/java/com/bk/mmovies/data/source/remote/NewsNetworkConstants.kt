package com.bk.mmovies.data.source.remote

const val NEWS_API_BASE_URL = "https://newsapi.org/v2/"

// NewsAPI free-plan key hard-coded for now per task scope; must move to a
// safer configuration (local.properties/BuildConfig/remote config) before
// this app is published or distributed.
const val NEWS_API_KEY = "a0c10d7285bb4e968a686dff05e778fb"

const val NEWS_TOP_HEADLINES_ENDPOINT = "top-headlines"

const val QUERY_PARAM_NEWS_COUNTRY = "country"
const val QUERY_PARAM_NEWS_CATEGORY = "category"
const val QUERY_PARAM_NEWS_API_KEY = "apiKey"
const val QUERY_PARAM_NEWS_PAGE = "page"
const val QUERY_PARAM_NEWS_PAGE_SIZE = "pageSize"

const val NEWS_DEFAULT_COUNTRY = "us"
const val NEWS_CATEGORY_ENTERTAINMENT = "entertainment"

// NewsAPI's own default/max for pageSize is 20/100; 20 keeps each page's
// payload comparable to a TMDB catalog page.
const val NEWS_PAGE_SIZE = 20

// Hard ceiling every NewsAPI free "Developer" plan account is capped at,
// regardless of what totalResults reports for a given query - requesting a
// page past this offset returns a "maximumResultsReached" error instead of
// data. See NewsRepositoryImpl, which folds this into the totalPages it
// derives so the app never attempts a page beyond it.
const val NEWS_MAX_RESULTS = 100
