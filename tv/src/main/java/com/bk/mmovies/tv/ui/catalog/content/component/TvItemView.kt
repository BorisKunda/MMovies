package com.bk.mmovies.tv.ui.catalog.content.component

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.bk.mmovies.ui.theme.MMoviesTheme

private val tvItemWidth = 105.dp
private val tvItemHeight = 158.dp
private val tvItemCornerRadius = 10.dp
private val tvItemFocusBorderWidth = 3.dp
private val tvItemShape = RoundedCornerShape(tvItemCornerRadius)

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
        placeholderPainter: Painter,
        focusRequester: FocusRequester? = null,
        onFocusChanged: (isFocused: Boolean) -> Unit = {},
        onKeyPressed: (keyEvent: KeyEvent) -> Boolean = { false }
          ) {
    var isFocused by remember { mutableStateOf(false) }
    val focusBorderColor = MaterialTheme.colorScheme.primary

    Box(
            modifier = Modifier
                    .padding(5.dp)
                    .width(tvItemWidth)
                    .height(tvItemHeight)
                    .drawWithContent {
                        drawContent()
                        if (isFocused) {
                            val strokeWidthPx = tvItemFocusBorderWidth.toPx()
                            val halfStrokePx = strokeWidthPx / 2f
                            drawRoundRect(
                                    color = focusBorderColor,
                                    topLeft = Offset(
                                            halfStrokePx,
                                            halfStrokePx
                                                    ),
                                    size = Size(
                                            size.width - strokeWidthPx,
                                            size.height - strokeWidthPx
                                               ),
                                    cornerRadius = CornerRadius(tvItemCornerRadius.toPx() - halfStrokePx),
                                    style = Stroke(width = strokeWidthPx)
                                         )
                        }
                    }
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
        TvItemPoster(
                posterUrl = posterUrl,
                placeholderPainter = placeholderPainter
                    )
        Box(
                modifier = Modifier
                        .fillMaxSize()
                        .clip(tvItemShape)
                        .drawBehind {
                            drawRect(color = Color.Black.copy(alpha = if (isFocused) 0.15f else 0.3f))
                        }
           )
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

@Composable
private fun TvItemPoster(
        posterUrl: String,
        placeholderPainter: Painter
                         ) {
    if (posterUrl.isBlank()) {
        Image(
                painter = placeholderPainter,
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
                error = placeholderPainter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                        .width(tvItemWidth)
                        .height(tvItemHeight)
                        .clip(tvItemShape)
                  )
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
