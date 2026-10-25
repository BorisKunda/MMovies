package com.bk.mmovies.demotv.ui.component

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.bk.mmovies.demotv.mock.mockPeople
import com.bk.mmovies.domain.model.PersonDetailsModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val MOCK_PERSON_IMAGE_URI_PREFIX = "android.resource://com.bk.mmovies.demotv/drawable/mock_person_"

class TvActorDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val _personDetailsState = MutableStateFlow<TvPersonDetailsUiState>(TvPersonDetailsUiState.Loading)
    val personDetailsState: StateFlow<TvPersonDetailsUiState> = _personDetailsState.asStateFlow()

    private var personId: Int? = null

    fun loadPersonDetails(personId: Int) {
        this.personId = personId
        val resources = getApplication<Application>().resources
        val person = mockPeople.firstOrNull { it.id == personId }
        _personDetailsState.value = if (person != null) {
            TvPersonDetailsUiState.Content(
                    PersonDetailsModel(
                            id = person.id,
                            name = resources.getString(person.nameRes),
                            biography = resources.getString(person.biographyRes),
                            birthday = "",
                            placeOfBirth = "",
                            profileUrl = MOCK_PERSON_IMAGE_URI_PREFIX + person.slug
                                       )
                                          )
        } else {
            TvPersonDetailsUiState.Error("")
        }
    }

    fun retry() {
        personId?.let(::loadPersonDetails)
    }
}

sealed interface TvPersonDetailsUiState {
    data object Loading : TvPersonDetailsUiState
    data class Content(val personDetails: PersonDetailsModel) : TvPersonDetailsUiState
    data class Error(val errorMessage: String) : TvPersonDetailsUiState
}
