package com.bk.mmovies.data.source.remote.api

import com.bk.mmovies.data.source.remote.GEMINI_GENERATE_CONTENT_ENDPOINT
import com.bk.mmovies.data.source.remote.GEMINI_MODEL
import com.bk.mmovies.data.source.remote.PATH_PARAM_GEMINI_MODEL
import com.bk.mmovies.data.source.remote.QUERY_PARAM_GEMINI_API_KEY
import com.bk.mmovies.data.source.remote.dto.GeminiGenerateContentRequestDto
import com.bk.mmovies.data.source.remote.dto.GeminiGenerateContentResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GeminiApi {
    @POST(GEMINI_GENERATE_CONTENT_ENDPOINT)
    suspend fun generateContent(
            @Path(PATH_PARAM_GEMINI_MODEL) model: String = GEMINI_MODEL,
            @Query(QUERY_PARAM_GEMINI_API_KEY) apiKey: String,
            @Body request: GeminiGenerateContentRequestDto
                                ): Response<GeminiGenerateContentResponseDto>
}
