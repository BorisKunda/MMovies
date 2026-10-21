package com.bk.mmovies.tv.ui.catalog.content.carousel

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
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
import androidx.core.content.ContextCompat
import com.bk.mmovies.tv.R
import com.bk.mmovies.tv.theme.tvHeaderTextStyle

@Composable
fun TvSearchRow(
        query: String,
        onQueryChanged: (String) -> Unit,
        focusRequester: FocusRequester = remember { FocusRequester() },
        navRailFocusRequester: FocusRequester? = null
                        ) {
    val focusManager = LocalFocusManager.current
    val clearButtonFocusRequester = remember { FocusRequester() }
    val micButtonFocusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    val voiceSearchUnavailableMessage = stringResource(R.string.voice_search_unavailable)
    val voiceSearchPrompt = stringResource(R.string.voice_search_prompt)
    val voiceSearchPermissionDeniedMessage = stringResource(R.string.voice_search_permission_denied)

    var isListening by remember { mutableStateOf(false) }
    val currentOnQueryChanged by rememberUpdatedState(onQueryChanged)
    val speechRecognizer = remember {
        if (SpeechRecognizer.isRecognitionAvailable(context)) SpeechRecognizer.createSpeechRecognizer(context) else null
    }
    DisposableEffect(speechRecognizer) {
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) = Unit
            override fun onBeginningOfSpeech() = Unit
            override fun onRmsChanged(rmsdB: Float) = Unit
            override fun onBufferReceived(buffer: ByteArray?) = Unit
            override fun onEndOfSpeech() = Unit
            override fun onEvent(eventType: Int, params: Bundle?) = Unit
            override fun onPartialResults(partialResults: Bundle?) = Unit
            override fun onError(error: Int) {
                isListening = false
            }

            override fun onResults(results: Bundle?) {
                isListening = false
                results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull()
                        ?.let(currentOnQueryChanged)
            }
        })
        onDispose { speechRecognizer?.destroy() }
    }

    fun startListening() {
        val recognitionIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        }
        isListening = true
        speechRecognizer?.startListening(recognitionIntent)
    }

    val microphonePermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            startListening()
        } else {
            Toast.makeText(context, voiceSearchPermissionDeniedMessage, Toast.LENGTH_LONG).show()
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
                                keyEvent.key == Key.DirectionRight && query.isNotEmpty() -> {
                                    runCatching { clearButtonFocusRequester.requestFocus() }
                                    true
                                }
                                keyEvent.key == Key.DirectionRight && query.isEmpty() -> {
                                    runCatching { micButtonFocusRequester.requestFocus() }
                                    true
                                }
                                keyEvent.key == Key.DirectionLeft && query.isEmpty() && navRailFocusRequester != null -> {
                                    runCatching { navRailFocusRequester.requestFocus() }
                                    true
                                }
                                else                              -> false
                            }
                        },
                placeholder = {
                    Text(
                            text = if (isListening) voiceSearchPrompt else stringResource(R.string.search_placeholder),
                            style = tvHeaderTextStyle
                        )
                },
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
        if (query.isNotEmpty()) {
            Spacer(modifier = Modifier.width(12.dp))
            TvSearchIconButton(
                    onClick = {
                        onQueryChanged("")
                        runCatching { focusRequester.requestFocus() }
                    },
                    modifier = Modifier.focusRequester(clearButtonFocusRequester)
                              ) {
                Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.search_field_clear_action),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        TvSearchIconButton(modifier = Modifier.focusRequester(micButtonFocusRequester), onClick = {
            when {
                speechRecognizer == null -> Toast.makeText(context, voiceSearchUnavailableMessage, Toast.LENGTH_SHORT).show()
                isListening -> {
                    speechRecognizer.stopListening()
                    isListening = false
                }
                ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED -> startListening()
                else -> microphonePermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }) {
            Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = stringResource(R.string.voice_search_action),
                    tint = if (isListening) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
        }
    }
}

@Composable
private fun TvSearchIconButton(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
                              ) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    IconButton(
            onClick = onClick,
            interactionSource = interactionSource,
            modifier = modifier.then(
                    if (isFocused) {
                        Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    } else {
                        Modifier
                    }
                                    ),
            content = content
              )
}
