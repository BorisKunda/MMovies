package com.bk.mmovies.tv.ui.component

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

// Mirrors :app's ActorDetailsViewModel - :tv can't reuse it directly since it
// lives in :app (a presentation-layer class, not shared :core logic), so
// this is the same small amount of fetch/retry logic reimplemented against
// the same :core PersonRepository.
@HiltViewModel
class TvActorDetailsViewModel @Inject constructor(
        private val personRepository: PersonRepository
                                                  ) : ViewModel() {

    private val _personDetailsState = MutableStateFlow<TvPersonDetailsUiState>(TvPersonDetailsUiState.Loading)
    val personDetailsState: StateFlow<TvPersonDetailsUiState> = _personDetailsState.asStateFlow()

    private var personId: Int? = null
    private var loadPersonDetailsJob: Job? = null

    fun loadPersonDetails(personId: Int) {
        // This ViewModel is scoped to the dialog's own hiltViewModel() call,
        // recreated each time the dialog reopens, so unlike :app's version
        // there is no "already loaded, same id" case worth skipping here.
        this.personId = personId
        fetchPersonDetails(personId)
    }

    fun retry() {
        val personId = personId ?: return
        fetchPersonDetails(personId)
    }

    private fun fetchPersonDetails(personId: Int) {
        _personDetailsState.value = TvPersonDetailsUiState.Loading
        loadPersonDetailsJob?.cancel()
        loadPersonDetailsJob = viewModelScope.launch {
            when (val result = personRepository.getPersonDetails(personId)) {
                is PersonDetailsResult.Success -> {
                    _personDetailsState.value = TvPersonDetailsUiState.Content(result.personDetails)
                }
                is PersonDetailsResult.Failure -> {
                    _personDetailsState.value = TvPersonDetailsUiState.Error(result.errorMessage)
                }
            }
        }
    }
}

sealed interface TvPersonDetailsUiState {
    data object Loading : TvPersonDetailsUiState
    data class Content(val personDetails: PersonDetailsModel) : TvPersonDetailsUiState
    data class Error(val errorMessage: String) : TvPersonDetailsUiState
}
