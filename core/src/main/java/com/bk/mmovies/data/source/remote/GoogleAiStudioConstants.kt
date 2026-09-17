package com.bk.mmovies.data.source.remote

import com.bk.mmovies.core.BuildConfig

const val GOOGLE_AI_STUDIO_WEBPAGE_API_KEYS = "https://aistudio.google.com/api-keys"
const val GOOGLE_AI_STUDIO_WEBPAGE_GETTING_STARTED = "https://aistudio.google.com/docs/get-started"
const val GEMINI_API_BASE_URL = "https://generativelanguage.googleapis.com/"

// Supplied at build time from the gitignored gemini.properties (see
// core/build.gradle.kts) rather than committed as a literal - falls back to
// an empty string so a fresh clone still compiles, just without a working
// AI recommendation call until a key is added. Used as the fallback in
// AiRecommendationRepositoryImpl when no runtime key has been saved yet.
val GEMINI_API_KEY: String = BuildConfig.GEMINI_API_KEY
const val GEMINI_MODEL = "gemini-3.6-flash"
const val GEMINI_GENERATE_CONTENT_ENDPOINT = "v1beta/models/{model}:generateContent"
const val PATH_PARAM_GEMINI_MODEL = "model"
const val QUERY_PARAM_GEMINI_API_KEY = "key"
