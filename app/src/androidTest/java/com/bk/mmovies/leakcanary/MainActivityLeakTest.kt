package com.bk.mmovies.leakcanary

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.bk.mmovies.ui.activity.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import leakcanary.DetectLeaksAfterTestSuccess
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class MainActivityLeakTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val leakRule = DetectLeaksAfterTestSuccess()

    @get:Rule(order = 2)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun triggerLeak() {
        // Activity launches, assigns itself to the static field, then this test ends
        // and the Activity gets destroyed — the rule checks for leaks right after.
    }
}
