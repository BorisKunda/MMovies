package com.bk.mmovies.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bk.mmovies.R
import com.bk.mmovies.ui.theme.MMoviesTheme
import com.bk.mmovies.ui.theme.primaryButtonCornerSize
import com.bk.mmovies.ui.theme.primaryButtonHeight
import com.bk.mmovies.ui.theme.primaryButtonIconPadding
import com.bk.mmovies.ui.theme.primaryButtonIconSize
import com.bk.mmovies.util.logDebug


private val errorViewPaddingStart = 28.dp
private val errorViewPaddingTop = 64.dp
private val errorViewPaddingEnd = 28.dp
private val errorViewPaddingBottom = 32.dp
private val errorViewVerticalSpacing = 48.dp
private val errorImageHeight = 88.dp
private val errorTitlePaddingTop = 14.dp
private val errorMessagePaddingTop: Dp = 20.dp

private val apikeyStepIndicatorSize = 44.dp
private val apikeyStepHeaderPaddingBottom = 20.dp
private val apiKeyStepHeaderTextPadding = 16.dp

private val linkTextStartEndTopPadding = 10.dp
private val linkTextBottomPadding = 36.dp


private val outlineButtonBorderStrokeWidth = 1.dp
private val outlinedTextFieldPaddingBottom = 28.dp

@Composable
private fun ErrorView(
        modifier: Modifier,
        @DrawableRes errorImageResId: Int,
        errorTitle: String,
        errorMessage: String,
        actions: (@Composable ColumnScope.() -> Unit)? = null,
        useSpaceBetween: Boolean = false,
        tag: String = "ErrorView"
                     ) {
    LazyColumn(
            modifier = modifier
                    .background(MaterialTheme.colorScheme.background)
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .imePadding(),
            contentPadding = PaddingValues(
                    start = errorViewPaddingStart,
                    top = errorViewPaddingTop,
                    end = errorViewPaddingEnd,
                    bottom = errorViewPaddingBottom
                                          ),
            verticalArrangement = if (useSpaceBetween) Arrangement.SpaceBetween else Arrangement.spacedBy(
                    errorViewVerticalSpacing
                                                                                                         ),
            horizontalAlignment = Alignment.CenterHorizontally
              ) {
        item {
            ErrorTitleMessage(
                    errorImageResId,
                    errorTitle,
                    errorMessage
                             )
        }
        if (actions != null) {
            item {
                ErrorActions(actions)
            }
        }
    }

    LaunchedEffect(Unit) {
        logDebug(
                tag,
                "LAUNCHED"
                )
    }

    DisposableEffect(Unit) {
        onDispose {
            logDebug(
                    tag,
                    "DISPOSED"
                    )
        }
    }
}


@Composable
fun NoInternetView(
        onSettingsButtonClicked: () -> Unit
                  ) {
    ErrorView(
            modifier = Modifier.fillMaxSize(),
            errorImageResId = R.drawable.ic_no_internet,
            errorTitle = stringResource(
                    R.string.error_no_internet_connection_title
                                       ),
            errorMessage = stringResource(
                    R.string.error_no_internet_connection_message
                                         ),
            actions = {
                PrimaryButton(
                        imageResId = R.drawable.ic_settings,
                        label = stringResource(
                                R.string.error_no_internet_action
                                              ),
                        {
                            onSettingsButtonClicked()
                        },
                        true
                             )
            },
            useSpaceBetween = true,
            tag = "NoInternetView"
             )
}

@Composable
fun InvalidApiKeyView(
        onOpenTmdbSettingsClicked: () -> Unit,
        onApiKeyHelpClicked: () -> Unit,
        onSaveClicked: (apiKey: String) -> Unit,
        missingKey: Boolean
                     ) {
    var apiKey by rememberSaveable {
        mutableStateOf("")
    }

    ErrorView(
            modifier = Modifier.fillMaxSize(),
            errorImageResId = R.drawable.ic_invalid_api_key,
            errorTitle = if (missingKey) stringResource(
                    R.string.error_missing_api_key_title
                                                       )
            else stringResource(
                    R.string.error_invalid_api_key_title
                               ),
            errorMessage = if (missingKey) stringResource(
                    R.string.error_missing_api_key_message
                                                         ) else stringResource(
                    R.string.error_invalid_api_key_message
                                                                              ),
            actions = {
                ApiKeyStepHeader(
                        stepNumber = 1,
                        title = stringResource(
                                R.string.get_your_api_key
                                              )
                                )

                OutlinedButton(
                        onClick = onOpenTmdbSettingsClicked,
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(primaryButtonHeight)
                                .padding(bottom = 5.dp),
                        shape = RoundedCornerShape(primaryButtonCornerSize),
                        border = BorderStroke(
                                width = outlineButtonBorderStrokeWidth,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                             ),
                        colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface
                                                                    )
                              ) {
                    Image(
                            painter = painterResource(
                                    R.drawable.ic_open_in_new
                                                     ),
                            contentDescription = null,
                            modifier = Modifier
                                    .size(primaryButtonIconSize)
                                    .padding(horizontal = primaryButtonIconPadding)
                         )

                    Text(
                            text = stringResource(
                                    R.string.open_tmdb_api_settings
                                                 ),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                }

                TextButton(
                        onClick = onApiKeyHelpClicked,
                        contentPadding = PaddingValues(
                                start = linkTextStartEndTopPadding,
                                end = linkTextStartEndTopPadding,
                                top = linkTextStartEndTopPadding,
                                bottom = linkTextBottomPadding
                                                      )
                          ) {
                    Text(
                            text = stringResource(
                                    R.string.how_to_get_api_key
                                                 ),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyLarge,
                            textDecoration = TextDecoration.Underline
                        )
                }

                ApiKeyStepHeader(
                        stepNumber = 2,
                        title = stringResource(
                                R.string.enter_your_api_key
                                              )
                                )

                OutlinedTextField(
                        value = apiKey,
                        onValueChange = { newValue ->
                            apiKey = newValue
                        },
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = outlinedTextFieldPaddingBottom),
                        label = {
                            Text(
                                    text = stringResource(R.string.api_key)
                                )
                        },
                        placeholder = {
                            Text(
                                    text = stringResource(
                                            R.string.paste_your_api_key
                                                         )
                                )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.None,
                                autoCorrectEnabled = false
                                                         ),
                        shape = RoundedCornerShape(primaryButtonCornerSize),
                        colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.55f
                                                                          ),
                                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.55f
                                                                            ),
                                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.65f
                                                                      ),
                                cursorColor = MaterialTheme.colorScheme.onSurface,
                                focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.4f
                                                                       )
                                                                 )
                                 )

                PrimaryButton(
                        imageResId = null,
                        label = stringResource(
                                R.string.save_and_continue
                                              ),
                        onClick = { onSaveClicked(apiKey.trim()) },
                        isEnabled = apiKey.isNotBlank()
                             )
            },
            useSpaceBetween = false,
            tag = "InvalidApiKeyView"
             )
}

