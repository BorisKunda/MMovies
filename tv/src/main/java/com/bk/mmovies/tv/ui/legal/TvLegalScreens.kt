package com.bk.mmovies.tv.ui.legal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bk.mmovies.tv.R

private val screenPadding = 40.dp
private val sectionSpacing = 24.dp
private val headingBodySpacing = 8.dp
private val introBottomSpacing = 8.dp
private val headingLetterSpacing = 1.sp
private const val SECONDARY_TEXT_ALPHA = 0.8f

private data class LegalSection(val heading: Int, val body: Int)

private val termsSections = listOf(
        LegalSection(R.string.terms_tmdb_attribution_heading, R.string.terms_tmdb_attribution_body),
        LegalSection(R.string.terms_content_ownership_heading, R.string.terms_content_ownership_body),
        LegalSection(R.string.terms_guardian_attribution_heading, R.string.terms_guardian_attribution_body),
        LegalSection(R.string.terms_gemini_attribution_heading, R.string.terms_gemini_attribution_body),
        LegalSection(R.string.terms_data_accuracy_heading, R.string.terms_data_accuracy_body),
        LegalSection(R.string.terms_no_warranty_heading, R.string.terms_no_warranty_body),
        LegalSection(R.string.terms_commercial_use_heading, R.string.terms_commercial_use_body),
        LegalSection(R.string.terms_changes_heading, R.string.terms_changes_body)
                                   )

private val privacyPolicySections = listOf(
        LegalSection(R.string.privacy_policy_account_heading, R.string.privacy_policy_account_body),
        LegalSection(R.string.privacy_policy_local_storage_heading, R.string.privacy_policy_local_storage_body),
        LegalSection(R.string.privacy_policy_third_party_heading, R.string.privacy_policy_third_party_body),
        LegalSection(R.string.privacy_policy_no_collection_heading, R.string.privacy_policy_no_collection_body),
        LegalSection(R.string.privacy_policy_children_heading, R.string.privacy_policy_children_body),
        LegalSection(R.string.privacy_policy_changes_heading, R.string.privacy_policy_changes_body)
                                            )

/** In-app Terms &amp; Conditions screen for TV - avoids relying on an external browser, which many Google TV devices don't have. */
@Composable
fun TvTermsScreen(onBack: () -> Unit, onNavigateToPrivacyPolicy: () -> Unit = {}) {
    TvLegalScreenScaffold(title = stringResource(R.string.terms_title), onBack = onBack) {
        Text(
                text = stringResource(R.string.terms_intro),
                modifier = Modifier.padding(bottom = introBottomSpacing),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = SECONDARY_TEXT_ALPHA),
                style = MaterialTheme.typography.bodyLarge
            )

        termsSections.forEach { section -> TvLegalSectionContent(section) }

        val interactionSource = remember { MutableInteractionSource() }
        val isFocused by interactionSource.collectIsFocusedAsState()
        Text(
                text = stringResource(R.string.terms_privacy_policy_link),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                                if (isFocused) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.14f) else Color.Transparent
                                   )
                        .clickable(
                                interactionSource = interactionSource,
                                role = Role.Button,
                                onClick = onNavigateToPrivacyPolicy
                                  )
                        .padding(horizontal = 4.dp, vertical = 6.dp)
            )
    }
}

/** In-app Privacy Policy screen for TV, mirroring the mobile app's version. */
@Composable
fun TvPrivacyPolicyScreen(onBack: () -> Unit) {
    TvLegalScreenScaffold(title = stringResource(R.string.privacy_policy_title), onBack = onBack) {
        Text(
                text = stringResource(R.string.privacy_policy_intro),
                modifier = Modifier.padding(bottom = introBottomSpacing),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = SECONDARY_TEXT_ALPHA),
                style = MaterialTheme.typography.bodyLarge
            )

        privacyPolicySections.forEach { section -> TvLegalSectionContent(section) }
    }
}

/**
 * Terms &amp; Conditions and Privacy Policy combined into one scrollable
 * block, for the nav rail's "About" destination - unlike [TvTermsScreen]/
 * [TvPrivacyPolicyScreen] this has no scaffold of its own (no back button,
 * no scroll container): it's meant to be dropped into an existing
 * scrollable container (the catalog screen's LazyColumn) alongside its own
 * fixed provider footer below.
 */
@Composable
fun TvAboutContent() {
    Column(verticalArrangement = Arrangement.spacedBy(sectionSpacing)) {
        Text(
                text = stringResource(R.string.terms_title),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        Text(
                text = stringResource(R.string.terms_intro),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = SECONDARY_TEXT_ALPHA),
                style = MaterialTheme.typography.bodyLarge
            )
        termsSections.forEach { section -> TvLegalSectionContent(section) }

        Text(
                text = stringResource(R.string.privacy_policy_title),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        Text(
                text = stringResource(R.string.privacy_policy_intro),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = SECONDARY_TEXT_ALPHA),
                style = MaterialTheme.typography.bodyLarge
            )
        privacyPolicySections.forEach { section -> TvLegalSectionContent(section) }
    }
}

@Composable
private fun TvLegalScreenScaffold(title: String, onBack: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Box(
            modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
       ) {
        Column(
                modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = screenPadding, vertical = 100.dp),
                verticalArrangement = Arrangement.spacedBy(sectionSpacing)
              ) {
            Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

            content()
        }

        val backFocusRequester = remember { FocusRequester() }
        LaunchedEffect(Unit) { backFocusRequester.requestFocus() }
        IconButton(
                onClick = onBack,
                modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                        .focusRequester(backFocusRequester)
                  ) {
            Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_action),
                    tint = MaterialTheme.colorScheme.onSurface
                )
        }
    }
}

@Composable
private fun TvLegalSectionContent(section: LegalSection) {
    Column {
        Text(
                text = stringResource(section.heading),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = headingLetterSpacing
            )
        Text(
                text = stringResource(section.body),
                modifier = Modifier.padding(top = headingBodySpacing),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = SECONDARY_TEXT_ALPHA),
                style = MaterialTheme.typography.bodyLarge
            )
    }
}
