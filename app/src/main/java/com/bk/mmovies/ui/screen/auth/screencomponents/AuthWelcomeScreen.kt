package com.bk.mmovies.ui.screen.auth.screencomponents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bk.mmovies.R
import com.bk.mmovies.ui.component.PoweredByTmdbFooter
import com.bk.mmovies.ui.theme.primaryButtonCornerSize
import com.bk.mmovies.ui.theme.primaryButtonHeight
import com.bk.mmovies.ui.theme.primaryButtonIconPadding
import com.bk.mmovies.ui.theme.primaryButtonIconSize

private val authScreenPaddingHorizontal = 28.dp
private val authScreenPaddingTop = 40.dp
private val authScreenPaddingBottom = 88.dp
private val authTitlePaddingBottom = 12.dp
private val authSubtitlePaddingBottom = 28.dp
private val authFieldSpacing = 20.dp
private val authDividerPaddingVertical = 28.dp
private val authDividerLabelPadding = 12.dp
private val outlineButtonBorderStrokeWidth = 1.dp
private val authContinueAsGuestPaddingBottom = 4.dp
private val authTextFieldFontSize = 16.sp

@Composable
private fun authOutlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
        focusedLabelColor = MaterialTheme.colorScheme.onSurface,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
        cursorColor = MaterialTheme.colorScheme.onSurface,
        focusedBorderColor = MaterialTheme.colorScheme.onSurface,
        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                                                            )

@Composable
fun AuthWelcomeScreen(
        onLoginClicked: (username: String, password: String) -> Unit,
        onRegisterOnTmdbClicked: () -> Unit,
        onContinueAsGuestClicked: () -> Unit,
        onNavigateToTerms: () -> Unit = {}
                             ) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    Box(
            modifier = Modifier.fillMaxSize()
       ) {
    Column(
            modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(
                            start = authScreenPaddingHorizontal,
                            top = authScreenPaddingTop,
                            end = authScreenPaddingHorizontal,
                            bottom = authScreenPaddingBottom
                            ),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
        Text(
                text = stringResource(R.string.auth_title),
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = authTitlePaddingBottom),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )

        Text(
                text = stringResource(R.string.auth_subtitle),
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = authSubtitlePaddingBottom),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyLarge
            )

        OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = authFieldSpacing),
                label = { Text(text = stringResource(R.string.username_label)) },
                placeholder = { Text(text = stringResource(R.string.username_placeholder)) },
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = authTextFieldFontSize),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrectEnabled = false
                                                  ),
                shape = RoundedCornerShape(primaryButtonCornerSize),
                colors = authOutlinedTextFieldColors()
                         )

        OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = authFieldSpacing),
                label = { Text(text = stringResource(R.string.password_label)) },
                placeholder = { Text(text = stringResource(R.string.password_placeholder)) },
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = authTextFieldFontSize),
                singleLine = true,
                visualTransformation = if (isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Password
                                                  ),
                shape = RoundedCornerShape(primaryButtonCornerSize),
                colors = authOutlinedTextFieldColors(),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                                imageVector = if (isPasswordVisible) {
                                    Icons.Filled.VisibilityOff
                                } else {
                                    Icons.Filled.Visibility
                                },
                                contentDescription = stringResource(
                                        if (isPasswordVisible) {
                                            R.string.hide_password
                                        } else {
                                            R.string.show_password
                                        }
                                                                    )
                            )
                    }
                }
                         )

        OutlinedButton(
                onClick = {
                    onLoginClicked(username, password)
                },
                enabled = username.isNotBlank() && password.isNotBlank(),
                modifier = Modifier
                        .fillMaxWidth()
                        .height(primaryButtonHeight),
                shape = RoundedCornerShape(primaryButtonCornerSize),
                border = BorderStroke(
                        width = outlineButtonBorderStrokeWidth,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                     ),
                colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                                                            )
                      ) {
            Text(
                    text = stringResource(R.string.log_in),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
        }

        AuthDivider()

        Text(
                text = stringResource(R.string.no_tmdb_account_message),
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = authFieldSpacing),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium
            )

        OutlinedButton(
                onClick = onRegisterOnTmdbClicked,
                modifier = Modifier
                        .fillMaxWidth()
                        .height(primaryButtonHeight),
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
                    painter = painterResource(R.drawable.ic_open_in_new),
                    contentDescription = null,
                    modifier = Modifier
                            .size(primaryButtonIconSize)
                            .padding(horizontal = primaryButtonIconPadding)
                 )

            Text(
                    text = stringResource(R.string.register_on_tmdb),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
        }

        TextButton(
                onClick = onContinueAsGuestClicked,
                modifier = Modifier
                        .fillMaxWidth()
                        .height(primaryButtonHeight)
                        .padding(
                                top = authFieldSpacing,
                                bottom = authContinueAsGuestPaddingBottom
                                )
                  ) {
            Text(
                    text = stringResource(R.string.continue_as_guest),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
        }
    }

        PoweredByTmdbFooter(
                modifier = Modifier.align(Alignment.BottomCenter),
                onClick = onNavigateToTerms
                           )
    }
}

@Composable
private fun AuthDivider() {
    Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = authDividerPaddingVertical),
            verticalAlignment = Alignment.CenterVertically
       ) {
        HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                         )
        Text(
                text = stringResource(R.string.auth_divider_or),
                modifier = Modifier.padding(horizontal = authDividerLabelPadding),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodyLarge
            )
        HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                         )
    }
}
