package com.bk.mmovies.tv.ui.catalog

import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.focusGroup
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.focusable
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.bk.mmovies.core.R as CoreR
import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.domain.model.Category
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.NewsItem
import com.bk.mmovies.domain.model.TvSeriesCategory
import com.bk.mmovies.tv.R
import com.bk.mmovies.tv.ui.component.ApiKeySetupSteps
import com.bk.mmovies.tv.ui.component.TvDetailsActionButton
import com.bk.mmovies.tv.ui.component.TvOfflineView
import com.bk.mmovies.tv.ui.component.TvProfileHeader
import com.bk.mmovies.tv.ui.component.TvProviderFooter
import com.bk.mmovies.tv.ui.component.TvRow
import com.bk.mmovies.tv.ui.component.TvRowShimmer
import com.bk.mmovies.tv.ui.legal.TvAboutContent
import com.bk.mmovies.ui.component.PoweredByTmdbFooter
import com.bk.mmovies.ui.component.UserScoreView
import com.bk.mmovies.ui.theme.MMoviesTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Expanded only while D-pad focus is actually inside the rail (switching
// between Movies/TV Series/... etc) - a slim, icon-only rail the rest of the
// time gives the content pane more width, matching YouTube TV's own nav rail
// behavior.
private val navRailExpandedWidth = 260.dp
private val navRailCollapsedWidth = 96.dp
private const val NAV_RAIL_WIDTH_ANIMATION_MS = 200

// Temporarily hides the Search screen's recent-searches chips. Everything
// behind them (storage, ViewModel loading, RecentSearchesSection, clear
// action) is intact - flip to true to bring the section back.
private const val SHOW_RECENT_SEARCHES = false
private val navItemCornerShape = 12.dp
private val navIconSize = 24.dp

private val heroHeight = 380.dp
private val heroCornerShape = 20.dp

// About's content is plain text (see TvAboutContent) - unlike every other
// destination's rows/items there's nothing inside it for the D-pad's normal
// focus-search-driven scrolling to land on, so the content pane's
// LazyListState is scrolled directly by DirectionDown/Up from the focusable
// region wrapped around the text (reached with DirectionRight from the About
// rail item), the same manual-scroll pattern the details screens use once
// their own focusable "stops" run out.
private const val SCROLL_STEP_PX = 400f
// D-pad overshoot-and-correct in the nav rail would otherwise fire a
// destination load (and its network calls) per item passed over.
private const val NAV_RAIL_FOCUS_DEBOUNCE_MS = 250L
// How many TvRowShimmer placeholders to show while Movies/TV Series' first
// page of rows is still loading (rows is empty pre-load and post-load alike,
// so this is the only signal available to tell the two apart).
private const val SHIMMER_ROW_PLACEHOLDER_COUNT = 2
// How many frames to retry the nav rail's initial/return requestFocus() for
// - see that LaunchedEffect's own comment for why a single attempt can race
// (same class of issue as the details screens' equivalent retry).
private const val FOCUS_REQUEST_RETRY_FRAMES = 5

@Composable
fun CatalogTvScreen(
        viewModel: TvCatalogViewModel = hiltViewModel(),
        onViewDetailsClicked: (item: CatalogItem, category: Category?) -> Unit = { _, _ -> },
        onMovieClicked: (item: CatalogItem) -> Unit = {},
        onNewsItemClicked: (newsItem: NewsItem) -> Unit = {},
        onNavigateToAuth: () -> Unit = {},
        firstItemFocusRequester: FocusRequester? = null
                    ) {
    val selectedDestination by viewModel.selectedDestination.collectAsStateWithLifecycle()
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    val userProfileState by viewModel.userProfileState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    // The profile chip no longer jumps straight to the Auth screen - it
    // opens a small dialog first (log out -> guest mode without leaving the
    // catalog if already signed in, or a "Log in" prompt that then opens the
    // real Auth screen if currently a guest). See TvProfileDialog below.
    var showProfileDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.messageEvent.collect { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }

    // Reruns on every genuine return to this screen (see the focus
    // LaunchedEffect below) - catches a favorite toggled from a details
    // screen, which has no way to reach this ViewModel's cached rows itself.
    LaunchedEffect(Unit) { viewModel.refreshFavoriteStates(reloadFavoritesTab = true) }

    // One stable FocusRequester per nav rail destination, reusing the
    // Activity-supplied instance for Movies specifically purely so it
    // survives recomposition the same way (see TvMainActivity - it no
    // longer calls requestFocus() itself, this screen's own LaunchedEffect
    // below is solely responsible now).
    val navItemFocusRequesters = remember {
        TvCatalogDestination.entries.associateWith { destination ->
            if (destination == TvCatalogDestination.Movies && firstItemFocusRequester != null) {
                firstItemFocusRequester
            } else {
                FocusRequester()
            }
        }
    }

    // This screen isn't always the first thing on screen anymore (Splash/
    // Auth can precede it), and it stays composed-but-obscured (not
    // recreated) while a pushed screen - details, the news webview - is on
    // top, so this LaunchedEffect only actually reruns on a genuine return
    // to this screen. Re-requesting focus on the *currently selected*
    // destination's own requester every time (rather than the ViewModel's
    // old one-shot guard, or always requesting the first item) is what
    // fixes the tab reverting to Movies on return: leaving a pushed screen
    // disposes whatever had focus there, and Compose's own fallback grabs
    // the first focusable node in the tree - which used to be the Movies
    // item - before this had a chance to steer focus back to the actual
    // selected tab.
    // Single-press BACK restoration contract, scoped deliberately: the
    // pushed screen's own single BackHandler.popBackStack() call (see
    // TvNavigation) already returns here in exactly one press, and once
    // focus lands back in a row, TvRow's own row-level memory (rowId-keyed,
    // rememberSaveable) puts it back on the exact card that was focused
    // before navigating away. What this does NOT do is skip the rail and
    // land focus directly on that remembered card - it deliberately always
    // re-requests the *rail* item first (see the doc above), because the
    // "land straight back on the last content focus" behavior this session
    // used to have was exactly what caused the nav-rail-hijack bug already
    // fixed once here (a disposed focus target silently falling back to and
    // triggering the first focusable node). Full YouTube-TV-style deep
    // restoration would need a different, more deliberate mechanism than
    // Compose's default focus fallback to be safe to reintroduce.
    LaunchedEffect(Unit) {
        val target = navItemFocusRequesters[selectedDestination] ?: return@LaunchedEffect
        // Single attempt can race the nav rail item's own focusRequester()
        // modifier attaching (same class of issue as the details screens'
        // initial-focus grab - see MovieDetailsTvScreen's LaunchedEffect for
        // the full explanation), especially on this screen's very first-ever
        // appearance right after Splash/Auth. Retrying across a few frames
        // is a harmless no-op once focus is already there.
        repeat(FOCUS_REQUEST_RETRY_FRAMES) {
            runCatching { target.requestFocus() }
            withFrameNanos {}
        }
    }

    CatalogTvScreenContent(
            selectedDestination = selectedDestination,
            screenState = screenState,
            userProfileState = userProfileState,
            canToggleFavorite = viewModel.canToggleFavorite,
            onDestinationSelected = viewModel::onDestinationSelected,
            onViewDetailsClicked = onViewDetailsClicked,
            onFeaturedFavoriteClicked = viewModel::onFeaturedFavoriteClicked,
            onMovieClicked = onMovieClicked,
            onNewsItemClicked = onNewsItemClicked,
            onProfileClicked = { showProfileDialog = true },
            onItemFocused = viewModel::onItemFocused,
            onDialogDismissed = viewModel::onDialogDismissed,
            onPopupClosedPrune = viewModel::onPopupClosedPrune,
            onSearchQueryChanged = viewModel::onSearchQueryChanged,
            // Opens the preview popup like the Movies/TV Series rows; its
            // "View details" button does the navigation.
            onSearchItemPressed = { item ->
                viewModel.onSearchResultClicked(item)
                viewModel.onItemFocused(item)
            },
            onRecentSearchClicked = viewModel::onRecentSearchClicked,
            onClearRecentSearchesClicked = viewModel::onClearRecentSearchesClicked,
            onRowEndReached = viewModel::onRowEndReached,
            onFavoritesRowEndReached = viewModel::onFavoritesRowEndReached,
            onNewsRowEndReached = viewModel::onNewsRowEndReached,
            onRecommendationsRetryClicked = viewModel::onRecommendationsRetryClicked,
            onOfflineRetryClicked = viewModel::onOfflineRetryClicked,
            onGeminiApiKeySaved = viewModel::onGeminiApiKeySaved,
            onNewsApiKeySaved = viewModel::onNewsApiKeySaved,
            navItemFocusRequesters = navItemFocusRequesters
                           )

    if (showProfileDialog) {
        TvProfileDialog(
                isGuest = userProfileState.isGuest,
                name = userProfileState.name,
                onDismissRequest = { showProfileDialog = false },
                onLoginClicked = {
                    showProfileDialog = false
                    onNavigateToAuth()
                },
                onLogoutClicked = {
                    showProfileDialog = false
                    viewModel.onLogoutClicked()
                }
                        )
    }
}

@Composable
private fun TvProfileDialog(
        isGuest: Boolean,
        name: String,
        onDismissRequest: () -> Unit,
        onLoginClicked: () -> Unit,
        onLogoutClicked: () -> Unit
                            ) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
                modifier = Modifier
                        .width(420.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(32.dp)
              ) {
            Text(
                    text = if (isGuest) stringResource(R.string.guest_label) else name,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            Text(
                    text = stringResource(
                            if (isGuest) R.string.profile_dialog_guest_message else R.string.profile_dialog_logout_message
                                         ),
                    modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyLarge
                )
            Button(
                    onClick = if (isGuest) onLoginClicked else onLogoutClicked,
                    modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                  ) {
                Text(
                        text = stringResource(if (isGuest) R.string.login_action else R.string.logout_action),
                        fontWeight = FontWeight.Bold
                    )
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                    onClick = onDismissRequest,
                    modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                          ) {
                Text(text = stringResource(R.string.cancel_action))
            }
        }
    }
}

