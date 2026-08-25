package com.bk.mmovies.data.repositoryimpl

import android.content.Context
import com.bk.mmovies.R
import com.bk.mmovies.data.mapper.PersonDetailsMapper
import com.bk.mmovies.data.source.remote.NetworkManager
import com.bk.mmovies.data.source.remote.api.TmdbApi
import com.bk.mmovies.data.source.remote.dto.PersonDetailsDto
import com.bk.mmovies.data.source.remote.result.ApiCallResult
import com.bk.mmovies.domain.model.result.PersonDetailsResult
import com.bk.mmovies.domain.repository.PersonRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PersonRepositoryImpl @Inject constructor(
        private val api: TmdbApi,
        private val networkManager: NetworkManager,
        private val personDetailsMapper: PersonDetailsMapper,
        @ApplicationContext private val context: Context
                                               ) : PersonRepository {
    private val failureMessage: String
        get() = context.getString(R.string.error_person_details_load_failed)

    override suspend fun getPersonDetails(personId: Int): PersonDetailsResult {
        val apiCallResult: ApiCallResult<PersonDetailsDto> = networkManager.executeApiCall(
                "GetPersonDetails",
                apiCall = { -> api.getPersonDetails(personId) })

        return when (apiCallResult) {
            is ApiCallResult.Success<PersonDetailsDto> -> {
                PersonDetailsResult.Success(personDetailsMapper.toModel(apiCallResult.data))
            }
            is ApiCallResult.Failure                   -> {
                PersonDetailsResult.Failure(failureMessage)
            }
        }
    }
}
