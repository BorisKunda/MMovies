package com.bk.mmovies.tv.ui.component

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.bk.mmovies.core.R as CoreR
import com.bk.mmovies.ui.theme.MMoviesTheme

private val tvItemWidth = 140.dp
private val tvItemHeight = 210.dp
private val tvItemShape = RoundedCornerShape(10.dp)
private val rememberedAccentHeight = 4.dp

// Matches the amber accent used for "remembered" state in the catalog focus
// design (see TV_DETAILS_TRAILER_NEWS_AUTH.md / the catalog focus map) -
// distinct from MaterialTheme.colorScheme.primary, which is reserved for
// true focus, so the two states never read as the same color.
private val rememberedAccentColor = Color(0xFFE7A23D)

@Composable
fun TvItem(
        posterUrl: String,
        title: String = "",
        // Upcoming titles are frequently posterless this far ahead of
        // release - the plain placeholder alone (no title) reads as a
        // broken/empty tile in that row specifically, so this overlays the
        // title on the placeholder there. Other rows keep the bare
        // placeholder look rather than every blank poster growing a label.
        isUpcoming: Boolean = false,
        // True for the item a row would restore focus to if re-entered right
        // now (see TvRow's row-level focus memory) - shown only while this
        // item itself isn't the one truly focused, so the two states never
        // overlap on the same tile.
        isRemembered: Boolean = false,
        focusRequester: FocusRequester? = null,
        onFocusChanged: (isFocused: Boolean) -> Unit = {},
        onKeyPressed: (keyEvent: KeyEvent) -> Boolean = { false }
          ) {
    var isFocused by remember { mutableStateOf(false) }

    Box(
            modifier = Modifier
                    .padding(5.dp)
                    .width(tvItemWidth)
                    .height(tvItemHeight)
                    .then(
                            if (isFocused) {
                                Modifier.border(
                                        width = 3.dp,
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = tvItemShape
                                               )
                            } else {
                                Modifier
                            }
                         )
                    .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
                    // onFocusChanged only observes the focus target that comes
                    // AFTER it in the chain - same rule as focusRequester - so
                    // it must precede focusable(), not follow it.
                    .onFocusChanged { focusState ->
                        isFocused = focusState.isFocused
                        onFocusChanged(focusState.isFocused)
                    }
                    .focusable()
                    .onKeyEvent { keyEvent ->
                        // The actual "select" action is entirely the
                        // caller's: onKeyPressed already fires onItemFocused
                        // + onItemPressed for a DirectionCenter key-up (see
                        // TvRow), so there's nothing else to do with it here.
                        onKeyPressed(keyEvent)
                    }
       ) {
        if (posterUrl.isBlank()) {
            Image(
                    painter = painterResource(CoreR.drawable.placeholder),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                            .width(tvItemWidth)
                            .height(tvItemHeight)
                            .clip(tvItemShape)
                 )
        } else {
            AsyncImage(
                    model = posterUrl,
                    error = painterResource(CoreR.drawable.placeholder),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                            .width(tvItemWidth)
                            .height(tvItemHeight)
                            .clip(tvItemShape)
                      )
        }
        Box(
                modifier = Modifier
                        .fillMaxSize()
                        .clip(tvItemShape)
                        .background(Color.Black.copy(alpha = if (isFocused) 0.15f else 0.3f))
           )
        if (isRemembered && !isFocused) {
            Box(
                    modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(rememberedAccentHeight)
                            .background(rememberedAccentColor)
               )
        }
        if (isUpcoming && posterUrl.isBlank() && title.isNotBlank()) {
            Text(
                    text = title,
                    modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .padding(8.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall
                )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview
@Composable
private fun TvItemPreview() {
    MMoviesTheme {
        Scaffold(
                containerColor = MaterialTheme.colorScheme.background
                ) { _ ->

        }
    }
}
