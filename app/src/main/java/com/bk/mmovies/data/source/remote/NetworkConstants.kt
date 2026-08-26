package com.bk.mmovies.data.source.remote

const val TMDB_WEBPAGE_SIGNUP_URL = "https://www.themoviedb.org/signup"
const val TMDB_WEBPAGE_API_SETTINGS_URL = "https://www.themoviedb.org/settings/api"
const val TMDB_WEBPAGE_API_GETTING_STARTED_URL = "https://developer.themoviedb.org/docs/getting-started"

const val TMDB_API_BASE_URL = "https://api.themoviedb.org/3/"
const val AUTHENTICATION_ENDPOINT = "authentication"
const val LOGIN_VALIDATION_TOKEN_ENDPOINT = "authentication/token/new"
const val LOGIN_WITH_CREDENTIALS_ENDPOINT = "authentication/token/validate_with_login"
const val LOGIN_SESSION_ID_ENDPOINT = "authentication/session/new"
const val GUEST_SESSION_ID_ENDPOINT = "authentication/guest_session/new"
const val DELETE_SESSION_ENDPOINT = "authentication/session"
const val ACCOUNT_DETAILS_ENDPOINT = "account"
const val FAVORITE_ENDPOINT = "account/{account_id}/favorite"
const val FAVORITE_MOVIES_ENDPOINT = "account/{account_id}/favorite/movies"
const val FAVORITE_TV_SERIES_ENDPOINT = "account/{account_id}/favorite/tv"
const val QUERY_PARAM_SESSION_ID = "session_id"
const val QUERY_PARAM_PAGE = "page"
const val QUERY_PARAM_SORT_BY = "sort_by"

const val POPULAR_MOVIES_LIST_ENDPOINT = "movie/popular"
const val UPCOMING_MOVIES_LIST_ENDPOINT = "movie/upcoming"
const val DISCOVER_MOVIES_LIST_ENDPOINT = "discover/movie"
const val NOW_PLAYING_MOVIES_LIST_ENDPOINT = "movie/now_playing"
const val TOP_RATED_MOVIES_LIST_ENDPOINT = "movie/top_rated"

const val POPULAR_TV_SERIES_LIST_ENDPOINT = "tv/popular"
const val AIRING_TODAY_TV_SERIES_LIST_ENDPOINT = "tv/airing_today"
const val ON_TV_TV_SERIES_LIST_ENDPOINT = "tv/on_the_air"
const val TOP_RATED_TV_SERIES_LIST_ENDPOINT = "tv/top_rated"
const val DISCOVER_TV_SERIES_LIST_ENDPOINT = "discover/tv"
const val TV_SERIES_ENDPOINT = "tv/{series_id}"
const val TV_SEASON_ENDPOINT = "tv/{series_id}/season/{season_number}"
const val TV_EPISODE_ENDPOINT = "tv/{series_id}/season/{season_number}/episode/{episode_number}"

const val MOVIE_ENDPOINT = "movie/{movie_id}"
const val PERSON_ENDPOINT = "person/{person_id}"
const val SEARCH_MULTI_ENDPOINT = "search/multi"
const val QUERY_PARAM_QUERY = "query"
const val QUERY_PARAM_API_KEY = "api_key"
const val QUERY_PARAM_LANGUAGE = "language"
const val QUERY_PARAM_APPEND_TO_RESPONSE = "append_to_response"
const val APPEND_TO_RESPONSE_CREDITS = "credits"
const val APPEND_TO_RESPONSE_ACCOUNT_STATES = "account_states"
const val APPEND_TO_RESPONSE_CREDITS_AND_ACCOUNT_STATES = "$APPEND_TO_RESPONSE_CREDITS,$APPEND_TO_RESPONSE_ACCOUNT_STATES"

const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
const val POSTER_PATH_SIZE_SEGMENT_LIST_ITEM = "w342"
const val POSTER_PATH_SIZE_SEGMENT_LIST_ITEM_ZOOM = "w780"
const val AVATAR_PATH_SIZE_SEGMENT = "w185"
const val CAST_PROFILE_PATH_SIZE_SEGMENT = "w185"
const val CAST_PROFILE_PATH_SIZE_SEGMENT_ZOOM = "h632"
// "w300" is TMDB's largest fixed still size, but that's narrower than this
// 128dp list thumbnail renders at on most phone screens (~380-400px at
// typical density) — Coil then has to upscale it, which reads as both blurry
// and, since the blur eats the frame edges, misleadingly "more zoomed in".
// "original" avoids the upscale; Coil downsamples it cheaply for the thumbnail.
const val STILL_PATH_SIZE_SEGMENT = "original"

const val TMDB_ERROR_CODE_INVALID_API_KEY = 7
const val TMDB_ERROR_CODE_SUSPENDED_API_KEY = 10
