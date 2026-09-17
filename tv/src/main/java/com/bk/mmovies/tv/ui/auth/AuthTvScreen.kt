package com.bk.mmovies.tv.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bk.mmovies.core.R
import com.bk.mmovies.ui.component.PoweredByTmdbFooter
import com.bk.mmovies.ui.theme.MMoviesTheme

private val buttonHeight = 56.dp
private val buttonCornerSize = 16.dp
private val outlineButtonBorderStrokeWidth = 1.dp
private val authFieldSpacing = 20.dp
private val authDividerPaddingVertical = 28.dp
private val authDividerLabelPadding = 12.dp

@Composable
private fun authOutlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
        cursorColor = MaterialTheme.colorScheme.onSurface,
        focusedBorderColor = MaterialTheme.colorScheme.onSurface,
        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                                                            )

@Composable
fun AuthTvScreen(
        onLoginClicked: (username: String, password: String) -> Unit = { _, _ -> },
        onContinueAsGuestClicked: () -> Unit = {}
                ) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    val usernameFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val loginFocusRequester = remember { FocusRequester() }
    val guestFocusRequester = remember { FocusRequester() }
    val isLoginEnabled = username.isNotBlank() && password.isNotBlank()

    // OutlinedTextField consumes DPAD up/down itself (for cursor movement)
    // instead of letting focus traverse past it, which otherwise permanently
    // traps D-pad navigation on the first field. Intercept up/down here with
    // onPreviewKeyEvent (runs before the field sees the event) and drive
    // focus explicitly between the fixed stops on this screen.
    fun downKeyHandler(next: FocusRequester) = { event: androidx.compose.ui.input.key.KeyEvent ->
        if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionDown) {
            next.requestFocus()
            true
        } else false
    }

    fun upKeyHandler(previous: FocusRequester) = { event: androidx.compose.ui.input.key.KeyEvent ->
        if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionUp) {
            previous.requestFocus()
            true
        } else false
    }

    // A Column with the Row taking weight(1f) - not a Box overlaying the
    // footer at BottomCenter - so the footer always sits in the layout flow
    // below the Row's content instead of drawn on top of it. The Row's
    // vertically-centered columns then only ever center within whatever
    // height remains above the footer, which can never overlap them
    // regardless of how tall the content or the footer itself is.
    Column(
            modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
          ) {
        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(start = 54.dp, top = 54.dp, end = 54.dp)
           ) {
            Column(
                    modifier = Modifier
                            .weight(0.4f)
                            .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                  ) {
                Text(
                        text = stringResource(R.string.auth_title),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                Text(
                        text = stringResource(R.string.auth_subtitle),
                        modifier = Modifier.padding(top = 12.dp),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyLarge
                    )
            }

            Column(
                    modifier = Modifier
                            .weight(0.5f)
                            .fillMaxHeight()
                            .padding(start = 64.dp)
                            .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center
                  ) {
                OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = authFieldSpacing)
                                .focusRequester(usernameFocusRequester)
                                .onPreviewKeyEvent(downKeyHandler(passwordFocusRequester)),
                        placeholder = { Text(text = stringResource(R.string.username_label)) },
                        singleLine = true,
                        shape = RoundedCornerShape(buttonCornerSize),
                        colors = authOutlinedTextFieldColors()
                                 )

                OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = authFieldSpacing)
                                .focusRequester(passwordFocusRequester)
                                .onPreviewKeyEvent(upKeyHandler(usernameFocusRequester))
                                .onPreviewKeyEvent { event ->
                                    if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionDown) {
                                        if (isLoginEnabled) loginFocusRequester.requestFocus() else guestFocusRequester.requestFocus()
                                        true
                                    } else false
                                },
                        placeholder = { Text(text = stringResource(R.string.password_label)) },
                        singleLine = true,
                        visualTransformation = if (isPasswordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
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
                                                                           ),
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                            }
                        },
                        shape = RoundedCornerShape(buttonCornerSize),
                        colors = authOutlinedTextFieldColors()
                                 )

                OutlinedButton(
                        onClick = { onLoginClicked(username, password) },
                        enabled = isLoginEnabled,
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(buttonHeight)
                                .focusRequester(loginFocusRequester)
                                .onPreviewKeyEvent(upKeyHandler(passwordFocusRequester))
                                .onPreviewKeyEvent(downKeyHandler(guestFocusRequester)),
                        shape = RoundedCornerShape(buttonCornerSize),
                        border = BorderStroke(
                                width = outlineButtonBorderStrokeWidth,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                             ),
                        colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface,
                                disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                                                    )
                              ) {
                    Text(
                            text = stringResource(R.string.log_in),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                }

                AuthTvDivider()

                Text(
                        text = stringResource(R.string.no_tmdb_account_message),
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = authFieldSpacing),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium
                    )

                TextButton(
                        onClick = onContinueAsGuestClicked,
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(buttonHeight)
                                .focusRequester(guestFocusRequester)
                                .onPreviewKeyEvent { event ->
                                    if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionUp) {
                                        if (isLoginEnabled) loginFocusRequester.requestFocus() else passwordFocusRequester.requestFocus()
                                        true
                                    } else false
                                }
                          ) {
                    Text(
                            text = stringResource(R.string.continue_as_guest),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                }
            }
        }

        PoweredByTmdbFooter(modifier = Modifier.padding(vertical = 10.dp))
    }
}

@Composable
private fun AuthTvDivider() {
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

@Preview(device = "id:tv_1080p")
@Composable
private fun AuthTvScreenPreview() {
    MMoviesTheme {
        Scaffold(
                containerColor = MaterialTheme.colorScheme.background
                ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                AuthTvScreen()
            }
        }
    }
}
