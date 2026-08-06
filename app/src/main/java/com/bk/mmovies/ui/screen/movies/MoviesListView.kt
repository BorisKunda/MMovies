package com.bk.mmovies.ui.screen.movies

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.bk.mmovies.R
import com.bk.mmovies.data.source.remote.POSTER_PATH_SIZE_SEGMENT_LIST_ITEM
import com.bk.mmovies.data.source.remote.POSTER_PATH_SIZE_SEGMENT_ZOOM
import com.bk.mmovies.domain.model.MovieCategory
import com.bk.mmovies.domain.model.MovieModel
import com.bk.mmovies.ui.component.UserScoreView
import com.bk.mmovies.ui.theme.MMoviesTheme
import com.bk.mmovies.util.logDebug
import com.bk.mmovies.util.logError

private val listTopBottomPadding = 8.dp
private val cardPaddingHorizontal = 12.dp
private val cardPaddingVertical = 6.dp
private val cardCornerShape = 12.dp
private val cardElevation = 4.dp
private val rowPaddingHorizontal = 16.dp
private val rowPaddingVertical = 14.dp
private val spacerPadding = 12.dp
private val badgeSpacerPadding = 8.dp
private val textPadding = 4.dp
private val posterWidth = 92.dp
private val posterHeight = 130.dp
private val posterCornerShape = 8.dp
private val ratingBadgeSize = 28.dp

// New: styling for the score caption under the badge.
private val scoreLabelTopSpacing = 6.dp
private val scoreLabelFontSize = 8.sp
private val scoreLabelLetterSpacing = 0.5.sp
private val scoreLabelAlpha = 0.45f

@Composable
fun MoviesListView(
        movies: List<MovieModel>,
        selectedCategory: MovieCategory,
        onMovieClicked: (movieId: Int) -> Unit
                  ) {
    LazyColumn(
            content = {
                items(movies.size) { position ->
                    MovieRow(
                            movies[position],
                            showUserScore = selectedCategory != MovieCategory.UpcomingMovieCategory,
                            onMovieClicked
                            )
                }
            },
            contentPadding = PaddingValues(
                    bottom = listTopBottomPadding
                                          ),
            modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(),
              )
    LaunchedEffect(Unit) {
        logDebug(
                "MoviesListView",
                "LAUNCHED"
                )
    }
    DisposableEffect(Unit) {
        onDispose {
            logDebug(
                    "MoviesListView",
                    "DISPOSED"
                    )
        }
    }
}

