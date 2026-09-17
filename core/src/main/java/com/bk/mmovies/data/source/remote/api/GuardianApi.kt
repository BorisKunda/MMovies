package com.bk.mmovies.data.source.remote.api

import com.bk.mmovies.data.source.remote.GUARDIAN_API_KEY
import com.bk.mmovies.data.source.remote.GUARDIAN_ORDER_BY_NEWEST
import com.bk.mmovies.data.source.remote.GUARDIAN_PAGE_SIZE
import com.bk.mmovies.data.source.remote.GUARDIAN_SEARCH_ENDPOINT
import com.bk.mmovies.data.source.remote.GUARDIAN_SECTIONS_FILM_AND_TV
import com.bk.mmovies.data.source.remote.GUARDIAN_SHOW_ELEMENTS_IMAGE
import com.bk.mmovies.data.source.remote.GUARDIAN_SHOW_FIELDS
import com.bk.mmovies.data.source.remote.GUARDIAN_SHOW_FIELDS_BODY
import com.bk.mmovies.data.source.remote.PATH_PARAM_GUARDIAN_ARTICLE_ID
import com.bk.mmovies.data.source.remote.QUERY_PARAM_GUARDIAN_API_KEY
import com.bk.mmovies.data.source.remote.QUERY_PARAM_GUARDIAN_ORDER_BY
import com.bk.mmovies.data.source.remote.QUERY_PARAM_GUARDIAN_PAGE
import com.bk.mmovies.data.source.remote.QUERY_PARAM_GUARDIAN_PAGE_SIZE
import com.bk.mmovies.data.source.remote.QUERY_PARAM_GUARDIAN_SECTION
import com.bk.mmovies.data.source.remote.QUERY_PARAM_GUARDIAN_SHOW_ELEMENTS
import com.bk.mmovies.data.source.remote.QUERY_PARAM_GUARDIAN_SHOW_FIELDS
import com.bk.mmovies.data.source.remote.dto.GuardianItemResponseWrapperDto
import com.bk.mmovies.data.source.remote.dto.GuardianSearchResponseWrapperDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Dedicated Retrofit interface for the Guardian Content API
 * (https://content.guardianapis.com), kept separate from [NewsApi] since it
 * targets a different base URL and uses its own API key scheme. Backs :tv's
 * News feature only - :app still uses [NewsApi]/NewsAPI.
 */
interface GuardianApi {

    @GET(GUARDIAN_SEARCH_ENDPOINT)
    suspend fun search(
            @Query(QUERY_PARAM_GUARDIAN_SECTION) section: String = GUARDIAN_SECTIONS_FILM_AND_TV,
            @Query(QUERY_PARAM_GUARDIAN_PAGE) page: Int = 1,
            @Query(QUERY_PARAM_GUARDIAN_PAGE_SIZE) pageSize: Int = GUARDIAN_PAGE_SIZE,
            @Query(QUERY_PARAM_GUARDIAN_SHOW_FIELDS) showFields: String = GUARDIAN_SHOW_FIELDS,
            @Query(QUERY_PARAM_GUARDIAN_ORDER_BY) orderBy: String = GUARDIAN_ORDER_BY_NEWEST,
            @Query(QUERY_PARAM_GUARDIAN_API_KEY) apiKey: String = GUARDIAN_API_KEY
                       ): Response<GuardianSearchResponseWrapperDto>

    // id is a path like "film/2026/sep/16/..." (see
    // GuardianNewsRepositoryImpl's derivation from an article's webUrl) -
    // encoded = true keeps its slashes literal instead of percent-encoded.
    @GET("{$PATH_PARAM_GUARDIAN_ARTICLE_ID}")
    suspend fun getArticle(
            @Path(PATH_PARAM_GUARDIAN_ARTICLE_ID, encoded = true) id: String,
            @Query(QUERY_PARAM_GUARDIAN_SHOW_FIELDS) showFields: String = GUARDIAN_SHOW_FIELDS_BODY,
            @Query(QUERY_PARAM_GUARDIAN_SHOW_ELEMENTS) showElements: String = GUARDIAN_SHOW_ELEMENTS_IMAGE,
            @Query(QUERY_PARAM_GUARDIAN_API_KEY) apiKey: String = GUARDIAN_API_KEY
                          ): Response<GuardianItemResponseWrapperDto>
}
