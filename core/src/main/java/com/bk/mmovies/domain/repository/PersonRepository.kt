package com.bk.mmovies.domain.repository

import com.bk.mmovies.domain.model.result.PersonDetailsResult

interface PersonRepository {
    suspend fun getPersonDetails(personId: Int): PersonDetailsResult
}
