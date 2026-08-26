package com.bk.mmovies.ui.screen.privacypolicy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bk.mmovies.R
import com.bk.mmovies.ui.theme.MMoviesTheme

private val screenPadding = 16.dp
private val sectionSpacing = 24.dp
private val headingBodySpacing = 8.dp
private val introBottomSpacing = 8.dp
private val headingLetterSpacing = 1.sp
private const val SECONDARY_TEXT_ALPHA = 0.85f

private data class PrivacyPolicySection(val heading: Int, val body: Int)

private val privacyPolicySections = listOf(
        PrivacyPolicySection(R.string.privacy_policy_account_heading, R.string.privacy_policy_account_body),
        PrivacyPolicySection(R.string.privacy_policy_local_storage_heading, R.string.privacy_policy_local_storage_body),
        PrivacyPolicySection(R.string.privacy_policy_third_party_heading, R.string.privacy_policy_third_party_body),
        PrivacyPolicySection(R.string.privacy_policy_no_collection_heading, R.string.privacy_policy_no_collection_body),
        PrivacyPolicySection(R.string.privacy_policy_children_heading, R.string.privacy_policy_children_body),
        PrivacyPolicySection(R.string.privacy_policy_changes_heading, R.string.privacy_policy_changes_body)
                                            )

@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        PrivacyPolicyTopBar(onBack = onBack)

        Column(
                modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(screenPadding),
                verticalArrangement = Arrangement.spacedBy(sectionSpacing)
              ) {
            Text(
                    text = stringResource(R.string.privacy_policy_intro),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = introBottomSpacing)
                )

            privacyPolicySections.forEach { section ->
                PrivacyPolicySectionContent(section)
            }
        }
    }
}

@Composable
private fun PrivacyPolicySectionContent(section: PrivacyPolicySection) {
    Column {
        Text(
                text = stringResource(section.heading),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = headingLetterSpacing
            )
        Text(
                text = stringResource(section.body),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = SECONDARY_TEXT_ALPHA),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = headingBodySpacing)
            )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrivacyPolicyTopBar(onBack: () -> Unit) {
    TopAppBar(
            title = {
                Text(
                        text = stringResource(R.string.privacy_policy_title),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium
                    )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.details_back),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                                                      )
             )
}

@Preview
@Composable
private fun PrivacyPolicyScreenPreview() {
    MMoviesTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                PrivacyPolicyScreen(onBack = {})
            }
        }
    }
}
