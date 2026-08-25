package com.bk.mmovies.ui.screen.catalog.screencomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bk.mmovies.domain.model.Category
import com.bk.mmovies.ui.screen.catalog.UserProfileUiState

// Mirrors CategorySelector's own start padding so UserSelector sits as far
// from the end edge as CategorySelector sits from the start edge.
private val userSelectorEndPadding = 28.dp

private val headerTopMargin = 12.dp

// Fixed above the tab content (see MoviesScreen) so switching between the
// Movies and TV Series tabs never resets the selected category or user.
@Composable
fun CatalogHeaderBar(
        selectedCategory: Category,
        userProfileState: UserProfileUiState,
        onCategoryClick: () -> Unit,
        onLogout: () -> Unit
                   ) {
    Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = headerTopMargin),
            horizontalArrangement = Arrangement.SpaceBetween,
            // Both chips apply the same top/bottom margin around their own
            // content, so centering their bounding boxes centers their content.
            verticalAlignment = Alignment.CenterVertically
       ) {
        CategorySelector(
                selectedCategory = selectedCategory,
                onClick = onCategoryClick
                        )

        UserSelector(
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
