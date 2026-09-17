package com.bk.mmovies.tv.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.bk.mmovies.core.R

private val stepIndicatorSize = 44.dp
private val stepHeaderPaddingBottom = 20.dp
private val stepHeaderTextPadding = 16.dp
private val buttonHeight = 56.dp
private val buttonCornerSize = 16.dp
private val outlineButtonBorderStrokeWidth = 1.dp

// The "get a key, then paste it in" form shared by every TV API-key setup
// surface. TMDB's ApiKeySetupTvScreen passes the discovery-link callbacks and
// gets the full two-step version (a button/link out to TMDB's site, which
// renders fine there). News and Gemini's setup surfaces (CatalogTvScreen)
// omit them - both destination sites either render unusably (Gemini's
// AI Studio needs a browser feature this WebView doesn't have) or are simply
// unnecessary friction here, so those two just show the plain "enter your
// key" step and expect the caller's own explanatory text above this to cover
// how to actually obtain one (see ai_recommendations_empty_message /
// news_api_key_setup_message).
@Composable
fun ApiKeySetupSteps(
        openKeySettingsLabel: String? = null,
        onOpenKeySettingsClicked: (() -> Unit)? = null,
        onApiKeyHelpClicked: (() -> Unit)? = null,
        onSaveClicked: (apiKey: String) -> Unit,
        modifier: Modifier = Modifier
                     ) {
    var apiKey by rememberSaveable { mutableStateOf("") }
    val apiKeyFieldFocusRequester = remember { FocusRequester() }
    val saveButtonFocusRequester = remember { FocusRequester() }
    val showKeySettingsStep = openKeySettingsLabel != null && onOpenKeySettingsClicked != null && onApiKeyHelpClicked != null

    Column(modifier = modifier) {
        if (showKeySettingsStep) {
            ApiKeyStepHeader(
                    stepNumber = 1,
                    title = stringResource(R.string.get_your_api_key)
                            )

            OutlinedButton(
                    onClick = onOpenKeySettingsClicked,
                    modifier = Modifier
                            .fillMaxWidth()
                            .height(buttonHeight)
                            .padding(bottom = 5.dp),
                    shape = RoundedCornerShape(buttonCornerSize),
                    border = BorderStroke(
                            width = outlineButtonBorderStrokeWidth,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                         ),
                    colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                                                                )
                          ) {
                Text(
                        text = openKeySettingsLabel,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
            }

            val helpLinkInteractionSource = remember { MutableInteractionSource() }
            val isHelpLinkFocused by helpLinkInteractionSource.collectIsFocusedAsState()

            Text(
                    text = stringResource(R.string.how_to_get_api_key),
                    modifier = Modifier
                            .padding(top = 15.dp, bottom = 36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                    if (isHelpLinkFocused) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                                    else Color.Transparent
                                       )
                            .clickable(
                                    interactionSource = helpLinkInteractionSource,
                                    indication = null,
                                    onClick = onApiKeyHelpClicked
                                      )
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                    color = if (isHelpLinkFocused) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = TextDecoration.Underline
                )

            HorizontalDivider(
                    modifier = Modifier.padding(bottom = 24.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                              )
        }

        if (showKeySettingsStep) {
            ApiKeyStepHeader(
                    stepNumber = 2,
                    title = stringResource(R.string.enter_your_api_key)
                            )
        } else {
            // No step 1 to count against, so a bare "1" would just be noise -
            // plain title, no numbered circle.
            Text(
                    modifier = Modifier.padding(bottom = stepHeaderPaddingBottom),
                    text = stringResource(R.string.enter_your_api_key),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
        }

        OutlinedTextField(
                value = apiKey,
                onValueChange = { newValue -> apiKey = newValue },
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 28.dp)
                        .focusRequester(apiKeyFieldFocusRequester)
                        // A plain OutlinedTextField consumes DPAD up/down
                        // itself (for cursor movement) instead of letting
                        // focus traverse past it, which otherwise permanently
                        // trapped D-pad navigation on this field - same fix
                        // as AuthTvScreen's username/password fields.
                        .onPreviewKeyEvent { event ->
                            if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionDown) {
                                saveButtonFocusRequester.requestFocus()
                                true
                            } else {
                                false
                            }
                        },
                placeholder = {
                    Text(text = stringResource(R.string.api_key))
                },
                singleLine = true,
                shape = RoundedCornerShape(buttonCornerSize),
                colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.55f
                                                                                           ),
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.55f
                                                                                             ),
                        cursorColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.4f
                                                                                        )
                                                         )
                         )

        Button(
                onClick = { onSaveClicked(apiKey.trim()) },
                enabled = apiKey.isNotBlank(),
                modifier = Modifier
                        .fillMaxWidth()
                        .height(buttonHeight)
                        .focusRequester(saveButtonFocusRequester)
                        .onPreviewKeyEvent { event ->
                            if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionUp) {
                                apiKeyFieldFocusRequester.requestFocus()
                                true
                            } else {
                                false
                            }
                        },
                shape = RoundedCornerShape(buttonCornerSize),
                colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.24f),
                        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                                     )
              ) {
            Text(
                    text = stringResource(R.string.save_and_continue),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
        }
    }
}

@Composable
private fun ApiKeyStepHeader(
        stepNumber: Int,
        title: String
                            ) {
    Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = stepHeaderPaddingBottom),
            verticalAlignment = Alignment.CenterVertically
       ) {
        Box(
                modifier = Modifier
                        .size(stepIndicatorSize)
                        .background(
                                color = MaterialTheme.colorScheme.onSurface,
                                shape = CircleShape
                                   ),
                contentAlignment = Alignment.Center
           ) {
            Text(
                    text = stepNumber.toString(),
                    color = MaterialTheme.colorScheme.background,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
        }

        Text(
                modifier = Modifier.padding(start = stepHeaderTextPadding),
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
    }
}