@Composable
fun CatalogTvScreenContent(
        selectedDestination: TvCatalogDestination,
        screenState: TvCatalogScreenState,
        userProfileState: TvUserProfileUiState = TvUserProfileUiState(),
        canToggleFavorite: Boolean = false,
        onDestinationSelected: (TvCatalogDestination) -> Unit,
        onViewDetailsClicked: (item: CatalogItem, category: Category?) -> Unit,
        onFeaturedFavoriteClicked: () -> Unit,
        onMovieClicked: (item: CatalogItem) -> Unit,
        onNewsItemClicked: (newsItem: NewsItem) -> Unit = {},
        onProfileClicked: () -> Unit = {},
        onItemFocused: (item: CatalogItem, category: Category?) -> Unit = { _, _ -> },
        onDialogDismissed: () -> Unit = {},
        onPopupClosedPrune: () -> Unit = {},
        onSearchQueryChanged: (String) -> Unit = {},
        onSearchItemPressed: (item: CatalogItem) -> Unit = {},
        onRecentSearchClicked: (query: String) -> Unit = {},
        onClearRecentSearchesClicked: () -> Unit = {},
        onRowEndReached: (category: Category) -> Unit = {},
        onFavoritesRowEndReached: (category: Category) -> Unit = {},
        onNewsRowEndReached: () -> Unit = {},
        onRecommendationsRetryClicked: () -> Unit = {},
        onOfflineRetryClicked: () -> Unit = {},
        onGeminiApiKeySaved: (apiKey: String) -> Unit = {},
        onNewsApiKeySaved: (apiKey: String) -> Unit = {},
        navItemFocusRequesters: Map<TvCatalogDestination, FocusRequester> = emptyMap()
                                   ) {
    val focusedItem = when (screenState) {
        is TvCatalogScreenState.Movies          -> screenState.focusedItem
        is TvCatalogScreenState.TvSeries        -> screenState.focusedItem
        is TvCatalogScreenState.Favorites       -> screenState.focusedItem
        is TvCatalogScreenState.Search          -> screenState.focusedItem
        is TvCatalogScreenState.Recommendations -> screenState.focusedItem
        else                                    -> null
    }
    val focusedCategory = when (screenState) {
        is TvCatalogScreenState.Movies   -> screenState.focusedCategory
        is TvCatalogScreenState.TvSeries -> screenState.focusedCategory
        else                             -> null
    }
    val focusedCategoryIsUpcoming =
            focusedCategory == MovieCategory.UpcomingMovieCategory || focusedCategory == TvSeriesCategory.UpcomingTvSeriesCategory

    // The nav rail's own topmost item is a much larger, more "attractive"
    // focus target than this small top-right button, so plain spatial
    // DirectionUp search from the content below always lands back on the
    // rail instead - confirmed on-device via the accessibility tree (focus
    // never left the rail no matter which content row/column DirectionUp
    // was pressed from). Redirecting explicitly, the same reasoning as the
    // details screens' own stops list, is what actually gets a D-pad here.
    val profileFocusRequester = remember { FocusRequester() }
    // The profile header is reachable ONLY from the Movies rail item (D-pad Up
    // there): it can take focus while that item, or the header itself, holds
    // it. Everywhere else spatial focus search skips it, so Up from the first
    // row or Right from a row's end can't land on it.
    var moviesRailItemFocused by remember { mutableStateOf(false) }
    var profileFocused by remember { mutableStateOf(false) }
    val contentListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    // About's text is otherwise unfocusable - D-pad Right from the About rail
    // item lands on this so the whole Terms & Privacy text can be scrolled.
    val aboutContentFocusRequester = remember { FocusRequester() }
    // Deliberately NOT read with `by` here (which would make this function's
    // own composition scope subscribe to it) - kept as the raw State<Boolean>
    // object and handed to a leaf composable that reads .value itself below.
    // Every rail focus change was otherwise recomposing this entire function
    // - LazyColumn, every visible row, every poster AsyncImage in it - purely
    // because CatalogTvScreenContent's own body read the unwrapped boolean to
    // size the Spacer a few lines down. Writing .value in the lambda passed
    // to CatalogNavRail below is a write, not a read, so it doesn't create
    // that subscription either.
    val isRailFocused = remember { mutableStateOf(false) }

    Box(
            modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
       ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Reserves the collapsed/expanded rail's width instantly (no
            // animation) rather than sharing a Row weight with the rail
            // itself - animating a fixed-width sibling's width still forces
            // every OTHER weighted child to remeasure on every single frame
            // of that animation, and this content pane is expensive to
            // remeasure (a full row of poster images). The rail below draws
            // its own smooth width animation as an overlay on top instead,
            // so only its own (much cheaper) small icon/label column repaints
            // per frame - this Spacer just needs to land on the right final
            // width once the transition settles.
            RailReservedSpaceSpacer(isRailFocused)

            // A Column (not the LazyColumn itself) owns the weight(1f)/fillMaxHeight
            // here so the provider footer below can sit fixed at the bottom of the
            // content pane, outside the scrolling area, instead of scrolling away
            // with the rows as it did when it was the LazyColumn's own last item.
            Column(
                    modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                  ) {
            TvProfileHeader(
                    name = userProfileState.name,
                    imageUrl = userProfileState.imageUrl,
                    isGuest = userProfileState.isGuest,
                    modifier = Modifier.padding(start = 40.dp, top = 24.dp, end = 40.dp),
                    focusRequester = profileFocusRequester,
                    canBeFocused = moviesRailItemFocused || profileFocused,
                    onFocusChanged = { profileFocused = it },
                    // Back the way it came in: the profile is only reachable from
                    // the Movies rail item, so Down/Left return there (expanding
                    // the rail) rather than dropping into a nearby content card.
                    onNavigateBack = {
                        navItemFocusRequesters[TvCatalogDestination.Movies]?.let { runCatching { it.requestFocus() } }
                    },
                    onClick = onProfileClicked
                            )

            LazyColumn(
                    state = contentListState,
                    modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                    contentPadding = PaddingValues(start = 40.dp, top = 20.dp, end = 40.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(36.dp)
                      ) {
                when (screenState) {
                    // onItemPressed is deliberately a no-op here: pressing an item
                    // only opens the preview dialog (via onItemFocused) - actual
                    // navigation happens from the dialog's "View details" button,
                    // which knows which item/category is currently shown.
                    is TvCatalogScreenState.Movies    -> if (screenState.rows.isEmpty()) {
                        items(SHIMMER_ROW_PLACEHOLDER_COUNT) { TvRowShimmer() }
                    } else {
                        items(screenState.rows) { row ->
                            CategoryRow(
                                    category = row.category,
                                    items = row.items,
                                    onItemPressed = {},
                                    onItemFocused = { item -> onItemFocused(item, row.category) },
                                    onEndReached = { onRowEndReached(row.category) },
                                    navRailFocusRequester = navItemFocusRequesters[selectedDestination]
                                       )
                        }
                    }
                    is TvCatalogScreenState.TvSeries  -> if (screenState.rows.isEmpty()) {
                        items(SHIMMER_ROW_PLACEHOLDER_COUNT) { TvRowShimmer() }
                    } else {
                        items(screenState.rows) { row ->
                            CategoryRow(
                                    category = row.category,
                                    items = row.items,
                                    onItemPressed = {},
                                    onItemFocused = { item -> onItemFocused(item, row.category) },
                                    onEndReached = { onRowEndReached(row.category) },
                                    navRailFocusRequester = navItemFocusRequesters[selectedDestination]
                                       )
                        }
                    }
                    is TvCatalogScreenState.Favorites -> if (screenState.loginRequired) {
                        item {
                            Text(
                                    text = stringResource(R.string.favorites_login_required),
                                    modifier = Modifier.padding(top = 8.dp),
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                        }
                    } else if (screenState.isLoading) {
                        // One shimmer per favorites row (movies, then series).
                        items(SHIMMER_ROW_PLACEHOLDER_COUNT) { TvRowShimmer() }
                    } else {
                        // A press opens the preview popup (same as Movies/TV
                        // Series); its "View details" button navigates.
                        items(screenState.rows) { row ->
                            CategoryRow(
                                    category = row.category,
                                    items = row.items,
                                    onItemPressed = { item -> onItemFocused(item, null) },
                                    onEndReached = { onFavoritesRowEndReached(row.category) },
                                    emptyMessage = stringResource(
                                            if (row.category is TvSeriesCategory) R.string.favorites_empty_tv_series
                                            else R.string.favorites_empty_movies
                                                                 ),
                                    navRailFocusRequester = navItemFocusRequesters[selectedDestination]
                                       )
                        }
                    }
                    is TvCatalogScreenState.News      -> item {
                        if (screenState.keySetupRequired) {
                            NewsKeySetup(
                                    onApiKeySaved = { apiKey ->
                                        // Saving swaps this whole composable out for a
                                        // loading/loaded state, so the Save button's own
                                        // focus node disappears mid-click - without
                                        // explicitly steering focus somewhere still on
                                        // screen first, Compose's fallback grabs the
                                        // first focusable node in the tree (the Movies
                                        // nav item), and that rail auto-navigates on
                                        // focus (see CatalogNavRailItem), silently
                                        // hijacking the destination and cancelling the
                                        // very load this button just kicked off.
                                        navItemFocusRequesters[selectedDestination]?.let { runCatching { it.requestFocus() } }
                                        onNewsApiKeySaved(apiKey)
                                    }
                                        )
                        } else if (screenState.isLoading) {
                            TvRowShimmer()
                        } else {
                            TvNewsRow(
                                    title = stringResource(TvCatalogDestination.News.labelResId),
                                    items = screenState.items,
                                    onNewsItemClicked = onNewsItemClicked,
                                    onEndReached = onNewsRowEndReached,
                                    navRailFocusRequester = navItemFocusRequesters[TvCatalogDestination.News]
                                     )
                        }
                    }
                    is TvCatalogScreenState.Search    -> item {
                        Column {
                            Text(
                                    text = stringResource(TvCatalogDestination.Search.labelResId),
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.headlineSmall
                                )
                            Spacer(modifier = Modifier.height(16.dp))
                            val searchFieldFocusRequester = remember { FocusRequester() }
                            SearchField(
                                    query = screenState.query,
                                    onQueryChanged = onSearchQueryChanged,
                                    focusRequester = searchFieldFocusRequester,
                                    navRailFocusRequester = navItemFocusRequesters[selectedDestination]
                                       )
                            Spacer(modifier = Modifier.height(24.dp))
                            if (screenState.query.isBlank()) {
                                if (SHOW_RECENT_SEARCHES && screenState.recentSearches.isNotEmpty()) {
                                    RecentSearchesSection(
                                            recentSearches = screenState.recentSearches,
                                            onRecentSearchClicked = { query ->
                                                // Picking a chip fills the query, which swaps
                                                // this whole section out for the results row -
                                                // the focused chip's node disappears mid-click,
                                                // and Compose's fallback then grabs the first
                                                // focusable node (the Movies nav item, which
                                                // auto-navigates on focus and hijacks the
                                                // destination). Park focus on the search field
                                                // first, same as the News/Recommendations
                                                // key-save wrappers above.
                                                runCatching { searchFieldFocusRequester.requestFocus() }
                                                onRecentSearchClicked(query)
                                            },
                                            onClearRecentSearchesClicked = onClearRecentSearchesClicked
                                                          )
                                }
                            } else if (screenState.isSearching) {
                                TvRowShimmer()
                            } else {
                                TvRow(
                                        rowId = "search",
                                        list = screenState.items,
                                        onItemPressed = onSearchItemPressed,
                                        navRailFocusRequester = navItemFocusRequesters[selectedDestination],
                                        focusFirstOnEntry = true
                                     )
                            }
                        }
                    }
                    is TvCatalogScreenState.Recommendations -> item {
                        RecommendationsContent(
                                screenState = screenState,
                                // See the News branch's identical wrapper above for
                                // why this focus redirect is needed.
                                onApiKeySaved = { apiKey ->
                                    navItemFocusRequesters[selectedDestination]?.let { runCatching { it.requestFocus() } }
                                    onGeminiApiKeySaved(apiKey)
                                },
                                onItemPressed = { item -> onItemFocused(item, null) },
                                // Retry swaps this error state out for the loading
                                // shimmer, disposing the button that holds focus -
                                // steer focus back to the rail item first, same as
                                // the key-save wrapper above.
                                onRetryClicked = {
                                    navItemFocusRequesters[selectedDestination]?.let { runCatching { it.requestFocus() } }
                                    onRecommendationsRetryClicked()
                                },
                                navRailFocusRequester = navItemFocusRequesters[selectedDestination]
                                               )
                    }
                    is TvCatalogScreenState.Offline -> item {
                        TvOfflineView(
                                // Retry swaps this view for the loading shimmer,
                                // disposing the button that holds focus - steer
                                // focus back to the rail item first (same reason
                                // as the other retry/save wrappers here).
                                onRetryClicked = {
                                    navItemFocusRequesters[selectedDestination]?.let { runCatching { it.requestFocus() } }
                                    onOfflineRetryClicked()
                                },
                                navRailFocusRequester = navItemFocusRequesters[selectedDestination]
                                     )
                    }
                    is TvCatalogScreenState.About -> item {
                        // A focusable region around the text: Down/Up scroll the
                        // pane (nothing inside is focusable to scroll to), Left
                        // hands focus back to the About rail item, which expands
                        // the rail. Any other key (Up at the very top) falls
                        // through to normal focus movement.
                        Box(
                                modifier = Modifier
                                        .focusRequester(aboutContentFocusRequester)
                                        .onPreviewKeyEvent { keyEvent ->
                                            if (keyEvent.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                                            when {
                                                keyEvent.key == Key.DirectionDown -> {
                                                    coroutineScope.launch { contentListState.animateScrollBy(SCROLL_STEP_PX) }
                                                    true
                                                }
                                                keyEvent.key == Key.DirectionUp && contentListState.canScrollBackward -> {
                                                    coroutineScope.launch { contentListState.animateScrollBy(-SCROLL_STEP_PX) }
                                                    true
                                                }
                                                keyEvent.key == Key.DirectionLeft -> {
                                                    navItemFocusRequesters[TvCatalogDestination.About]?.let { runCatching { it.requestFocus() } }
                                                    true
                                                }
                                                else                              -> false
                                            }
                                        }
                                        .focusable()
                           ) {
                            TvAboutContent()
                        }
                    }
                }
            }

            // Fixed to the bottom of the content pane (outside the LazyColumn
            // above), so it never scrolls with the rows. Which provider it
            // attributes depends on which destination is showing: News uses
            // Guardian content (see GuardianNewsRepositoryImpl), Recommendations
            // uses Gemini, everything else is TMDB-backed.
            when (selectedDestination) {
                TvCatalogDestination.News            -> TvProviderFooter(
                        label = stringResource(R.string.powered_by_guardian),
                        modifier = Modifier.padding(vertical = 10.dp)
                                                                        )
                TvCatalogDestination.Recommendations -> TvProviderFooter(
                        label = stringResource(R.string.powered_by_gemini),
                        modifier = Modifier.padding(vertical = 10.dp)
                                                                        )
                else                                 -> PoweredByTmdbFooter(
                        modifier = Modifier.padding(vertical = 10.dp)
                                                                            )
            }
        }
    }

        // Drawn on top of (not sharing a Row weight with) the content pane -
        // see the reserved-space Spacer's comment above for why. Its own
        // background fully occludes content behind it while briefly
        // overlapping during the expand/collapse transition.
        CatalogNavRail(
                selectedDestination = selectedDestination,
                onDestinationSelected = onDestinationSelected,
                navItemFocusRequesters = navItemFocusRequesters,
                profileFocusRequester = profileFocusRequester,
                onMoviesItemFocusChanged = { moviesRailItemFocused = it },
                aboutContentFocusRequester = aboutContentFocusRequester,
                isFocusedState = isRailFocused,
                onFocusChanged = { isRailFocused.value = it }
                      )
    }

    if (focusedItem != null) {
        val viewDetailsFocusRequester = remember { FocusRequester() }
        // A Dialog is a separate window - nothing focuses anything inside it
        // on its own, so without this the D-pad does nothing at all the
        // moment this preview opens. Retried across a few frames for the
        // same reason as the details screens' own initial-focus grab: this
        // can race the Button's focusRequester() modifier attaching on the
        // dialog's very first composition.
        LaunchedEffect(focusedItem) {
            repeat(FOCUS_REQUEST_RETRY_FRAMES) {
                runCatching { viewDetailsFocusRequester.requestFocus() }
                withFrameNanos {}
            }
        }
        // Closing the popup also drops cards whose favorite state it flipped
        // (favorited on Recommendations, un-favorited on Favorites) - but only
        // after focus has left the card (see TvCatalogViewModel.
        // onPopupClosedPrune), so the removal can't trigger Compose's focus
        // fallback onto the Movies rail item.
        val closeDialog: () -> Unit = {
            val prune = when (screenState) {
                is TvCatalogScreenState.Recommendations -> screenState.items.any { it.isFavorite }
                is TvCatalogScreenState.Favorites       -> screenState.rows.any { row -> row.items.any { !it.isFavorite } }
                else                                    -> false
            }
            onDialogDismissed()
            if (prune) {
                coroutineScope.launch {
                    withFrameNanos {}
                    navItemFocusRequesters[selectedDestination]?.let { runCatching { it.requestFocus() } }
                    withFrameNanos {}
                    onPopupClosedPrune()
                }
            }
        }
        Dialog(
                onDismissRequest = closeDialog,
                properties = DialogProperties(usePlatformDefaultWidth = false)
              ) {
            Box(
                    modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .padding(24.dp)
               ) {
                CatalogHeroBanner(
                        item = focusedItem,
                        // Dismiss the preview dialog itself before navigating -
                        // otherwise it stays composed underneath the details
                        // screen and reappears the moment the user comes back.
                        onViewDetailsClicked = {
                            closeDialog()
                            onViewDetailsClicked(focusedItem, focusedCategory)
                        },
                        onFavoriteClicked = onFeaturedFavoriteClicked,
                        showFavoriteButton = canToggleFavorite,
                        // Items resolved through search (Search results, AI
                        // recommendations) carry no rating - a "0%" badge would
                        // be misleading, so only show it when there is one.
                        showUserScore = !focusedCategoryIsUpcoming && focusedItem.rating > 0,
                        viewDetailsFocusRequester = viewDetailsFocusRequester
                                 )
            }
        }
    }
}

// A separate composable purely so the State<Boolean> read (.value) happens in
// its own small recomposition scope - see the call site's comment in
// CatalogTvScreenContent for why this exists at all.
@Composable
private fun RailReservedSpaceSpacer(isRailFocused: State<Boolean>) {
    val focused by isRailFocused
    Spacer(modifier = Modifier.width(if (focused) navRailExpandedWidth else navRailCollapsedWidth))
}

@Composable
private fun CatalogNavRail(
        selectedDestination: TvCatalogDestination,
        onDestinationSelected: (TvCatalogDestination) -> Unit,
        navItemFocusRequesters: Map<TvCatalogDestination, FocusRequester> = emptyMap(),
        profileFocusRequester: FocusRequester? = null,
        onMoviesItemFocusChanged: (isFocused: Boolean) -> Unit = {},
        aboutContentFocusRequester: FocusRequester? = null,
        // Whether D-pad focus is currently anywhere inside this rail -
        // hoisted (not local state) so the content pane's reserved-space
        // Spacer in CatalogTvScreenContent can react to the same signal
        // instantly, instead of through this composable's own animated
        // width. Passed as the raw State<Boolean> (read via `by` below, in
        // this composable's own scope) rather than an unwrapped Boolean -
        // see CatalogTvScreenContent's comment on its own copy of this state
        // for why that distinction actually matters here.
        // Used here to suppress the "current destination" dim highlight on
        // the item focus just left - without this, moving focus from Movies
        // to TV Series left Movies showing its dim highlight until
        // onDestinationSelected's debounce committed the new
        // selectedDestination, ~250ms later, reading as if Movies were still
        // focused. hasFocus (via focusGroup so it aggregates children)
        // updates the instant Compose focus moves, so the previous item's
        // highlight now clears the same frame focus leaves it, while the dim
        // highlight still appears once focus leaves the rail entirely (e.g.
        // into content), correctly indicating the active section.
        isFocusedState: State<Boolean> = remember { mutableStateOf(false) },
        onFocusChanged: (Boolean) -> Unit = {}
                           ) {
    val isFocused by isFocusedState
    // Animation disabled for now (perf investigation - see chat history for
    // why: ruled out as the actual jank source, but leaving this off until
    // that's confirmed on a real, unmirrored test). Kept commented rather
    // than removed so it's a one-line revert once confirmed fine.
    // val railWidth by animateDpAsState(
    //         targetValue = if (isFocused) navRailExpandedWidth else navRailCollapsedWidth,
    //         animationSpec = tween(NAV_RAIL_WIDTH_ANIMATION_MS),
    //         label = "navRailWidth"
    //                                   )
    val railWidth = if (isFocused) navRailExpandedWidth else navRailCollapsedWidth

    Column(
            modifier = Modifier
                    .width(railWidth)
                    .fillMaxHeight()
                    // Solid backing so this fully occludes the content pane
                    // behind it while briefly overlapping during the
                    // expand/collapse transition (see the Box overlay's
                    // comment in CatalogTvScreenContent for why this is an
                    // overlay rather than a Row sibling).
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 20.dp, vertical = 32.dp)
                    .focusGroup()
                    .onFocusChanged { onFocusChanged(it.hasFocus) },
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
        TvCatalogDestination.entries.forEachIndexed { index, destination ->
            val focusRequester = navItemFocusRequesters[destination]
            var itemModifier = if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier
            // Only the topmost item needs this: it's the one a DirectionUp
            // from anywhere else in the rail lands on first (see
            // profileFocusRequester's doc above).
            if (index == 0) {
                // Tracks whether the Movies item itself is focused - the only
                // state in which the profile header may take focus.
                itemModifier = itemModifier.onFocusChanged { onMoviesItemFocusChanged(it.isFocused) }
            }
            if (index == 0 && profileFocusRequester != null) {
                itemModifier = itemModifier.onPreviewKeyEvent { keyEvent ->
                    if (keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.DirectionUp) {
                        runCatching { profileFocusRequester.requestFocus() }
                        true
                    } else {
                        false
                    }
                }
            }
            // About's text is scrolled from inside its own focusable region
            // (see the About branch in CatalogTvScreenContent), so this rail
            // item only needs to hand focus over on DirectionRight - Up/Down
            // are plain rail navigation like every other item (Up goes to
            // Recommendations).
            if (destination == TvCatalogDestination.About && aboutContentFocusRequester != null) {
                itemModifier = itemModifier.onPreviewKeyEvent { keyEvent ->
                    if (keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.DirectionRight) {
                        runCatching { aboutContentFocusRequester.requestFocus() }
                        true
                    } else {
                        false
                    }
                }
            }
            CatalogNavRailItem(
                    label = stringResource(destination.labelResId),
                    icon = destination.icon,
                    isSelected = destination == selectedDestination && !isFocused,
                    isExpanded = isFocused,
                    onClick = { onDestinationSelected(destination) },
                    modifier = itemModifier
                              )
        }
    }
}

@Composable
private fun CatalogNavRailItem(
        label: String,
        icon: TvNavIcon,
        isSelected: Boolean,
        isExpanded: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
                               ) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    // D-pad landing on a destination loads it after a short pause - no
    // separate "press select" step, matching how the rest of this nav rail
    // is meant to be driven purely by focus. The delay is debounced by the
    // LaunchedEffect key itself: focus moving off this item before it
    // elapses cancels the effect, so scrolling past several rail items
    // (including overshoot-and-correct) doesn't fire a load per item.
    LaunchedEffect(isFocused) {
        if (isFocused) {
            delay(NAV_RAIL_FOCUS_DEBOUNCE_MS)
            onClick()
        }
    }

    val backgroundColor = when {
        isFocused    -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.18f)
        isSelected   -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f)
        else         -> Color.Transparent
    }
    val contentColor = if (isSelected || isFocused) {
        MaterialTheme.colorScheme.onBackground
    } else {
        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
    }

    Row(
            modifier = modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(navItemCornerShape))
                    .background(backgroundColor)
                    .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onClick
                              )
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            // Centers the icon in the slim collapsed rail instead of leaving
            // it pinned to the start with a lopsided gap on the right where
            // the label used to be.
            horizontalArrangement = if (isExpanded) Arrangement.Start else Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
       ) {
        when (icon) {
            is TvNavIcon.Vector   -> Icon(
                    imageVector = icon.imageVector,
                    contentDescription = if (isExpanded) null else label,
                    tint = contentColor,
                    modifier = Modifier.size(navIconSize)
                                         )
            is TvNavIcon.Drawable -> Icon(
                    painter = painterResource(icon.resId),
                    contentDescription = if (isExpanded) null else label,
                    tint = contentColor,
                    modifier = Modifier.size(navIconSize)
                                         )
        }
        // Animation disabled for now (perf investigation - see chat history for
        // why: ruled out as the actual jank source, but leaving this off until
        // that's confirmed on a real, unmirrored test). Kept commented rather
        // than removed so it's a one-line revert once confirmed fine.
        // AnimatedVisibility(
        //         visible = isExpanded,
        //         enter = fadeIn(tween(NAV_RAIL_WIDTH_ANIMATION_MS)),
        //         exit = fadeOut(tween(NAV_RAIL_WIDTH_ANIMATION_MS / 2))
        //                    ) {
        if (isExpanded) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                        text = label,
                        color = contentColor,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1
                    )
            }
        }
    }
}

