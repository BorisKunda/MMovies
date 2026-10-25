package com.bk.mmovies.demotv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.focus.FocusRequester
import androidx.navigation.compose.rememberNavController
import com.bk.mmovies.demotv.theme.MMoviesTvTheme
import com.bk.mmovies.demotv.ui.navigation.TvNavigation

class TvMainActivity : ComponentActivity() {
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
