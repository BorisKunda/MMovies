package com.bk.mmovies.ui.component

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bk.mmovies.domain.model.PersonDetailsModel
import com.bk.mmovies.domain.model.result.PersonDetailsResult
import com.bk.mmovies.domain.repository.PersonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActorDetailsViewModel @Inject constructor(
        private val personRepository: PersonRepository
                                                ) : ViewModel() {

    private val _personDetailsState = MutableStateFlow<PersonDetailsUiState>(
            PersonDetailsUiState.Loading
                                                                             )
    val personDetailsState: StateFlow<PersonDetailsUiState> = _personDetailsState.asStateFlow()

    private var personId: Int? = null
    private var loadPersonDetailsJob: Job? = null

    fun loadPersonDetails(personId: Int) {
        // This ViewModel is scoped to the NavBackStackEntry, so it outlives the
        // dialog that opened it. Skipping the refetch for an already-loaded
        // person avoids a redundant call, but a previous *failure* must not be
        // sticky — otherwise reopening the same actor replays the stale error
        // with no way back.
        if (this.personId == personId && _personDetailsState.value !is PersonDetailsUiState.Error) return
        this.personId = personId
        fetchPersonDetails(personId)
    }

    fun retry() {
        val personId = personId ?: return
        fetchPersonDetails(personId)
    }

    private fun fetchPersonDetails(personId: Int) {
        _personDetailsState.value = PersonDetailsUiState.Loading
        loadPersonDetailsJob?.cancel()
        loadPersonDetailsJob = viewModelScope.launch {
            when (val result = personRepository.getPersonDetails(personId)) {
                is PersonDetailsResult.Success -> {
                    _personDetailsState.value = PersonDetailsUiState.Content(result.personDetails)
                }
                is PersonDetailsResult.Failure -> {
                    _personDetailsState.value = PersonDetailsUiState.Error(result.errorMessage)
                }
            }
        }
    }
}

sealed interface PersonDetailsUiState {
    object Loading : PersonDetailsUiState
    data class Content(val personDetails: PersonDetailsModel) : PersonDetailsUiState
    data class Error(val errorMessage: String) : PersonDetailsUiState
}