// For rows with no real Category behind them (News, Search) - CategoryRow
@Composable
private fun SearchField(
        query: String,
        onQueryChanged: (String) -> Unit,
        focusRequester: FocusRequester = remember { FocusRequester() },
        navRailFocusRequester: FocusRequester? = null
                        ) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val voiceSearchUnavailableMessage = stringResource(R.string.voice_search_unavailable)
    val voiceSearchPrompt = stringResource(R.string.voice_search_prompt)

    // No custom recognition UI - ACTION_RECOGNIZE_SPEECH hands off to
    // whatever voice input service is installed (the same one Leanback's
    // SearchOrb defers to), matching the Tier-2 Google TV quality guideline
    // that calls for a mic affordance next to a search field whose keyboard
    // doesn't natively support voice input.
    val voiceSearchLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val spokenText = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
        if (spokenText != null) {
            onQueryChanged(spokenText)
        }
    }

    Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
       ) {
        OutlinedTextField(
                value = query,
                onValueChange = onQueryChanged,
                modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester)
                        // A single-line BasicTextField swallows DirectionDown as
                        // a cursor-movement key before it ever reaches Compose's
                        // 2D focus search - on a D-pad remote that makes the
                        // search results row permanently unreachable once the
                        // field has focus, so intercept it here and move focus
                        // down manually.
                        // Likewise DirectionLeft is swallowed as a cursor move even
                        // with nothing to move over, trapping focus in the field -
                        // while it's empty, send it to the Search rail item.
                        .onPreviewKeyEvent { keyEvent ->
                            if (keyEvent.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                            when {
                                keyEvent.key == Key.DirectionDown -> {
                                    focusManager.moveFocus(FocusDirection.Down)
                                    true
                                }
                                keyEvent.key == Key.DirectionLeft && query.isEmpty() && navRailFocusRequester != null -> {
                                    runCatching { navRailFocusRequester.requestFocus() }
                                    true
                                }
                                else                              -> false
                            }
                        },
                placeholder = { Text(text = stringResource(R.string.search_placeholder)) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                        cursorColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                                          )
                 )
        Spacer(modifier = Modifier.width(12.dp))
        IconButton(onClick = {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PROMPT, voiceSearchPrompt)
            }
            try {
                voiceSearchLauncher.launch(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, voiceSearchUnavailableMessage, Toast.LENGTH_SHORT).show()
            }
        }) {
            Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = stringResource(R.string.voice_search_action),
                    tint = MaterialTheme.colorScheme.onSurface
                )
        }
    }
}

