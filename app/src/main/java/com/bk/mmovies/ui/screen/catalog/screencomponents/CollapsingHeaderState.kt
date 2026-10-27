package com.bk.mmovies.ui.screen.catalog.screencomponents

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.layout
import kotlin.math.roundToInt

class CollapsingHeaderState {
    var collapsedOffsetPx by mutableFloatStateOf(0f)
    var fullHeightPx = 0f

    val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val newOffset = (collapsedOffsetPx + available.y).coerceIn(-fullHeightPx, 0f)
            val consumedY = newOffset - collapsedOffsetPx
            collapsedOffsetPx = newOffset
            return Offset(0f, consumedY)
        }
    }
}

@Composable
fun rememberCollapsingHeaderState(): CollapsingHeaderState = remember { CollapsingHeaderState() }

fun Modifier.collapsingHeader(state: CollapsingHeaderState): Modifier = this
        .layout { measurable, constraints ->
            val placeable = measurable.measure(constraints)
            state.fullHeightPx = placeable.height.toFloat()
            val clampedOffset = state.collapsedOffsetPx.coerceIn(-state.fullHeightPx, 0f)
            val visibleHeight = (placeable.height + clampedOffset).roundToInt()
            layout(placeable.width, visibleHeight) {
                placeable.placeRelative(0, clampedOffset.roundToInt())
            }
        }
        .clipToBounds()
