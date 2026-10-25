package com.bk.mmovies.demomobile.ui.screen.catalog.screencomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private val userSelectorEndPadding = 12.dp
private val headerTopMargin = 5.dp

@Composable
fun UserProfileBar(
        name: String,
        imageUrl: String,
        isGuest: Boolean,
        onClick: () -> Unit
                  ) {
    Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = headerTopMargin),
            horizontalArrangement = Arrangement.End
       ) {
        UserAvatarButton(
                name = name,
                imageUrl = imageUrl,
                isGuest = isGuest,
                onClick = onClick,
                modifier = Modifier.padding(
                        end = userSelectorEndPadding,
                        top = categorySelectorVerticalMargin,
                        bottom = categorySelectorVerticalMargin
                                           )
                         )
    }
}
