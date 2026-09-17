package com.bk.mmovies.tv.ui.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

private val shimmerLabelWidth = 200.dp
private val shimmerLabelHeight = 26.dp
private val shimmerTileWidth = 140.dp
private val shimmerTileHeight = 210.dp
private val shimmerTileShape = RoundedCornerShape(10.dp)
private const val SHIMMER_TILE_COUNT = 5

// A row-shaped loading placeholder - a label-sized bar plus a run of
// tile-shaped boxes matching TvItem's own width/height/corner radius - shown
// in place of TvRow/CategoryRow while a row's data is still loading. Unlike
// LoaderView (a full-screen icon), this preserves the catalog's row/tile
// rhythm so real content sliding in doesn't jump the layout, and reads as
// "this row is loading" rather than "the app restarted".
@Composable
fun TvRowShimmer(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        ShimmerBox(
                modifier = Modifier
                        .width(shimmerLabelWidth)
                        .height(shimmerLabelHeight),
                shape = RoundedCornerShape(6.dp)
                  )
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(SHIMMER_TILE_COUNT) {
                ShimmerBox(
                        modifier = Modifier
                                .width(shimmerTileWidth)
                                .height(shimmerTileHeight),
                        shape = shimmerTileShape
                          )
            }
        }
    }
}

@Composable
private fun ShimmerBox(modifier: Modifier = Modifier, shape: Shape) {
    val transition = rememberInfiniteTransition(label = "tvRowShimmer")
    val alpha by transition.animateFloat(
            initialValue = 0.35f,
            targetValue = 0.7f,
            animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 700, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                                              ),
            label = "tvRowShimmerAlpha"
                                        )
    Box(
            modifier = modifier
                    .clip(shape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha * 0.24f))
       )
}
