package com.bk.mmovies.data.source.remote.dto

import com.google.gson.annotations.SerializedName

data class GeminiGenerateContentRequestDto(
        val contents: List<GeminiContentDto>,
        val generationConfig: GeminiGenerationConfigDto
                                           )

data class GeminiContentDto(
        val parts: List<GeminiPartDto>
                            )

data class GeminiPartDto(
        val text: String
                         )

// Forces the model to return a raw JSON string in the response text instead
// of prose/markdown, so it can be parsed directly rather than scraped out of
// a ```json fenced block or similar.
data class GeminiGenerationConfigDto(
        @SerializedName("responseMimeType")
        val responseMimeType: String = "application/json"
                                     )
