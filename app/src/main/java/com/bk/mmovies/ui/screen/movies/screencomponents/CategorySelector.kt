package com.bk.mmovies.ui.screen.movies.screencomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bk.mmovies.R
import com.bk.mmovies.domain.model.MovieCategory

private val categoryIconEndPadding = 6.dp
private val categorySelectorCornerShape = 18.dp
private val categorySelectorBorderWidth = 1.dp

// Also used by MoviesScreen to give UserSelector the same top/bottom margin,
// so the two top-bar chips' content centers line up.
val categorySelectorVerticalMargin = 12.dp

private val categorySelectorContentHorizontalPadding = 12.dp
private val categorySelectorContentVerticalPadding = 6.dp
private val categoryIconSize = 16.dp
private val categoryArrowSize = 18.dp
private val categoryArrowStartPadding = 4.dp

// Matches the poster's start margin in MovieRow: cardPaddingHorizontal (12dp) + rowPaddingHorizontal (16dp).
private val categorySelectorStartPadding = 28.dp

private const val CATEGORY_SELECTOR_CONTAINER_ALPHA = 0.06f
private const val CATEGORY_SELECTOR_BORDER_ALPHA = 0.18f
private const val CATEGORY_SELECTOR_ARROW_ALPHA = 0.7f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelector(
        selectedCategory: MovieCategory,
        onClick: () -> Unit
                     ) {
    val categorySelectorShape = RoundedCornerShape(categorySelectorCornerShape)
    val categoryLabel = stringResource(selectedCategory.labelRes)
    // Without this the chip announces only "Popular" — no hint that it is a
    // control, or that activating it opens the category picker.
    val selectorDescription = stringResource(
            R.string.category_selector_description,
            categoryLabel
                                            )

    Row(
            modifier = Modifier
                    .padding(
                            start = categorySelectorStartPadding,
                            top = categorySelectorVerticalMargin,
                            bottom = categorySelectorVerticalMargin
                            )
                    // Keeps the chip visually small (as designed) while giving
                    // it the 48dp minimum touch target.
                    .minimumInteractiveComponentSize()
                    .clip(categorySelectorShape)
                    .background(
                            MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = CATEGORY_SELECTOR_CONTAINER_ALPHA
                                                                    )
                               )
                    .border(
                            width = categorySelectorBorderWidth,
                            color = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = CATEGORY_SELECTOR_BORDER_ALPHA
                                                                            ),
                            shape = categorySelectorShape
                           )
                    .clickable(
                            role = Role.Button,
                            onClickLabel = stringResource(R.string.category_selector_action)
                              ) { onClick() }
                    .padding(
                            horizontal = categorySelectorContentHorizontalPadding,
                            vertical = categorySelectorContentVerticalPadding
                            )
                    // Merged into one node so TalkBack reads the whole chip as
                    // a single control rather than icon / label / arrow.
                    .semantics(mergeDescendants = true) {
                        contentDescription = selectorDescription
                    },
            verticalAlignment = Alignment.CenterVertically
       ) {
        Icon(
                painter = painterResource(selectedCategory.drawableRes),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(categoryIconSize)
            )
        Spacer(modifier = Modifier.width(categoryIconEndPadding))
        Text(
                text = categoryLabel,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        Icon(
                painter = painterResource(R.drawable.ic_categories_arrow_drop_down),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = CATEGORY_SELECTOR_ARROW_ALPHA
                                                               ),
                modifier = Modifier
                        .padding(start = categoryArrowStartPadding)
                        .size(categoryArrowSize)
            )
    }
}