// Shown in place of the (otherwise empty) results row while the query is
// blank - mirrors :app's recent-searches list, minus its per-item remove
// affordance (see TvCatalogViewModel.onSearchResultClicked's doc for why).
@Composable
private fun RecentSearchesSection(
        recentSearches: List<String>,
        onRecentSearchClicked: (query: String) -> Unit,
        onClearRecentSearchesClicked: () -> Unit
                                  ) {
    Column {
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
           ) {
            Text(
                    text = stringResource(R.string.recent_searches_label),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelLarge
                )
            Text(
                    text = stringResource(R.string.clear_recent_searches_action),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelLarge,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable(onClick = onClearRecentSearchesClicked)
                )
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(recentSearches, key = { it }) { query ->
                RecentSearchChip(query = query, onClick = { onRecentSearchClicked(query) })
            }
        }
    }
}

@Composable
private fun RecentSearchChip(query: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Box(
            modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (isFocused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onClick
                              )
                    .padding(horizontal = 20.dp, vertical = 10.dp)
       ) {
        Text(
                text = query,
                color = if (isFocused) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
    }
}

// For rows with no real Category behind them (Search) - CategoryRow needs a
// real Category for its icon/label, which this destination doesn't have.
@Composable
private fun SimpleCatalogRow(
        rowId: String,
        title: String,
        items: List<CatalogItem>,
        onItemPressed: (item: CatalogItem) -> Unit,
        leadingIcon: (@Composable () -> Unit)? = null,
        navRailFocusRequester: FocusRequester? = null
                             ) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            leadingIcon?.let {
                it()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TvRow(
                rowId = rowId,
                list = items,
                onItemPressed = onItemPressed,
                navRailFocusRequester = navRailFocusRequester
             )
    }
}

