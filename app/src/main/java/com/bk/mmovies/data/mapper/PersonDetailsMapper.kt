package com.bk.mmovies.data.mapper

import com.bk.mmovies.data.source.remote.CAST_PROFILE_PATH_SIZE_SEGMENT
import com.bk.mmovies.data.source.remote.dto.PersonDetailsDto
import com.bk.mmovies.domain.model.PersonDetailsModel
import com.bk.mmovies.locale.LocaleMonitor
import javax.inject.Inject

class PersonDetailsMapper @Inject constructor(
        private val localeMonitor: LocaleMonitor
                                              ) {

    fun toModel(dto: PersonDetailsDto): PersonDetailsModel = PersonDetailsModel(
            id = dto.id ?: 0,
            name = dto.name ?: "",
            biography = dto.biography ?: "",
            birthday = dto.birthday?.let { getFormattedDate(it) } ?: "",
            placeOfBirth = dto.placeOfBirth ?: "",
            profileUrl = dto.profilePath?.let { getFullImageUrl(CAST_PROFILE_PATH_SIZE_SEGMENT, it) } ?: "")

    private fun getFormattedDate(date: String): String =
            formatTmdbDate(date, localeMonitor.currentLanguage.value.locale) ?: date
}
