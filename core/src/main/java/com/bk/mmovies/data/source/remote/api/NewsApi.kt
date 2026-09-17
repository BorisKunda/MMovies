package com.bk.mmovies.data.source.remote.api

import com.bk.mmovies.data.source.remote.NEWS_API_KEY
import com.bk.mmovies.data.source.remote.NEWS_CATEGORY_ENTERTAINMENT
import com.bk.mmovies.data.source.remote.NEWS_DEFAULT_COUNTRY
import com.bk.mmovies.data.source.remote.NEWS_PAGE_SIZE
import com.bk.mmovies.data.source.remote.NEWS_TOP_HEADLINES_ENDPOINT
import com.bk.mmovies.data.source.remote.QUERY_PARAM_NEWS_API_KEY
import com.bk.mmovies.data.source.remote.QUERY_PARAM_NEWS_CATEGORY
import com.bk.mmovies.data.source.remote.QUERY_PARAM_NEWS_COUNTRY
import com.bk.mmovies.data.source.remote.QUERY_PARAM_NEWS_PAGE
import com.bk.mmovies.data.source.remote.QUERY_PARAM_NEWS_PAGE_SIZE
import com.bk.mmovies.data.source.remote.dto.NewsResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Dedicated Retrofit interface for NewsAPI (https://newsapi.org), kept
 * separate from [com.bk.mmovies.data.source.remote.api.TmdbApi] since it
 * targets a different base URL and uses its own API key scheme.
 */
interface NewsApi {

    @GET(NEWS_TOP_HEADLINES_ENDPOINT)
    suspend fun getTopHeadlines(
            @Query(QUERY_PARAM_NEWS_COUNTRY) country: String = NEWS_DEFAULT_COUNTRY,
            @Query(QUERY_PARAM_NEWS_CATEGORY) category: String = NEWS_CATEGORY_ENTERTAINMENT,
            @Query(QUERY_PARAM_NEWS_PAGE) page: Int = 1,
            @Query(QUERY_PARAM_NEWS_PAGE_SIZE) pageSize: Int = NEWS_PAGE_SIZE,
            @Query(QUERY_PARAM_NEWS_API_KEY) apiKey: String = NEWS_API_KEY
                               ): Response<NewsResponseDto>
}
