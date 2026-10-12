package com.bk.mmovies.tv.ui.catalog.content.carousel

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

private val shimmerLabelWidth = 200.dp
private val shimmerLabelHeight = 20.dp
private val shimmerTileWidth = 105.dp
private val shimmerTileHeight = 158.dp
private val shimmerTileShape = RoundedCornerShape(10.dp)
private const val SHIMMER_TILE_COUNT = 10

// A row-shaped loading placeholder - a label-sized bar plus a run of
// tile-shaped boxes matching TvItem's own width/height/corner radius - shown
// in place of TvRow/CategoryRow while a row's data is still loading. Unlike
// LoaderView (a full-screen icon), this preserves the catalog's row/tile
// rhythm so real content sliding in doesn't jump the layout, and reads as
// "this row is loading" rather than "the app restarted".
@Composable
fun TvShimmerRow(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "tvRowShimmer")
    val alpha = transition.animateFloat(
            initialValue = 0.35f,
            targetValue = 0.7f,
            animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 700, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                                              ),
            label = "tvRowShimmerAlpha"
                                        )
    Column(modifier = modifier.fillMaxWidth()) {
        ShimmerBox(
                modifier = Modifier
                        .width(shimmerLabelWidth)
                        .height(shimmerLabelHeight),
                shape = RoundedCornerShape(6.dp),
                alpha = alpha
                  )
        LazyRow(
                modifier = Modifier.fillMaxWidth(),
                userScrollEnabled = false,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
               ) {
            items(SHIMMER_TILE_COUNT) {
                ShimmerBox(
                        modifier = Modifier
                                .padding(5.dp)
                                .width(shimmerTileWidth)
                                .height(shimmerTileHeight),
                        shape = shimmerTileShape,
                        alpha = alpha
                          )
            }
        }
    }
}

@Composable
private fun ShimmerBox(modifier: Modifier = Modifier, shape: Shape, alpha: State<Float>) {
    val baseColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.24f)
    Box(
            modifier = modifier
                    .clip(shape)
                    .drawBehind { drawRect(color = baseColor, alpha = alpha.value) }
       )
}
