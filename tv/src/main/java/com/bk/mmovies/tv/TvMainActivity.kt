package com.bk.mmovies.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.focus.FocusRequester
import androidx.navigation.compose.rememberNavController
import com.bk.mmovies.tv.theme.MMoviesTvTheme
import com.bk.mmovies.tv.ui.navigation.TvNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TvMainActivity : ComponentActivity() {
    // Shared with CatalogTvScreen purely so its nav rail can reuse one
    // stable FocusRequester instance for the Movies item across
    // recompositions (see its own navItemFocusRequesters comment) - this
    // Activity itself no longer calls requestFocus() on it. It used to
    // (from onWindowFocusChanged, to dodge a "window doesn't have input
    // focus yet" race), back when the catalog screen was the very first
    // thing shown. Now that Splash/Auth always precede it, that call was
    // firing before the catalog (and this requester's target nav item) had
    // ever composed, so it reliably no-op'd with a "FocusRequester is not
    // initialized" warning on every single cold launch - CatalogTvScreen's
    // own retry-hardened LaunchedEffect is what actually grabs focus once
    // the screen genuinely appears, so the Activity-level attempt was pure
    // guaranteed-to-warn dead weight and was removed.
    private val firstItemFocusRequester = FocusRequester()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MMoviesTvTheme {
                val navController = rememberNavController()
                TvNavigation(
                        navController = navController,
                        firstItemFocusRequester = firstItemFocusRequester,
                        onAppExit = { finish() }
                            )
            }
        }
    }
}