@Composable
private fun CatalogHeroBanner(
        item: CatalogItem?,
        onViewDetailsClicked: () -> Unit,
        onFavoriteClicked: () -> Unit,
        showFavoriteButton: Boolean = true,
        showUserScore: Boolean = true,
        viewDetailsFocusRequester: FocusRequester? = null
                              ) {
    Box(
            modifier = Modifier
                    .fillMaxWidth()
                    .height(heroHeight)
                    .clip(RoundedCornerShape(heroCornerShape))
                    .background(
                            Brush.linearGradient(
                                    colors = listOf(
                                            Color(0xFF1B2A3D),
                                            Color(0xFF2E4258),
                                            Color(0xFF141B26)
                                                   )
                                                 )
                               )
       ) {
        // Static backdrop only - no trailer autoplay in this preview. A
        // plain gradient shows instead before the image loads.
        if (item != null) {
            AsyncImage(
                    model = item.backdropUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(heroCornerShape))
                      )
            Box(
                    modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(heroCornerShape))
                            .background(
                                    Brush.horizontalGradient(
                                            colors = listOf(
                                                    Color.Black.copy(alpha = 0.85f),
                                                    Color.Black.copy(alpha = 0.4f)
                                                           )
                                                            )
                                       )
               )
        }

        if (item != null) {
            Column(
                    modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(32.dp)
                            .fillMaxWidth(0.6f)
                  ) {
                Text(
                        text = item.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.displaySmall
                    )

                Row(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                   ) {
                    if (showUserScore) {
                        UserScoreView(score = item.rating, size = 32.dp)
                    }
                    if (item.releaseDate.isNotBlank()) {
                        Text(
                                text = item.releaseDate,
                                modifier = Modifier.padding(start = if (showUserScore) 12.dp else 0.dp),
                                color = Color.White.copy(alpha = 0.75f),
                                style = MaterialTheme.typography.titleMedium
                            )
                    }
                }

                Row(
                        modifier = Modifier.padding(top = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                   ) {
                    Button(
                            onClick = onViewDetailsClicked,
                            colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Color.Black
                                                                 ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = if (viewDetailsFocusRequester != null) {
                                Modifier.focusRequester(viewDetailsFocusRequester)
                            } else {
                                Modifier
                            }
                          ) {
                        Text(
                                text = stringResource(R.string.view_details_button),
                                fontWeight = FontWeight.Bold
                            )
                    }

                    if (showFavoriteButton) {
                        OutlinedButton(
                                onClick = onFavoriteClicked,
                                colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color.White
                                                                            ),
                                shape = RoundedCornerShape(10.dp)
                                      ) {
                            Icon(
                                    imageVector = if (item.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                    text = stringResource(
                                            if (item.isFavorite) R.string.remove_favorite_button else R.string.favorite_button
                                                          )
                                )
                        }
                    }
                }
            }
        }
    }
}

