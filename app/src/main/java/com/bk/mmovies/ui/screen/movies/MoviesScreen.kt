package com.bk.mmovies.ui.screen.movies

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bk.mmovies.R
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.MovieModel
import com.bk.mmovies.ui.component.CategoryListPopupView
import com.bk.mmovies.ui.component.EmptyStateView
import com.bk.mmovies.ui.component.GenericErrorScreen
import com.bk.mmovies.ui.theme.MMoviesTheme
import com.bk.mmovies.util.logDebug
import kotlinx.coroutines.launch

private const val TAG = "MoviesScreen"

private val categoryIconEndPadding = 6.dp
private val categorySelectorCornerShape = 18.dp
private val categorySelectorBorderWidth = 1.dp
private val categorySelectorVerticalMargin = 12.dp
private val categorySelectorContentHorizontalPadding = 12.dp
private val categorySelectorContentVerticalPadding = 6.dp
private val categoryIconSize = 16.dp
private val categoryArrowSize = 18.dp
private val categoryArrowStartPadding = 4.dp

// Matches the poster's start margin in MovieRow: cardPaddingHorizontal (12dp) + rowPaddingHorizontal (16dp).
private val categorySelectorStartPadding = 28.dp

// Ensures the gap below the selector never shrinks below the normal card-to-card gap
// once the list is scrolled past its own top content padding.
private val categorySelectorListSpacing = 12.dp

private val categorySelectorTopMargin = 12.dp

