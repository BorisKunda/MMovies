package com.bk.mmovies.util

import android.util.Log
import com.bk.mmovies.BuildConfig
import com.google.firebase.crashlytics.FirebaseCrashlytics

const val MAIN_ACTIVITY_TAG = "MainActivity"

fun logDebug(
        tag: String,
        message: String
            ) {
    if (BuildConfig.DEBUG) {
        Log.d(
                "LOG_$tag",
                message
             )
    }
}

/**
 * Logs a handled failure.
 *
 * Release builds have no logcat to read, so these also go to Crashlytics as a
 * breadcrumb — otherwise every handled error (failed loads, unparseable error
 * bodies, network exceptions) vanished in release and Crashlytics only ever
 * saw hard crashes.
 */
fun logError(
        tag: String,
        message: String
            ) {
    if (BuildConfig.DEBUG) {
        Log.e(
                "LOG_$tag",
                message
             )
    } else {
        FirebaseCrashlytics.getInstance().log("$tag: $message")
    }
}