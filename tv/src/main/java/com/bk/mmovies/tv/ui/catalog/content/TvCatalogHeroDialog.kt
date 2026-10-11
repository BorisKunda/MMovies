package com.bk.mmovies.tv.ui.catalog.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.bk.mmovies.domain.model.CatalogItem
import com.bk.mmovies.domain.model.Category
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.TvSeriesCategory
import com.bk.mmovies.tv.ui.catalog.FOCUS_REQUEST_RETRY_FRAMES
import kotlinx.coroutines.launch
import com.bk.mmovies.tv.R
import com.bk.mmovies.tv.ui.component.rememberFavoriteTapCooldown
import com.bk.mmovies.ui.component.UserScoreView

private val heroHeight = 380.dp
private val heroCornerShape = 20.dp

@Composable
fun TvCatalogHeroDialog(
        item: CatalogItem,
        focusedCategory: Category?,
        pruneOnClose: Boolean,
        showFavoriteButton: Boolean,
        railFocusRequester: FocusRequester?,
        onViewDetailsClicked: (item: CatalogItem, category: Category?) -> Unit,
        onFavoriteClicked: () -> Unit,
        onDismissed: () -> Unit,
        onPopupClosedPrune: () -> Unit
                       ) {
    val coroutineScope = rememberCoroutineScope()
    val viewDetailsFocusRequester = remember { FocusRequester() }
    val isUpcoming = focusedCategory == MovieCategory.UpcomingMovieCategory ||
                     focusedCategory == TvSeriesCategory.UpcomingTvSeriesCategory

    LaunchedEffect(item.id, item.mediaType) {
        repeat(FOCUS_REQUEST_RETRY_FRAMES) {
            runCatching { viewDetailsFocusRequester.requestFocus() }
            withFrameNanos {}
        }
    }

    val closeDialog: () -> Unit = {
        onDismissed()
        if (pruneOnClose) {
            coroutineScope.launch {
                withFrameNanos {}
                railFocusRequester?.let { runCatching { it.requestFocus() } }
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
                    item = item,
                    onViewDetailsClicked = {
                        closeDialog()
                        onViewDetailsClicked(item, focusedCategory)
                    },
                    onFavoriteClicked = onFavoriteClicked,
                    showFavoriteButton = showFavoriteButton,
                    showUserScore = !isUpcoming && item.rating > 0,
                    viewDetailsFocusRequester = viewDetailsFocusRequester
                             )
        }
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
                        val (favoriteCooling, guardedFavoriteClick) = rememberFavoriteTapCooldown(onFavoriteClicked)
                        OutlinedButton(
                                onClick = guardedFavoriteClick,
                                colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color.White
                                                                            ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.alpha(if (favoriteCooling) 0.5f else 1f)
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


