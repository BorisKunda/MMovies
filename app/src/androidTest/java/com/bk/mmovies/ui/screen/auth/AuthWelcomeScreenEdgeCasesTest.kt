package com.bk.mmovies.ui.screen.auth

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import com.bk.mmovies.R
import com.bk.mmovies.ui.screen.auth.screencomponents.AuthWelcomeScreen
import com.bk.mmovies.ui.theme.MMoviesTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Edge cases for the login form, isolated from the ViewModel and network:
 * each test drives [AuthWelcomeScreen] directly with fake callbacks, the
 * same isolation pattern used for the search/catalog edge-case suites.
 */
class AuthWelcomeScreenEdgeCasesTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    private fun setAuthWelcomeScreen(
            onLoginClicked: (username: String, password: String) -> Unit = { _, _ -> },
            onRegisterOnTmdbClicked: () -> Unit = {},
            onContinueAsGuestClicked: () -> Unit = {},
            onNavigateToTerms: () -> Unit = {}
                                     ) {
        composeRule.setContent {
            MMoviesTheme {
                AuthWelcomeScreen(
                        onLoginClicked = onLoginClicked,
                        onRegisterOnTmdbClicked = onRegisterOnTmdbClicked,
                        onContinueAsGuestClicked = onContinueAsGuestClicked,
                        onNavigateToTerms = onNavigateToTerms
                                 )
            }
        }
    }

    @Test
    fun loginButton_disabledWhenBothFieldsBlank() {
        setAuthWelcomeScreen()

        composeRule.onNodeWithText(context.getString(R.string.log_in)).assertIsNotEnabled()
    }

    @Test
    fun loginButton_disabledWhenOnlyUsernameFilled() {
        setAuthWelcomeScreen()

        composeRule.onNodeWithText(context.getString(R.string.username_label)).performTextInput("boris")

        composeRule.onNodeWithText(context.getString(R.string.log_in)).assertIsNotEnabled()
    }

    @Test
    fun loginButton_disabledWhenOnlyPasswordFilled() {
        setAuthWelcomeScreen()

        composeRule.onNodeWithText(context.getString(R.string.password_label)).performTextInput("hunter2")

        composeRule.onNodeWithText(context.getString(R.string.log_in)).assertIsNotEnabled()
    }

    @Test
    fun loginButton_enabledWhenBothFieldsFilled_andClickPassesExactCredentials() {
        var loggedInUsername: String? = null
        var loggedInPassword: String? = null
        setAuthWelcomeScreen(onLoginClicked = { username, password ->
            loggedInUsername = username
            loggedInPassword = password
        })

        composeRule.onNodeWithText(context.getString(R.string.username_label)).performTextInput("boris")
        composeRule.onNodeWithText(context.getString(R.string.password_label)).performTextInput("hunter2")

        composeRule.onNodeWithText(context.getString(R.string.log_in))
                .assertIsEnabled()
                .performClick()

        assertEquals("boris", loggedInUsername)
        assertEquals("hunter2", loggedInPassword)
    }

    @Test
    fun passwordVisibilityToggle_defaultsToHiddenAndTogglesOnClick() {
        setAuthWelcomeScreen()

        // Masked by default: the "reveal" affordance is offered, not the
        // "hide" one.
        composeRule.onNodeWithContentDescription(context.getString(R.string.show_password)).assertIsDisplayed()

        composeRule.onNodeWithContentDescription(context.getString(R.string.show_password)).performClick()

        composeRule.onNodeWithContentDescription(context.getString(R.string.hide_password)).assertIsDisplayed()
    }

    @Test
    fun registerOnTmdbButton_tapInvokesCallback() {
        var registerClicked = false
        setAuthWelcomeScreen(onRegisterOnTmdbClicked = { registerClicked = true })

        composeRule.onNodeWithText(context.getString(R.string.register_on_tmdb)).performClick()

        assertTrue("Register-on-TMDB callback should fire on tap", registerClicked)
    }

    @Test
    fun continueAsGuestButton_tapInvokesCallback() {
        var guestClicked = false
        setAuthWelcomeScreen(onContinueAsGuestClicked = { guestClicked = true })

        composeRule.onNodeWithText(context.getString(R.string.continue_as_guest)).performClick()

        assertTrue("Continue-as-guest callback should fire on tap", guestClicked)
    }

    @Test
    fun poweredByFooter_tapInvokesOnNavigateToTerms() {
        var termsClicked = false
        setAuthWelcomeScreen(onNavigateToTerms = { termsClicked = true })

        composeRule.onNodeWithText(context.getString(R.string.powered_by_tmdb)).performClick()

        assertTrue("Tapping the TMDB footer should navigate to terms", termsClicked)
    }
}