private const val CATEGORY_SELECTOR_CONTAINER_ALPHA = 0.06f
private const val CATEGORY_SELECTOR_BORDER_ALPHA = 0.18f
private const val CATEGORY_SELECTOR_ARROW_ALPHA = 0.7f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesScreen(
        onNavigateToMovieDetails: (id: Int) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val moviesViewModel = hiltViewModel<MoviesViewModel>()
    val state by moviesViewModel.moviesScreenState.collectAsStateWithLifecycle()
    val selectedCategory by moviesViewModel.selectedCategory.collectAsStateWithLifecycle()

    MoviesScreenContent(
            state = state,
            selectedCategory = selectedCategory,
            onMovieClicked = { id -> moviesViewModel.handleMovieClick(id) },
            onCategorySelected = { category -> moviesViewModel.handleCategorySelected(category) },
            onRetry = { moviesViewModel.retry() }
                        )

    LaunchedEffect(Unit) {
        moviesViewModel.goToMovieDetailsWithIdNavEvent.collect { movieId: Int ->
            onNavigateToMovieDetails(movieId)
        }
    }
    DisposableEffect(lifecycleOwner) {
        // Composed/Disposed rather than a log on every recomposition, which is
        // what a bare logDebug in the composable body produced.
        logDebug(
                TAG,
                "Composed"
                )
        onDispose {
            logDebug(
                    TAG,
                    "Disposed"
                    )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoviesScreenContent(
        state: MoviesScreenState,
        selectedCategory: MovieCategory,
        onMovieClicked: (id: Int) -> Unit,
        onCategorySelected: (category: MovieCategory) -> Unit,
        onRetry: () -> Unit
                                ) {
    var isCategoryPopupVisible by remember { mutableStateOf(false) }
    val categorySheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    Column(
            modifier = Modifier
                    .fillMaxSize()
                    .padding(top = categorySelectorTopMargin),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
          ) {
        CategorySelector(
                selectedCategory = selectedCategory,
                onClick = { isCategoryPopupVisible = true }
                         )

        Spacer(modifier = Modifier.height(categorySelectorListSpacing))

        when (state) {
            is MoviesScreenState.Loading -> {
                MovieRowLoadingPlaceholderList()
            }

            is MoviesScreenState.Content -> {
                MoviesListView(
                        movies = state.movies,
                        selectedCategory = selectedCategory,
                        onMovieClicked = onMovieClicked
                              )
            }

            is MoviesScreenState.Empty -> {
                val isFavorites = selectedCategory == MovieCategory.FavoritesMovieCategory
                EmptyStateView(
                        imageResId = if (isFavorites) R.drawable.ic_star_filled_large
                        else R.drawable.ic_popcorn_bucket,
                        title = stringResource(
                                if (isFavorites) R.string.empty_favorites_title
                                else R.string.empty_movies_title
                                              ),
                        message = stringResource(
                                if (isFavorites) R.string.empty_favorites_message
                                else R.string.empty_movies_message
                                                )
                              )
            }

            is MoviesScreenState.Error -> {
                GenericErrorScreen(
                        state.errorMessage,
                        onTryAgainClicked = onRetry
                                  )
            }
        }
    }

    if (isCategoryPopupVisible) {
        CategoryListPopupView(
                lastSelectedCategory = selectedCategory,
                onNewCategorySelected = { newCategory ->
                    onCategorySelected(newCategory)
                    coroutineScope.launch { categorySheetState.hide() }
                            .invokeOnCompletion {
                                if (!categorySheetState.isVisible) {
                                    isCategoryPopupVisible = false
                                }
                            }
                },
                onDismiss = { isCategoryPopupVisible = false },
                state = categorySheetState
                              )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategorySelector(
        selectedCategory: MovieCategory,
        onClick: () -> Unit
                             ) {
    val categorySelectorShape = RoundedCornerShape(categorySelectorCornerShape)
    val categoryLabel = stringResource(selectedCategory.labelRes)
    // Without this the chip announces only "Popular" — no hint that it is a
    // control, or that activating it opens the category picker.
    val selectorDescription = stringResource(
            R.string.category_selector_description,
            categoryLabel
                                            )

    Row(
            modifier = Modifier
                    .padding(
                            start = categorySelectorStartPadding,
                            top = categorySelectorVerticalMargin,
                            bottom = categorySelectorVerticalMargin
                            )
                    // Keeps the chip visually small (as designed) while giving
                    // it the 48dp minimum touch target.
                    .minimumInteractiveComponentSize()
                    .clip(categorySelectorShape)
                    .background(
                            MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = CATEGORY_SELECTOR_CONTAINER_ALPHA
                                                                    )
                               )
                    .border(
                            width = categorySelectorBorderWidth,
                            color = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = CATEGORY_SELECTOR_BORDER_ALPHA
                                                                            ),
                            shape = categorySelectorShape
                           )
                    .clickable(
                            role = Role.Button,
                            onClickLabel = stringResource(R.string.category_selector_action)
                              ) { onClick() }
                    .padding(
                            horizontal = categorySelectorContentHorizontalPadding,
                            vertical = categorySelectorContentVerticalPadding
                            )
                    // Merged into one node so TalkBack reads the whole chip as
                    // a single control rather than icon / label / arrow.
                    .semantics(mergeDescendants = true) {
                        contentDescription = selectorDescription
                    },
            verticalAlignment = Alignment.CenterVertically
       ) {
        Icon(
                painter = painterResource(selectedCategory.drawableRes),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(categoryIconSize)
            )
        Spacer(modifier = Modifier.width(categoryIconEndPadding))
        Text(
                text = categoryLabel,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        Icon(
                painter = painterResource(R.drawable.ic_categories_arrow_drop_down),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = CATEGORY_SELECTOR_ARROW_ALPHA
                                                               ),
                modifier = Modifier
                        .padding(start = categoryArrowStartPadding)
                        .size(categoryArrowSize)
            )
    }
}

private val previewMovies = listOf(
        MovieModel(
                id = 1,
                title = "The Matrix",
                desc = "A computer hacker learns about the true nature of reality.",
                imageUrl = "https://image.tmdb.org/t/p/w342/dXNAPwY7VrqMAo51EKhhCJfaGb5.jpg",
                releaseDate = "March 31, 1999",
                rating = 83
                  ),
        MovieModel(
                id = 2,
                title = "Inception",
                desc = "A thief who steals corporate secrets through dream-sharing technology.",
                imageUrl = "https://image.tmdb.org/t/p/w342/xlaY2zyzMfkhk0HSC5VUwzoZPU1.jpg",
                releaseDate = "July 16, 2010",
                rating = 91
                  )
                                   )

// Hebrew content too (not just the UI chrome) so the preview exercises real
// RTL text shaping and bidi mixing with the Latin release dates/ratings.
private val previewMoviesHebrew = listOf(
        MovieModel(
                id = 1,
                title = "המטריקס",
                desc = "פורץ מחשבים מגלה את טבעה האמיתי של המציאות.",
                imageUrl = "https://image.tmdb.org/t/p/w342/dXNAPwY7VrqMAo51EKhhCJfaGb5.jpg",
                releaseDate = "31 במרץ 1999",
                rating = 83
                  ),
        MovieModel(
                id = 2,
                title = "התחלה",
                desc = "גנב הגונב סודות תאגידיים באמצעות טכנולוגיית שיתוף חלומות.",
                imageUrl = "https://image.tmdb.org/t/p/w342/xlaY2zyzMfkhk0HSC5VUwzoZPU1.jpg",
                releaseDate = "16 ביולי 2010",
                rating = 91
                  )
                                         )

@Preview(showBackground = true, locale = "iw", name = "Hebrew (RTL)")
@Composable
private fun MoviesScreenContentPreviewHebrew() {
    MoviesScreenPreviewFrame(MoviesScreenState.Content(previewMoviesHebrew))
}

@Preview(showBackground = true, locale = "iw", name = "Hebrew (RTL) - Empty favorites")
@Composable
private fun MoviesScreenEmptyFavoritesPreviewHebrew() {
    MoviesScreenPreviewFrame(
            state = MoviesScreenState.Empty,
            selectedCategory = MovieCategory.FavoritesMovieCategory
                            )
}

@Preview(showBackground = true)
@Composable
private fun MoviesScreenContentPreview() {
    MoviesScreenPreviewFrame(MoviesScreenState.Content(previewMovies))
}

@Preview(showBackground = true)
@Composable
private fun MoviesScreenLoadingPreview() {
    MoviesScreenPreviewFrame(MoviesScreenState.Loading)
}

@Preview(showBackground = true)
@Composable
private fun MoviesScreenErrorPreview() {
    MoviesScreenPreviewFrame(
            MoviesScreenState.Error("Couldn't load movies right now. Please try again later.")
                            )
}

@Preview(showBackground = true, name = "Empty - favorites")
@Composable
private fun MoviesScreenEmptyFavoritesPreview() {
    MoviesScreenPreviewFrame(
            state = MoviesScreenState.Empty,
            selectedCategory = MovieCategory.FavoritesMovieCategory
                            )
}

@Preview(showBackground = true, name = "Empty - category")
@Composable
private fun MoviesScreenEmptyPreview() {
    MoviesScreenPreviewFrame(MoviesScreenState.Empty)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoviesScreenPreviewFrame(
        state: MoviesScreenState,
        selectedCategory: MovieCategory = MovieCategory.PopularMovieCategory
                                    ) {
    MMoviesTheme {
        MoviesScreenContent(
                state = state,
                selectedCategory = selectedCategory,
                onMovieClicked = {},
                onCategorySelected = {},
                onRetry = {}
                            )
    }
}