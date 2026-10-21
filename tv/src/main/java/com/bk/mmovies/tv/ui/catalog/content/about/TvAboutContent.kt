package com.bk.mmovies.tv.ui.catalog.content.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bk.mmovies.tv.R
import com.bk.mmovies.tv.theme.tvContentTextStyle
import com.bk.mmovies.tv.theme.tvHeaderTextStyle


private val sectionSpacing = 24.dp
private val headingBodySpacing = 8.dp
private val headingLetterSpacing = 1.sp
private const val SECONDARY_TEXT_ALPHA = 0.8f

private data class LegalSection(
        val heading: Int,
        val body: Int
                               )

private val termsSections = listOf(
        LegalSection(
                R.string.terms_title,
                R.string.terms_intro
                    ),
        LegalSection(
                R.string.terms_tmdb_attribution_heading,
                R.string.terms_tmdb_attribution_body
                    ),
        LegalSection(
                R.string.terms_content_ownership_heading,
                R.string.terms_content_ownership_body
                    ),
        LegalSection(
                R.string.terms_guardian_attribution_heading,
                R.string.terms_guardian_attribution_body
                    ),
        LegalSection(
                R.string.terms_gemini_attribution_heading,
                R.string.terms_gemini_attribution_body
                    ),
        LegalSection(
                R.string.terms_voice_search_heading,
                R.string.terms_voice_search_body
                    ),
        LegalSection(
                R.string.terms_data_accuracy_heading,
                R.string.terms_data_accuracy_body
                    ),
        LegalSection(
                R.string.terms_no_warranty_heading,
                R.string.terms_no_warranty_body
                    ),
        LegalSection(
                R.string.terms_commercial_use_heading,
                R.string.terms_commercial_use_body
                    ),
        LegalSection(
                R.string.terms_changes_heading,
                R.string.terms_changes_body
                    )
                                  )

private val privacyPolicySections = listOf(
        LegalSection(
                R.string.privacy_policy_title,
                R.string.privacy_policy_intro
                    ),
        LegalSection(
                R.string.privacy_policy_account_heading,
                R.string.privacy_policy_account_body
                    ),
        LegalSection(
                R.string.privacy_policy_local_storage_heading,
                R.string.privacy_policy_local_storage_body
                    ),
        LegalSection(
                R.string.privacy_policy_voice_search_heading,
                R.string.privacy_policy_voice_search_body
                    ),
        LegalSection(
                R.string.privacy_policy_third_party_heading,
                R.string.privacy_policy_third_party_body
                    ),
        LegalSection(
                R.string.privacy_policy_no_collection_heading,
                R.string.privacy_policy_no_collection_body
                    ),
        LegalSection(
                R.string.privacy_policy_children_heading,
                R.string.privacy_policy_children_body
                    ),
        LegalSection(
                R.string.privacy_policy_changes_heading,
                R.string.privacy_policy_changes_body
                    )
                                          )

@Composable
fun TvAboutContent() {
    Column(verticalArrangement = Arrangement.spacedBy(sectionSpacing)) {
        termsSections.forEach { section -> TvLegalSectionContent(section) }
        privacyPolicySections.forEach { section -> TvLegalSectionContent(section) }
    }
}

@Composable
private fun TvLegalSectionContent(section: LegalSection) {
    Column {
        Text(
                text = stringResource(section.heading),
                color = MaterialTheme.colorScheme.onBackground,
                style = tvHeaderTextStyle,
                fontWeight = FontWeight.Bold,
                letterSpacing = headingLetterSpacing
            )
        Text(
                text = stringResource(section.body),
                modifier = Modifier.padding(top = headingBodySpacing),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = SECONDARY_TEXT_ALPHA),
                style = tvContentTextStyle
            )
    }
}
