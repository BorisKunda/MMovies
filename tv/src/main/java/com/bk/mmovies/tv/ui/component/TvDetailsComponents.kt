package com.bk.mmovies.tv.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bk.mmovies.tv.R
import com.bk.mmovies.tv.ui.details.FAVORITE_TAP_COOLDOWN_MS
import kotlinx.coroutines.delay

/** A single genre pill, mirroring app's GenreChip. */
@Composable
fun TvGenreChip(text: String) {
    Box(
            modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
       ) {
        Text(
                text = text,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
    }
}

/** An icon + text row, mirroring app's MetaRow (release date, runtime, etc). */
@Composable
fun TvMetaRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, modifier: Modifier = Modifier) {
    if (text.isBlank()) return
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                modifier = Modifier.width(18.dp)
            )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
                text = text,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyMedium
            )
    }
}

/**
 * Wraps a favorite click so presses (including a held remote button's repeats)
 * are ignored for FAVORITE_TAP_COOLDOWN_MS after an accepted one. Returns
 * whether the cooldown is active (for dimming) and the guarded click. The
 * button stays enabled - disabling it would drop D-pad focus.
 */
@Composable
fun rememberFavoriteTapCooldown(onClick: () -> Unit): Pair<Boolean, () -> Unit> {
    var cooling by remember { mutableStateOf(false) }
    val latestOnClick by rememberUpdatedState(onClick)
    LaunchedEffect(cooling) {
        if (cooling) {
            delay(FAVORITE_TAP_COOLDOWN_MS)
            cooling = false
        }
    }
    return cooling to {
        if (!cooling) {
            cooling = true
            latestOnClick()
        }
    }
}

/** D-pad focusable favorite toggle, mirroring app's FavoriteStarButton. */
@Composable
fun TvFavoriteStarButton(isFavorite: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val (cooling, guardedClick) = rememberFavoriteTapCooldown(onClick)
    IconButton(
            onClick = guardedClick,
            modifier = modifier
                    .alpha(if (cooling) 0.5f else 1f)
                    .clip(RoundedCornerShape(50))
                    .background(Color.Black.copy(alpha = 0.5f))
              ) {
        Icon(
                imageVector = if (isFavorite) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = stringResource(R.string.favorite_button),
                tint = Color.White
            )
    }
}

/** A single cast/crew member card used in a details screen's cast row. */
@Composable
fun TvCastMemberItem(
        name: String,
        character: String,
        profileUrl: String,
        modifier: Modifier = Modifier,
        focusRequester: FocusRequester? = null,
        onClick: (() -> Unit)? = null
                     ) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Column(modifier = modifier.width(110.dp)) {
        Box(
                modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .then(
                                if (isFocused) {
                                    Modifier.border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                                } else {
                                    Modifier
                                }
                             )
                        .let { if (focusRequester != null) it.focusRequester(focusRequester) else it }
                        .then(
                                if (onClick != null) {
                                    Modifier.clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
                                } else {
                                    Modifier.focusable(interactionSource = interactionSource)
                                }
                             )
           ) {
            if (profileUrl.isNotBlank()) {
                coil3.compose.AsyncImage(
                        model = profileUrl,
                        contentDescription = name,
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                                        )
            }
        }
        Text(
                text = name,
                modifier = Modifier.padding(top = 6.dp),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        if (character.isNotBlank()) {
            Text(
                    text = character,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
        }
    }
}

/** D-pad focusable primary action button used across the TV details screens. */
@Composable
fun TvDetailsActionButton(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
                          ) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    Button(
            onClick = onClick,
            interactionSource = interactionSource,
            modifier = modifier.then(
                    if (isFocused) Modifier.background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            RoundedCornerShape(10.dp)
                                                       ) else Modifier
                                     )
          ) {
        Text(text = text, fontWeight = FontWeight.Bold)
    }
}

/** Full-screen error state with a retry action, mirroring app's GenericErrorScreen. */
@Composable
fun TvGenericErrorScreen(message: String, onRetryClicked: () -> Unit, modifier: Modifier = Modifier) {
    Column(
            modifier = modifier
                    .fillMaxSize()
                    .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
        Icon(
                imageVector = Icons.Filled.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.width(56.dp)
            )
        Text(
                text = message,
                modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        TvDetailsActionButton(text = stringResource(R.string.retry_action), onClick = onRetryClicked)
    }
}