// loginRequired/keySetupRequired mirror Favorites' loginRequired: real
// suggestions need a logged-in session's favorites/recent searches plus a
// usable Gemini key (see TvCatalogViewModel.loadRecommendations). Saving a
// key from the form immediately retries loading (onGeminiApiKeySaved), so a
// successful save replaces this composable's own output with the loaded
// row rather than needing a manual re-select of the tab.
@Composable
private fun RecommendationsContent(
        screenState: TvCatalogScreenState.Recommendations,
        onApiKeySaved: (apiKey: String) -> Unit,
        onItemPressed: (item: CatalogItem) -> Unit,
        onRetryClicked: () -> Unit,
        navRailFocusRequester: FocusRequester?
                                   ) {
    when {
        screenState.loginRequired    -> RecommendationsMessage(stringResource(R.string.ai_recommendations_login_required))
        screenState.keySetupRequired -> RecommendationsKeySetup(onApiKeySaved)
        screenState.isLoading        -> TvRowShimmer(modifier = Modifier.padding(top = 24.dp))
        screenState.items.isNotEmpty() -> SimpleCatalogRow(
                rowId = "recommendations",
                title = stringResource(R.string.ai_recommendations_title),
                items = screenState.items,
                onItemPressed = onItemPressed,
                leadingIcon = { AiIcon() },
                navRailFocusRequester = navRailFocusRequester
                                                           )
        // Gemini's own failure (usage limit, overloaded, ...): say what
        // happened and let the user retry. Nothing retries automatically;
        // returning to the tab after visiting another also tries again.
        screenState.errorMessage != null -> RecommendationsMessage(
                message = screenState.errorMessage,
                onRetryClicked = onRetryClicked,
                navRailFocusRequester = navRailFocusRequester
                                                                  )
        else                          -> RecommendationsMessage(stringResource(R.string.ai_recommendations_no_signal_message))
    }
}

