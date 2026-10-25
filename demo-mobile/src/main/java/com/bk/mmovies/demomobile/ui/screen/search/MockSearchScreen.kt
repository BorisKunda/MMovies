package com.bk.mmovies.demomobile.ui.screen.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bk.mmovies.demomobile.R
import com.bk.mmovies.demomobile.ui.screen.search.screencomponents.SearchFilterRow
import com.bk.mmovies.demomobile.ui.screen.search.screencomponents.SearchIdleContent
import com.bk.mmovies.demomobile.ui.screen.search.screencomponents.SearchResultsContent
import com.bk.mmovies.domain.model.SearchResultMediaType
import com.bk.mmovies.domain.model.SearchResultModel
import com.bk.mmovies.ui.component.PoweredByTmdbFooter

private val searchFieldHorizontalPadding = 4.dp
private val searchBarHorizontalPadding = 8.dp
private val searchBarVerticalPadding = 4.dp

@Composable
fun MockSearchScreen(
        onBack: () -> Unit,
        onNavigateToMovieDetails: (movieId: Int) -> Unit,
        onNavigateToTvSeriesDetails: (seriesId: Int) -> Unit,
        viewModel: MockSearchViewModel = viewModel()
                    ) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val resultsState by viewModel.resultsState.collectAsStateWithLifecycle()
    val recentSearches by viewModel.recentSearches.collectAsStateWithLifecycle()
    val selectedFilters by viewModel.selectedFilters.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.goToMovieDetailsNavEvent.collect { movieId ->
            onNavigateToMovieDetails(movieId)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.goToTvSeriesDetailsNavEvent.collect { seriesId ->
            onNavigateToTvSeriesDetails(seriesId)
        }
    }

    MockSearchScreenContent(
            query = query,
            resultsState = resultsState,
            recentSearches = recentSearches,
            selectedFilters = selectedFilters,
            onBack = onBack,
            onQueryChanged = viewModel::onQueryChanged,
            onSearchSubmitted = viewModel::onSearchSubmitted,
            onFilterToggled = viewModel::onFilterToggled,
            onResultClicked = viewModel::onResultClicked,
            onRecentSearchClicked = viewModel::onRecentSearchClicked,
            onRemoveRecentSearchClicked = viewModel::onRemoveRecentSearchClicked,
            onClearRecentSearchesClicked = viewModel::onClearRecentSearchesClicked
                           )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MockSearchScreenContent(
        query: String,
        resultsState: MockSearchResultsState,
        recentSearches: List<String>,
        selectedFilters: Set<SearchResultMediaType>,
        onBack: () -> Unit,
        onQueryChanged: (String) -> Unit,
        onSearchSubmitted: () -> Unit,
        onFilterToggled: (SearchResultMediaType) -> Unit,
        onResultClicked: (SearchResultModel) -> Unit,
        onRecentSearchClicked: (String) -> Unit,
        onRemoveRecentSearchClicked: (String) -> Unit,
        onClearRecentSearchesClicked: () -> Unit
                           ) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = { PoweredByTmdbFooter(modifier = Modifier.alpha(0f), onClick = {}) },
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
                                onValueChange = onQueryChanged,
                                modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = searchFieldHorizontalPadding)
                                        .focusRequester(focusRequester),
                                placeholder = {
                                    Text(
                                            text = stringResource(R.string.search_screen_field_hint),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                },
                                singleLine = true,
                                trailingIcon = {
                                    if (query.isNotEmpty()) {
                                        IconButton(onClick = { onQueryChanged("") }) {
                                            Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = stringResource(R.string.search_field_clear_action)
                                                )
                                        }
                                    }
                                },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(onSearch = {
                                    onSearchSubmitted()
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
                            onFilterToggled = onFilterToggled
                                    )
                }
            }
            ) { innerPadding ->
        Box(
                modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .imePadding()
           ) {
            if (query.isBlank()) {
                SearchIdleContent(
                        recentSearches = recentSearches,
                        onRecentSearchClicked = onRecentSearchClicked,
                        onRemoveRecentSearchClicked = onRemoveRecentSearchClicked,
                        onClearRecentSearchesClicked = onClearRecentSearchesClicked
                                  )
            } else {
                SearchResultsContent(
                        state = resultsState,
                        selectedFilters = selectedFilters,
                        onResultClicked = onResultClicked
                                     )
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
