package com.bk.mmovies.tv.ui.inputtest

import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bk.mmovies.tv.input.KeyInterceptorEffect
import com.bk.mmovies.tv.input.TvInputMonitor
import com.bk.mmovies.tv.input.monitorFocus
import com.bk.mmovies.ui.theme.MMoviesTheme
import kotlinx.coroutines.delay

private const val BTN_1 = "btn_1"
private const val CARD_6 = "card_6"
private const val BLOCK_TOGGLE = "block_toggle"

// Throwaway playground for exercising TvInputMonitor on a real device:
// a grid of named focusable buttons/cards (focus logging), a live status
// line, plus one control each for the monitor's manage abilities.
// Watch Logcat with tag TvInputMonitor.
@Composable
fun InputMonitorTestScreen(onExit: () -> Unit) {
    var focusedName by remember { mutableStateOf("none") }
    var lastKey by remember { mutableStateOf("none") }
    var clickCount by remember { mutableIntStateOf(0) }
    var blockCenter by remember { mutableStateOf(false) }
    var redirectDown by remember { mutableStateOf(false) }

    BackHandler(onBack = onExit)

    // Observe-only, plus (when toggled) consumes select presses - except on
    // the toggle itself, so the block can always be switched off again.
    KeyInterceptorEffect { event ->
        if (event.action == KeyEvent.ACTION_DOWN) lastKey = KeyEvent.keyCodeToString(event.keyCode)
        val isSelect = event.keyCode == KeyEvent.KEYCODE_DPAD_CENTER || event.keyCode == KeyEvent.KEYCODE_ENTER
        blockCenter && isSelect && focusedName != BLOCK_TOGGLE
    }

    LaunchedEffect(redirectDown) {
        if (redirectDown) {
            TvInputMonitor.setNextFocus(BTN_1, FocusDirection.Down, CARD_6)
        } else {
            TvInputMonitor.clearNextFocus(BTN_1, FocusDirection.Down)
        }
    }
    DisposableEffect(Unit) {
        onDispose { TvInputMonitor.clearNextFocus(BTN_1, FocusDirection.Down) }
    }
    LaunchedEffect(Unit) {
        delay(150)
        TvInputMonitor.requestFocus(BTN_1)
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { paddingValues ->
        Column(
                modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(48.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
              ) {
            Text(
                    text = "TvInputMonitor test",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium
                )
            Text(
                    text = "Focused: $focusedName   |   Last key: $lastKey   |   Clicks: $clickCount" +
                            "   |   Block select: $blockCenter   |   btn_1 Down -> card_6: $redirectDown",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyMedium
                )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                listOf(BTN_1, "btn_2", "btn_3").forEach { name ->
                    TestItem(name, name, 200.dp, 56.dp, { focusedName = it }) { clickCount++ }
                }
            }
            for (row in 0..1) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    for (col in 1..4) {
                        val name = "card_${row * 4 + col}"
                        TestItem(name, name, 200.dp, 110.dp, { focusedName = it }) { clickCount++ }
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TestItem(BLOCK_TOGGLE, "Block select: $blockCenter", 260.dp, 56.dp, { focusedName = it }) {
                    blockCenter = !blockCenter
                }
                TestItem("redirect_toggle", "btn_1 Down -> card_6: $redirectDown", 260.dp, 56.dp, { focusedName = it }) {
                    redirectDown = !redirectDown
                }
                TestItem("jump", "Jump to card_6", 260.dp, 56.dp, { focusedName = it }) {
                    TvInputMonitor.requestFocus(CARD_6)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                    text = "BACK exits this screen. Logcat tag: TvInputMonitor",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.bodySmall
                )
        }
    }
}

@Composable
private fun TestItem(
        name: String,
        label: String,
        width: Dp,
        height: Dp,
        onFocusedName: (String) -> Unit,
        onClick: () -> Unit
                    ) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                    .size(width, height)
                    // Must precede clickable so it binds to its focus target.
                    .monitorFocus(name)
                    .onFocusChanged { if (it.isFocused) onFocusedName(name) }
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isFocused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
       ) {
        Text(
                text = label,
                color = if (isFocused) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge
            )
    }
}

@Preview(device = "id:tv_1080p")
@Composable
private fun InputMonitorTestScreenPreview() {
    MMoviesTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                InputMonitorTestScreen(onExit = {})
            }
        }
    }
}