// The nav rail's own Recommendations icon (ic_ai) reused here so the header
// conveys "AI" visually instead of spelling it out in the title text.
@Composable
private fun AiIcon() {
    Icon(
            painter = painterResource(CoreR.drawable.ic_ai),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(28.dp)
        )
}

@Composable
private fun RecommendationsMessage(
        message: String,
        onRetryClicked: (() -> Unit)? = null,
        navRailFocusRequester: FocusRequester? = null
                                  ) {
    Column(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
          ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AiIcon()
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                    text = stringResource(R.string.ai_recommendations_title),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )
        }
        Text(
                text = message,
                modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth(0.6f),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyLarge
            )
        if (onRetryClicked != null) {
            Spacer(modifier = Modifier.height(24.dp))
            TvDetailsActionButton(
                    text = stringResource(R.string.retry_action),
                    onClick = onRetryClicked,
                    // Left goes to this tab's rail item explicitly: spatial focus
                    // search would pick whichever rail item is nearest in height
                    // (News, from where this button sits).
                    modifier = Modifier.onPreviewKeyEvent { keyEvent ->
                        if (navRailFocusRequester != null &&
                            keyEvent.type == KeyEventType.KeyDown &&
                            keyEvent.key == Key.DirectionLeft
                        ) {
                            runCatching { navRailFocusRequester.requestFocus() }
                            true
                        } else {
                            false
                        }
                    }
                                 )
        }
    }
}

