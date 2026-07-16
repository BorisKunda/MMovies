package com.bk.mmovies.connectivity

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings

fun openDeviceInternetSettings(context: Context) {
    val intent =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
        } else {
            Intent(Settings.ACTION_WIFI_SETTINGS)
        }

    try {
        context.startActivity(intent)
    } catch (exception: ActivityNotFoundException) {
        context.startActivity(
            Intent(Settings.ACTION_SETTINGS)
        )
    }
}