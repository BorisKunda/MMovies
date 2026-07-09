package com.bk.mmovies.util

import android.util.Log
import com.bk.mmovies.BuildConfig

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

fun logError(
    tag: String,
    message: String
) {
    if (BuildConfig.DEBUG) {
        Log.e(
            "LOG_$tag",
            message
        )
    }
}