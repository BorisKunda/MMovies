package com.bk.mmovies.data.source.remote

const val TMDB_WEBPAGE_URL = "https://www.themoviedb.org"
const val TMDB_WEBPAGE_SIGNUP_URL = "https://www.themoviedb.org/signup"
const val TMDB_WEBPAGE_API_SETTINGS_URL = "https://www.themoviedb.org/settings/api"
const val TMDB_WEBPAGE_API_GETTING_STARTED_URL = "https://developer.themoviedb.org/docs/getting-started"

const val TMDB_WEBVIEW_LOGIN_URL = "https://www.themoviedb.org/authenticate/"
const val TMDB_WEBVIEW_LOGIN_REDIRECT_URL = "mmovies://tmdb-auth"

const val TMDB_API_BASE_URL = "https://api.themoviedb.org/3/"
const val AUTHENTICATION_ENDPOINT = "authentication"
const val LOGIN_VALIDATION_TOKEN_ENDPOINT = "authentication/token/new"
const val LOGIN_WITH_CREDENTIALS_ENDPOINT = "authentication/token/validate_with_login"
const val LOGIN_SESSION_ID_ENDPOINT = "authentication/session/new"
const val GUEST_SESSION_ID_ENDPOINT = "authentication/guest_session/new"
const val DELETE_SESSION_ENDPOINT = "authentication/session"
const val ACCOUNT_DETAILS_ENDPOINT = "account"
const val QUERY_PARAM_SESSION_ID = "session_id"

const val POPULAR_MOVIES_LIST_ENDPOINT = "movie/popular"
const val UPCOMING_MOVIES_LIST_ENDPOINT = "movie/upcoming"
const val DISCOVER_MOVIES_LIST_ENDPOINT = "discover/movie"
const val NOW_PLAYING_MOVIES_LIST_ENDPOINT = "movie/now_playing"
const val TOP_RATED_MOVIES_LIST_ENDPOINT = "movie/top_rated"

const val MOVIE_ENDPOINT = "movie/{movie_id}"
const val QUERY_PARAM_API_KEY = "api_key"
const val QUERY_PARAM_LANGUAGE = "language"
const val QUERY_PARAM_APPEND_TO_RESPONSE = "append_to_response"
const val APPEND_TO_RESPONSE_CREDITS = "credits"

const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
const val POSTER_PATH_SIZE_SEGMENT_LIST_ITEM = "w342"
const val POSTER_PATH_SIZE_SEGMENT_LIST_ITEM_ZOOM = "w780"
const val AVATAR_PATH_SIZE_SEGMENT = "w185"
const val CAST_PROFILE_PATH_SIZE_SEGMENT = "w185"

const val TMDB_ERROR_CODE_INVALID_API_KEY = 7
const val TMDB_ERROR_CODE_SUSPENDED_API_KEY = 10
