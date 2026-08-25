package com.bk.mmovies.ui.screen.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bk.mmovies.R
import com.bk.mmovies.domain.model.CastMemberModel
import com.bk.mmovies.domain.model.SearchResultMediaType
import com.bk.mmovies.domain.model.SearchResultModel
import com.bk.mmovies.ui.component.ActorDetailsDialog
import com.bk.mmovies.ui.screen.search.screencomponents.SearchFilterRow
import com.bk.mmovies.ui.screen.search.screencomponents.SearchIdleContent
import com.bk.mmovies.ui.screen.search.screencomponents.SearchResultsContent

private val searchFieldHorizontalPadding = 4.dp
private val searchBarHorizontalPadding = 8.dp
private val searchBarVerticalPadding = 4.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
        onNavigateToMovieDetails: (movieId: Int) -> Unit,
        onNavigateToTvSeriesDetails: (seriesId: Int) -> Unit,
        onBack: () -> Unit
                 ) {
    val searchViewModel = hiltViewModel<SearchViewModel>()
    val query by searchViewModel.query.collectAsStateWithLifecycle()
    val resultsState by searchViewModel.resultsState.collectAsStateWithLifecycle()
    val recentSearches by searchViewModel.recentSearches.collectAsStateWithLifecycle()
    val selectedFilters by searchViewModel.selectedFilters.collectAsStateWithLifecycle()

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var castMemberForDialog by remember { mutableStateOf<CastMemberModel?>(null) }

    Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                Column {
                    Row(
                            modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                            horizontal = searchBarHorizontalPadding,
                                            vertical = searchBarVerticalPadding
                                            ),
                            verticalAlignment = Alignment.CenterVertically
                       ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.search_back_action),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                        }
                        TextField(
                                value = query,
                                onValueChange = { searchViewModel.onQueryChanged(it) },
                                modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = searchFieldHorizontalPadding)
                                        .focusRequester(focusRequester),
                                placeholder = { Text(stringResource(R.string.search_field_hint)) },
                                singleLine = true,
                                trailingIcon = {
                                    if (query.isNotEmpty()) {
                                        IconButton(onClick = { searchViewModel.onQueryChanged("") }) {
                                            Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = stringResource(R.string.search_field_clear_action)
                                                )
                                        }
                                    }
                                },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(onSearch = {
                                    searchViewModel.onSearchSubmitted()
                                    keyboardController?.hide()
                                }),
                                colors = TextFieldDefaults.colors(
                                        focusedIndicatorColor = MaterialTheme.colorScheme.onSurface,
                                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                        focusedContainerColor = MaterialTheme.colorScheme.background,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.background
                                                                 )
                                 )
                    }
                    SearchFilterRow(
                            selectedFilters = selectedFilters,
                            onFilterToggled = { searchViewModel.onFilterToggled(it) }
                                    )
                }
            }
            ) { innerPadding ->
        Box(
                modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
           ) {
            if (query.isBlank()) {
                SearchIdleContent(
                        recentSearches = recentSearches,
                        onRecentSearchClicked = { searchViewModel.onRecentSearchClicked(it) },
                        onRemoveRecentSearchClicked = { searchViewModel.onRemoveRecentSearchClicked(it) },
                        onClearRecentSearchesClicked = { searchViewModel.onClearRecentSearchesClicked() }
                                  )
            } else {
                SearchResultsContent(
                        state = resultsState,
                        selectedFilters = selectedFilters,
                        onResultClicked = { result ->
                            handleResultClicked(result, searchViewModel) { castMember ->
                                castMemberForDialog = castMember
                            }
                        },
                        onRetry = { searchViewModel.retry() }
                                     )
            }
        }
    }

    castMemberForDialog?.let { castMember ->
        ActorDetailsDialog(
                castMember = castMember,
                onDismiss = { castMemberForDialog = null }
                           )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    LaunchedEffect(Unit) {
        searchViewModel.goToMovieDetailsNavEvent.collect { movieId ->
            onNavigateToMovieDetails(movieId)
        }
    }
    LaunchedEffect(Unit) {
        searchViewModel.goToTvSeriesDetailsNavEvent.collect { seriesId ->
            onNavigateToTvSeriesDetails(seriesId)
        }
    }
}

// Person results have no details screen of their own; instead of routing
// through the ViewModel's nav events (which only cover movie/tv), the click
// is handled right here so the screen can show the existing ActorDetailsDialog.
private fun handleResultClicked(
        result: SearchResultModel,
        searchViewModel: SearchViewModel,
        onShowPersonDialog: (CastMemberModel) -> Unit
                                ) {
    if (result.mediaType == SearchResultMediaType.PERSON) {
        onShowPersonDialog(
                CastMemberModel(
                        id = result.id,
                        name = result.title,
                        character = "",
                        profileUrl = result.imageUrl
                                )
                          )
    }
    searchViewModel.onResultClicked(result)
}
