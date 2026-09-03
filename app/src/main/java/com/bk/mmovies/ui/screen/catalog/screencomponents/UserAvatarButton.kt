package com.bk.mmovies.ui.screen.catalog.screencomponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.bk.mmovies.R

private val profileAvatarSize = 20.dp

// Mirrors CategorySelector's chip in MoviesScreen (corner shape, background/
// border alpha, content padding) so the two top-bar controls read as one UI.
private val userSelectorChipCornerShape = 18.dp
private val userSelectorChipBorderWidth = 1.dp
private val userSelectorChipContentHorizontalPadding = 12.dp
private val userSelectorChipContentVerticalPadding = 6.dp
private val userSelectorNameStartPadding = 6.dp
private val userSelectorIconStartPadding = 6.dp

private const val USER_SELECTOR_CHIP_CONTAINER_ALPHA = 0.06f
private const val USER_SELECTOR_CHIP_BORDER_ALPHA = 0.18f



// Just the avatar — no name/logout icon — at 2x the chip's usual avatar
// size. Set aside for now in favor of UserSelector's fulleryo chip, but kept
// working (still clickable, still opens the log in/out action) rather than
// stripped down, so switching back is a one-line change at the call site.
private val userAvatarButtonSize = profileAvatarSize * 2
private val logoutMenuItemVerticalPadding = 2.dp
private val logoutMenuItemHorizontalPadding = 16.dp

// DropdownMenu's default position (below-start of its anchor) would land
// this menu right on top of the search capsule directly under the avatar —
// dismissing it by tapping "outside" could then double as a tap into
// search. Shifting it left and back up to the avatar's own vertical level
// keeps it clear of that row entirely.
private val logoutMenuHorizontalOffset = (-100).dp
private val logoutMenuVerticalOffset = -userAvatarButtonSize

@Composable
fun UserAvatarButton(
        name: String,
        imageUrl: String,
        isGuest: Boolean,
        onLogout: () -> Unit,
        modifier: Modifier = Modifier
                     ) {
    val displayName = if (isGuest) stringResource(R.string.guest_label) else name
    val actionLabel = stringResource(if (isGuest) R.string.log_in_action else R.string.log_out)
    // Logging out still requires picking "Log out" from the menu below (not
    // an instant action), so a plain tap opening it isn't an accidental-logout
    // trap — and a tap is what users actually try on a profile avatar. A
    // long-press-only trigger tested as effectively unresponsive: nothing on
    // screen hints that a normal tap won't do anything.
    var isMenuExpanded by remember { mutableStateOf(false) }

    Box(
            modifier = modifier
                    .minimumInteractiveComponentSize()
                    .clip(CircleShape)
                    .clickable(
                            role = Role.Button,
                            onClickLabel = actionLabel,
                            onClick = { isMenuExpanded = true }
                              )
                    .semantics(mergeDescendants = true) {
                        contentDescription = "$displayName. $actionLabel"
                    }
       ) {
        UserAvatar(name = name, imageUrl = imageUrl, isGuest = isGuest, size = userAvatarButtonSize)

        DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = { isMenuExpanded = false },
                offset = DpOffset(x = logoutMenuHorizontalOffset, y = logoutMenuVerticalOffset)
                    ) {
            // DropdownMenuItem forces a 48dp minimum touch-target height —
            // too tall for a single short label — so this is a plain
            // clickable Text instead, sized to just its own text + 2dp
            // padding on every side.
            Text(
                    text = actionLabel,
                    modifier = Modifier
                            .clickable(role = Role.Button) {
                                isMenuExpanded = false
                                onLogout()
                            }
                            .padding(
                                    horizontal = logoutMenuItemHorizontalPadding,
                                    vertical = logoutMenuItemVerticalPadding
                                    )
                )
        }
    }
}

@Composable
private fun UserAvatar(name: String, imageUrl: String, isGuest: Boolean, size: Dp) {
    if (isGuest) {
        Image(
                painter = painterResource(R.drawable.ic_guest),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                        .size(size)
                        .clip(CircleShape)
             )
    } else if (imageUrl.isBlank()) {
        InitialsAvatar(name = name, size = size)
    } else {
        SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                        .size(size)
                        .clip(CircleShape),
                loading = { InitialsAvatar(name = name, size = size) },
                // A path from the API doesn't guarantee the image still
                // exists (TMDB avatar removed, network blip, etc).
                error = { InitialsAvatar(name = name, size = size) }
                             )
    }
}

// Matches CategorySelector's chip background/border in MoviesScreen so the
// avatar reads as part of the same UI.
private const val INITIALS_AVATAR_BACKGROUND_ALPHA = 0.06f
private const val INITIALS_AVATAR_BORDER_ALPHA = 0.18f
private val initialsAvatarBorderWidth = 1.dp

@Composable
private fun InitialsAvatar(
        name: String,
        modifier: Modifier = Modifier,
        size: Dp = profileAvatarSize
                          ) {
    val initial = name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    // Keeps the glyph at the same proportion of the circle regardless of
    // which call site's size (chip-sized or the 2x avatar-only button) is
    // in play.
    val fontSize = size.value * 0.5f
    Box(
            modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(
                            MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = INITIALS_AVATAR_BACKGROUND_ALPHA
                                                                    )
                               )
                    .border(
                            width = initialsAvatarBorderWidth,
                            color = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = INITIALS_AVATAR_BORDER_ALPHA
                                                                            ),
                            shape = CircleShape
                           ),
            contentAlignment = Alignment.Center
       ) {
        Text(
                text = initial,
                color = Color.White,
                // lineHeight must match fontSize too — titleMedium's default
                // 24sp line height otherwise makes Compose center on a box far
                // taller than the glyph, throwing off the visual center.
                style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = fontSize.sp,
                        lineHeight = fontSize.sp
                                                                  ),
                fontWeight = FontWeight.Bold
            )
    }
}
