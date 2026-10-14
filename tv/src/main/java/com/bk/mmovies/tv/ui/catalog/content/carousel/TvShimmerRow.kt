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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

private val shimmerPadding = 5.dp
private val shimmerHeaderWidth = 210.dp
private val shimmerLabelHeight = 20.dp
private val shimmerListItemWidth = 105.dp
private val shimmerListItemHeight = 158.dp
private val shimmerListItemShape = RoundedCornerShape(10.dp)
private const val SHIMMER_LIST_ITEMS_COUNT = 10

@Composable
fun TvShimmerRow(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "tvRowShimmer")
    val alpha: State<Float> = transition.animateFloat(
            initialValue = 0.35f,
            targetValue = 0.7f,
            animationSpec = infiniteRepeatable(
                    animation = tween(
                            durationMillis = 700,
                            easing = FastOutSlowInEasing
                                     ),
                    repeatMode = RepeatMode.Reverse
                                              ),
            label = "tvRowShimmerAlpha"
                                                     )
    Column(modifier = modifier.fillMaxWidth()) {
        ShimmerHeader(alpha)
        Spacer(Modifier.height(10.dp))
        ShimmerList(alpha)
    }
}

@Composable
private fun ShimmerHeader(alpha: State<Float>) {
    ShimmerContainer(
            modifier = Modifier
                    .width(shimmerHeaderWidth)
                    .height(shimmerLabelHeight)
                    .padding(start = shimmerPadding),
            shape = RoundedCornerShape(6.dp),
            alpha = alpha
                    )
}

@Composable
private fun ShimmerList(alpha: State<Float>) {
    LazyRow(
            modifier = Modifier.fillMaxWidth(),
            userScrollEnabled = false,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
           ) {
        items(SHIMMER_LIST_ITEMS_COUNT) {
            ShimmerContainer(
                    modifier = Modifier
                            .padding(shimmerPadding)
                            .width(shimmerListItemWidth)
                            .height(shimmerListItemHeight),
                    shape = shimmerListItemShape,
                    alpha = alpha
                            )
        }
    }
}


@Composable
private fun ShimmerContainer(
        modifier: Modifier = Modifier,
        shape: Shape,
        alpha: State<Float>
                            ) {
    val baseColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.24f)
    Box(
            modifier = modifier
                    .clip(shape)
                    .drawBehind {
                        drawRect(
                                color = baseColor,
                                alpha = alpha.value
                                )
                    }
       )
}
