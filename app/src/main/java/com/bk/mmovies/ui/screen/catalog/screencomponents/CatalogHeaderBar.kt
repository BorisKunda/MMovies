package com.bk.mmovies.ui.screen.catalog.screencomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bk.mmovies.ui.screen.catalog.UserProfileUiState

// Matches CatalogSearchBar's own horizontal margin so the avatar's end edge
// lines up with the search capsule's end edge below it.
private val userSelectorEndPadding = 12.dp

// The gap from the status bar to the user avatar, now that CatalogScreen's
// own Scaffold no longer double-pads the top inset.
private val headerTopMargin = 5.dp

// Rendered above CatalogSearchBar (see CatalogScreen), aligned to the same
// end edge as the search capsule below it.
@Composable
fun UserProfileBar(
        userProfileState: UserProfileUiState,
        onLogout: () -> Unit
                  ) {
    Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = headerTopMargin),
            horizontalArrangement = Arrangement.End
       ) {
        // Full UserSelector chip (name + logout icon) is set aside for now
        // per your request — swap this back in to restore it, nothing about
        // it was removed.
        UserAvatarButton(
                name = userProfileState.name,
                imageUrl = userProfileState.imageUrl,
                isGuest = userProfileState.isGuest,
                onLogout = onLogout,
                modifier = Modifier.padding(
                        end = userSelectorEndPadding,
                        top = categorySelectorVerticalMargin,
                        bottom = categorySelectorVerticalMargin
                                           )
                         )
    }
}
