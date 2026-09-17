package com.bk.mmovies.data.source.remote.dto

data class GuardianSearchResponseWrapperDto(
        val response: GuardianSearchResponseDto?
                                            )

data class GuardianSearchResponseDto(
        val status: String?,
        val currentPage: Int?,
        val pages: Int?,
        val results: List<GuardianArticleDto>?
                                     )

data class GuardianArticleDto(
        val id: String?,
        val webTitle: String?,
        val webUrl: String?,
        val webPublicationDate: String?,
        val sectionName: String?,
        val fields: GuardianArticleFieldsDto?,
        // Only populated when show-elements=image is requested (the
        // single-item endpoint, for a full-size hero image) - the search/list
        // endpoint never requests it, relying on fields.thumbnail instead.
        val elements: List<GuardianElementDto>? = null
                              )

data class GuardianElementDto(
        val type: String?,
        val assets: List<GuardianAssetDto>?
                              )

data class GuardianAssetDto(
        val file: String?,
        val typeData: GuardianAssetTypeDataDto?
                            )

data class GuardianAssetTypeDataDto(
        // Guardian returns this as a numeric string ("1000"), not a number.
        val width: String?
                                    )

data class GuardianArticleFieldsDto(
        val thumbnail: String?,
        val trailText: String?,
        val byline: String?,
        // Only populated when show-fields includes "body" - the search/list
        // endpoint never requests it (see GUARDIAN_SHOW_FIELDS), only the
        // single-item endpoint used for a full article's content does.
        val body: String? = null
                                    )

data class GuardianItemResponseWrapperDto(
        val response: GuardianItemResponseDto?
                                          )

data class GuardianItemResponseDto(
        val status: String?,
        val content: GuardianArticleDto?
                                   )
