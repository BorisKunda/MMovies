package com.bk.mmovies.data.source.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

private const val CACHE_MAX_AGE_SECONDS = 60

// TMDB doesn't send Cache-Control headers on its responses, so OkHttp's disk
// cache (see NetworkModule) would never actually store anything without this.
// Installed as a network interceptor so it stamps a short max-age onto every
// GET response, letting identical requests made moments apart — quickly
// re-opening a details screen, a locale-change reload of the current category
// — be served from disk instead of hitting the network again. POST calls
// (favorites, auth) are never HTTP-cacheable regardless, so this can't cause
// a stale favorite/login state.
@Singleton
class NetworkCacheInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        return response.newBuilder()
                .header("Cache-Control", "public, max-age=$CACHE_MAX_AGE_SECONDS")
                .build()
    }
}