// No discovery link out to AI Studio here (see ApiKeySetupSteps' doc) - its
// key page doesn't render in this WebView, so ai_recommendations_empty_message
// itself carries the "how to get a key" instructions instead of a button.
@Composable
private fun RecommendationsKeySetup(onApiKeySaved: (apiKey: String) -> Unit) {
    val context = LocalContext.current
    val savedMessage = stringResource(R.string.gemini_api_key_saved)

    Column(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
          ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AiIcon()
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                    text = stringResource(R.string.ai_recommendations_title),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )
        }
        Text(
                text = stringResource(R.string.ai_recommendations_empty_message),
                modifier = Modifier
                        .padding(top = 12.dp, bottom = 24.dp)
                        .fillMaxWidth(0.6f),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyLarge
            )

        ApiKeySetupSteps(
                onSaveClicked = { apiKey ->
                    onApiKeySaved(apiKey)
                    Toast.makeText(context, savedMessage, Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(0.6f)
                         )
    }
}

// No discovery link out to Guardian's key-signup page here (see
// ApiKeySetupSteps' doc) - news_api_key_setup_message itself carries the
// "how to get a key" instructions instead of a button.
@Composable
private fun NewsKeySetup(onApiKeySaved: (apiKey: String) -> Unit) {
    val context = LocalContext.current
    val savedMessage = stringResource(R.string.news_api_key_saved)

    Column(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
          ) {
        Text(
                text = stringResource(TvCatalogDestination.News.labelResId),
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall
            )
        Text(
                text = stringResource(R.string.news_api_key_setup_message),
                modifier = Modifier
                        .padding(top = 12.dp, bottom = 24.dp)
                        .fillMaxWidth(0.6f),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyLarge
            )

        ApiKeySetupSteps(
                onSaveClicked = { apiKey ->
                    onApiKeySaved(apiKey)
                    Toast.makeText(context, savedMessage, Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(0.6f)
                         )
    }
}

@Preview(device = "id:tv_1080p")
@Composable
private fun CatalogTvScreenPreview() {
    MMoviesTheme {
        Scaffold(
                containerColor = MaterialTheme.colorScheme.background
                ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                CatalogTvScreenContent(
                        selectedDestination = TvCatalogDestination.Movies,
                        screenState = TvCatalogScreenState.Movies(
                                listOf(
                                        TvCategoryRowContent(
                                                MovieCategory.PopularMovieCategory,
                                                emptyList()
                                                             )
                                      )
                                                                  ),
                        onDestinationSelected = {},
                        onViewDetailsClicked = { _, _ -> },
                        onFeaturedFavoriteClicked = {},
                        onMovieClicked = {}
                                       )
            }
        }
    }
}
