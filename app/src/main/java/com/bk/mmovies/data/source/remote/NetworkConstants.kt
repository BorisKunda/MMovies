package com.bk.mmovies.data.source.remote

const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
const val TOKEN_VALIDITY_ENDPOINT = "authentication"
const val POPULAR_MOVIES_LIST_ENDPOINT = "movie/popular"
const val UPCOMING_MOVIES_LIST_ENDPOINT = "movie/upcoming"
const val NOW_PLAYING_MOVIES_LIST_ENDPOINT = "movie/now_playing"
const val TOP_RATED_MOVIES_LIST_ENDPOINT = "movie/top_rated"
const val POSTER_PATH_SIZE_SEGMENT_LIST_ITEM = "w342"
const val QUERY_PARAM_API_KEY = "api_key"
const val NETWORK_TAG = "Network"
const val TOKEN_VALIDITY_RESPONSE_FIELD = "success"
const val MOVIES_RESPONSE_FIELD = "results"
const val ERROR_MESSAGE_RESPONSE_FIELD = "status_message"
const val ERROR_CODE_RESPONSE_FIELD = "status_code"

const val TMDB_ERROR_CODE_INVALID_API_KEY = 7
const val TMDB_ERROR_CODE_SUSPENDED_API_KEY = 10