@Composable
private fun ApiKeyStepHeader(
        stepNumber: Int,
        title: String
                            ) {
    Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = apikeyStepHeaderPaddingBottom),
            verticalAlignment = Alignment.CenterVertically
       ) {
        Box(
                modifier = Modifier
                        .size(apikeyStepIndicatorSize)
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
                modifier = Modifier.padding(start = apiKeyStepHeaderTextPadding),
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
    }
}

@Composable
private fun ErrorActions(actions: @Composable (ColumnScope.() -> Unit)) {
    Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = actions
          )
}

@Composable
private fun ErrorTitleMessage(
        @DrawableRes errorImageResId: Int,
        errorTitle: String,
        errorMessage: String
                             ) {
    Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
          ) {
        Image(
                painter = painterResource(errorImageResId),
                contentDescription = null,
                modifier = Modifier
                        .height(errorImageHeight)
                        .aspectRatio(1f),
                contentScale = ContentScale.Fit
             )
        Text(
                modifier = Modifier.padding(top = errorTitlePaddingTop),
                text = errorTitle,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

        Text(
                modifier = Modifier.padding(top = errorMessagePaddingTop),
                text = errorMessage,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
    }
}

@Composable
fun GenericErrorScreen(errorMessageText:String, onTryAgainClicked: () -> Unit) {
    ErrorView(
            modifier = Modifier.fillMaxSize(),
            errorImageResId = R.drawable.ic_error,
            errorTitle = "Error",
            errorMessage = errorMessageText,
            {
                PrimaryButton(
                        imageResId = null,
                        label = "Try again",
                        {
                            onTryAgainClicked()
                        },
                        true
                             )
            },
            useSpaceBetween = true,
            tag = "GenericErrorView"
             )
}

/**
 * PREVIEWS
 */

@Composable
@Preview(showBackground = true)
private fun InvalidApiKeyViewPreview() {
    MMoviesTheme {
        InvalidApiKeyView(
                onOpenTmdbSettingsClicked = {
                    logDebug(
                            "InvalidApiKeyViewPreview",
                            "onOpenTmdbSettingsClicked"
                            )
                },
                onApiKeyHelpClicked = {
                    logDebug(
                            "InvalidApiKeyViewPreview",
                            "onApiKeyHelpClicked"
                            )
                },
                onSaveClicked = { key: String ->
                    logDebug(
                            "InvalidApiKeyViewPreview",
                            "onSaveClicked key"
                            )
                },
                false
                         )
    }
}


@Composable
@Preview(showBackground = true)
private fun MissingApiKeyViewPreview() {
    MMoviesTheme {
        InvalidApiKeyView(
                onOpenTmdbSettingsClicked = {
                    logDebug(
                            "MissingApiKeyViewPreview",
                            "onOpenTmdbSettingsClicked"
                            )
                },
                onApiKeyHelpClicked = {
                    logDebug(
                            "MissingApiKeyViewPreview",
                            "onApiKeyHelpClicked"
                            )
                },
                onSaveClicked = { key: String ->
                    logDebug(
                            "MissingApiKeyViewPreview",
                            "onSaveClicked key"
                            )
                },
                true
                         )
    }
}


@Composable
@Preview(showBackground = true)
private fun NoInternetViewPreview() {
    MMoviesTheme {
        NoInternetView {
            logDebug(
                    "NoInternetViewPreview",
                    "onSettingsButtonClicked"
                    )
        }
    }
}


@Composable
@Preview(showBackground = true)
private fun GenericErrorViewPreview() {
    MMoviesTheme {
        ErrorView(
                modifier = Modifier.fillMaxSize(),
                errorImageResId = R.drawable.ic_error,
                errorTitle = "Error",
                errorMessage = "Something went wrong..",
                {
                    PrimaryButton(
                            imageResId = null,
                            label = "Try again",
                            {
                                logDebug(
                                        "GenericErrorViewPreview",
                                        "Try again clicked"
                                        )
                            },
                            true
                                 )
                },
                useSpaceBetween = true,
                tag = "GenericErrorViewPreview"
                 )
    }
}