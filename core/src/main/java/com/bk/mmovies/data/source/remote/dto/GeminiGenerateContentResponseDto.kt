package com.bk.mmovies.data.source.remote.dto

data class GeminiGenerateContentResponseDto(
        val candidates: List<GeminiCandidateDto>?
                                            )

data class GeminiCandidateDto(
        val content: GeminiContentDto?
                              )

// The shape of the JSON text Gemini is asked (via responseMimeType) to
// return inside candidates[0].content.parts[0].text - a second, nested
// parse step, not part of the outer Gemini response envelope above.
data class RecommendationSuggestionDto(
        val query: String?,
        val mediaType: String?,
        val reason: String?
                                       )
