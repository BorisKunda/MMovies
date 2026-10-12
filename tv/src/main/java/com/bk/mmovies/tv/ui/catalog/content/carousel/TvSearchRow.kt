package com.bk.mmovies.tv.ui.catalog.content.carousel

import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bk.mmovies.tv.R
import com.bk.mmovies.tv.theme.tvHeaderFontStyle

@Composable
fun TvSearchRow(
        query: String,
        onQueryChanged: (String) -> Unit,
        focusRequester: FocusRequester = remember { FocusRequester() },
        navRailFocusRequester: FocusRequester? = null
                        ) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val voiceSearchUnavailableMessage = stringResource(R.string.voice_search_unavailable)
    val voiceSearchPrompt = stringResource(R.string.voice_search_prompt)

    val voiceSearchLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val spokenText = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
        if (spokenText != null) {
            onQueryChanged(spokenText)
        }
    }

    Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
       ) {
        OutlinedTextField(
                value = query,
                onValueChange = onQueryChanged,
                modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester)
                        .onPreviewKeyEvent { keyEvent ->
                            if (keyEvent.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                            when {
                                keyEvent.key == Key.DirectionDown -> {
                                    focusManager.moveFocus(FocusDirection.Down)
                                    true
                                }
                                keyEvent.key == Key.DirectionLeft && query.isEmpty() && navRailFocusRequester != null -> {
                                    runCatching { navRailFocusRequester.requestFocus() }
                                    true
                                }
                                else                              -> false
                            }
                        },
                placeholder = { Text(text = stringResource(R.string.search_placeholder), style = tvHeaderFontStyle) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                        cursorColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                                          )
                 )
        Spacer(modifier = Modifier.width(12.dp))
        IconButton(onClick = {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PROMPT, voiceSearchPrompt)
            }
            try {
                voiceSearchLauncher.launch(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, voiceSearchUnavailableMessage, Toast.LENGTH_SHORT).show()
            }
        }) {
            Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = stringResource(R.string.voice_search_action),
                    tint = MaterialTheme.colorScheme.onSurface
                )
        }
    }
}
