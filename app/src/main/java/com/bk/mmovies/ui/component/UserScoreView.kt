package com.bk.mmovies.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bk.mmovies.ui.theme.CardSurface

private val backgroundColor = CardSurface
private val ringColor = Color(0xFF3D3D3D)
private val progressBelow40Color = Color(0xFFDB2360)
private val progress40To70Color = Color(0xFFD2D531)
private val progressAbove70Color = Color(0xFF21D07A)

private val defaultSize = 40.dp

// The ring and the numeral both scale with `size`, so callers that ask for a
// bigger badge get a proportionally bigger percentage rather than a tiny label
// adrift in a large circle. The floors keep the 28dp list badge exactly as it
// was tuned (4dp stroke, 10sp numeral) while letting larger badges grow.
private const val RING_STROKE_RATIO = 0.10f
private const val MIN_RING_STROKE_DP = 4f
private const val NUMERAL_SIZE_RATIO = 0.30f
private const val MIN_NUMERAL_SIZE_DP = 10f

@Composable
fun UserScoreView(
    score: Int,
    modifier: Modifier = Modifier,
    size: Dp? = defaultSize
) {
    val clampedScore = score.coerceIn(0, 100)
    val badgeSize = size ?: defaultSize
    val ringStrokeWidth = (badgeSize.value * RING_STROKE_RATIO)
        .coerceAtLeast(MIN_RING_STROKE_DP).dp
    // Converted through the density's fontScale (not a bare .sp) so the
    // numeral always fits this fixed-size ring — on devices/users with a
    // larger system font scale, plain .sp made the text overflow the badge.
    val numeralFontSize = with(LocalDensity.current) {
        (badgeSize.value * NUMERAL_SIZE_RATIO).coerceAtLeast(MIN_NUMERAL_SIZE_DP).dp.toSp()
    }

    Box(
        modifier = if (size != null) modifier.size(size) else modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(color = backgroundColor)
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = ringStrokeWidth.toPx(), cap = StrokeCap.Round)
            )
            if (clampedScore > 0) {
                drawArc(
                    color = scoreColor(clampedScore),
                    startAngle = -90f,
                    sweepAngle = 360f * (clampedScore / 100f),
                    useCenter = false,
                    style = Stroke(width = ringStrokeWidth.toPx(), cap = StrokeCap.Round)
                )
            }
        }
        Text(
            text = "$clampedScore%",
            color = Color.White,
            fontSize = numeralFontSize,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun scoreColor(score: Int): Color = when {
    score < 40 -> progressBelow40Color
    score < 70 -> progress40To70Color
    else -> progressAbove70Color
}

@Preview
@Composable
private fun UserScoreViewPreview() {
    Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
        UserScoreView(score = 50)
    }
}

// Every size actually used in the app, so the ring/numeral scaling stays honest.
@Preview
@Composable
private fun UserScoreViewSizesPreview() {
    Row(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserScoreView(score = 28, size = 28.dp)
        UserScoreView(score = 55, size = 40.dp)
        UserScoreView(score = 85, size = 52.dp)
    }
}