@Composable
fun MovieRowLoadingPlaceholderList() {
    LazyColumn(
            userScrollEnabled = false,
            content = {
                items(4) { position ->
                    MovieRowLoadingPlaceholder()
                }
            },
            contentPadding = PaddingValues(
                    bottom = listTopBottomPadding
                                          ),
            modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(),
              )
    LaunchedEffect(Unit) {
        logDebug(
                "MoviesLoadingList",
                "LAUNCHED"
                )
    }
    DisposableEffect(Unit) {
        onDispose {
            logDebug(
                    "MoviesLoadingList",
                    "DISPOSED"
                    )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MovieRow(
        movieModel: MovieModel,
        showUserScore: Boolean = true,
        onMovieClicked: (Int) -> Unit
            ) {
    var isPosterZoomed by remember { mutableStateOf(false) }

    Card(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                            horizontal = cardPaddingHorizontal,
                            vertical = cardPaddingVertical
                            )
                    .clickable {
                        onMovieClicked(movieModel.id)
                    },
            shape = RoundedCornerShape(cardCornerShape),

            colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A1A)
                                            ),

            elevation = CardDefaults.cardElevation(
                    defaultElevation = cardElevation
                                                  )
        ) {
        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                                horizontal = rowPaddingHorizontal,
                                vertical = rowPaddingVertical
                                ),

                verticalAlignment = Alignment.Top
           ) {
            AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                            .data(movieModel.imageUrl)
                            .crossfade(true)
                            .build(),
                    placeholder = painterResource(R.drawable.placeholder),
                    error = painterResource(
                            if (showUserScore) R.drawable.error_placeholder else R.drawable.placeholder
                                            ),
                    contentDescription = movieModel.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                            .size(
                                    width = posterWidth,
                                    height = posterHeight
                                 )
                            .clip(RoundedCornerShape(posterCornerShape))
                            .combinedClickable(
                                    onClick = { onMovieClicked(movieModel.id) },
                                    onLongClick = { isPosterZoomed = true }
                                              ),
                    onLoading = {
                        logDebug(
                                "MOVIE_ROW",
                                "loading image from url: ${movieModel.imageUrl}"
                                )
                    },
                    onSuccess = {
                        logDebug(
                                "MOVIE_ROW",
                                "success - loading image from url: ${movieModel.imageUrl}"
                                )
                    },
                    onError = { state ->
                        logError(
                                "MOVIE_ROW_TAG",
                                "error - loading image from url: ${movieModel.imageUrl} cause: ${state.result.throwable}"
                                )
                    },
                      )
            Spacer(modifier = Modifier.width(spacerPadding))
            Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.Top
               ) {
                if (showUserScore) {
                    // Score badge + caption grouped in a Column so the label
                    // sits directly under the ring instead of competing with
                    // the title text next to it.
                    Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(
                                    top = 10.dp,
                                    end = 5.dp
                                                       )
                          ) {
                        UserScoreView(
                                score = movieModel.rating,
                                size = ratingBadgeSize
                                     )
                        Spacer(modifier = Modifier.height(scoreLabelTopSpacing))
                        Text(
                                text = "USER\nSCORE",
                                color = Color.White.copy(alpha = scoreLabelAlpha),
                                fontSize = scoreLabelFontSize,
                                lineHeight = scoreLabelFontSize,
                                letterSpacing = scoreLabelLetterSpacing,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                    }
                    Spacer(modifier = Modifier.width(badgeSpacerPadding))
                }
                Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(textPadding)
                      ) {
                    Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = movieModel.title,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    Text(
                            modifier = Modifier.wrapContentSize(),
                            text = movieModel.releaseDate,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                }
            }
        }
    }

    if (isPosterZoomed) {
        Dialog(
                onDismissRequest = { isPosterZoomed = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
              ) {
            Box(
                    modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.9f))
                            .clickable { isPosterZoomed = false },
                    contentAlignment = Alignment.Center
               ) {
                AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                                .data(
                                        movieModel.imageUrl.replace(
                                                POSTER_PATH_SIZE_SEGMENT_LIST_ITEM,
                                                POSTER_PATH_SIZE_SEGMENT_ZOOM
                                                                    )
                                     )
                                .crossfade(true)
                                .build(),
                        placeholder = painterResource(R.drawable.placeholder),
                        error = painterResource(R.drawable.error_placeholder),
                        contentDescription = movieModel.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                                .aspectRatio(posterWidth / posterHeight)
                                .clip(RoundedCornerShape(posterCornerShape))
                          )
            }
        }
    }
}

@Composable
fun MovieRowLoadingPlaceholder() {
    val transition = rememberInfiniteTransition(label = "loading")

    val loadingAlpha by transition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 900),
                    repeatMode = RepeatMode.Reverse
                                              ),
            label = "loadingAlpha"
                                               )

    Card(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                            horizontal = cardPaddingHorizontal,
                            vertical = cardPaddingVertical
                            ),

            shape = RoundedCornerShape(cardCornerShape),

            colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A1A)
                                            ),

            elevation = CardDefaults.cardElevation(
                    defaultElevation = cardElevation
                                                  )
        ) {
        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                                horizontal = rowPaddingHorizontal,
                                vertical = rowPaddingVertical
                                ),

                verticalAlignment = Alignment.Top
           ) {
            Image(
                    painter = painterResource(R.drawable.placeholder),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                            .size(
                                    width = posterWidth,
                                    height = posterHeight
                                 )
                            .clip(RoundedCornerShape(posterCornerShape))
                            .alpha(loadingAlpha)
                 )
            Spacer(modifier = Modifier.width(spacerPadding))
            Box(
                    modifier = Modifier
                            .padding(top = textPadding)
                            .fillMaxWidth()
                            .height(24.dp)
                            .alpha(loadingAlpha)
                            .background(
                                    color = Color.Gray,
                                    shape = RoundedCornerShape(8.dp)
                                       )
               )
        }
    }
}

@Preview
@Composable
private fun MovieRowLoadingPlaceholderListPreview(){
    MMoviesTheme {
        MovieRowLoadingPlaceholderList()
    }
}

@Preview
@Composable
private fun MovieRowLoadingPlaceholderPreview() {
    MMoviesTheme {
        MovieRowLoadingPlaceholder()
    }
}

@Preview
@Composable
private fun MoviesListViewPreview() {
    val sampleMovies = listOf(
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
                      ),
            MovieModel(
                    id = 3,
                    title = "Interstellar",
                    desc = "A team of explorers travel through a wormhole in space.",
                    imageUrl = "https://image.tmdb.org/t/p/w342/yQvGrMoipbRoddT0ZR8tPoR7NfX.jpg",
                    releaseDate = "November 7, 2014",
                    rating = 85
                      )
                             )
    MoviesListView(
            movies = sampleMovies,
            selectedCategory = MovieCategory.PopularMovieCategory,
            onMovieClicked = {}
                  )
}
