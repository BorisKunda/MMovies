package com.bk.mmovies.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val backgroundColor = Color(0xFF1A1A1A)
private val ringColor = Color(0xFF3D3D3D)
private val progressBelow40Color = Color(0xFFDB2360)
private val progress40To70Color = Color(0xFFD2D531)
private val progressAbove70Color = Color(0xFF21D07A)
private val ringStrokeWidth = 4.dp

@Composable
fun UserScoreView(
    score: Int,
    modifier: Modifier = Modifier,
    size: Dp? = 40.dp
) {
    val clampedScore = score.coerceIn(0, 100)

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
            fontSize = 10.sp,
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