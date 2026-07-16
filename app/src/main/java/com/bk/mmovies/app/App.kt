package com.bk.mmovies.app

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.bk.mmovies.BuildConfig
import com.bk.mmovies.connectivity.InternetMonitor
import com.bk.mmovies.util.MAIN_ACTIVITY_TAG
import com.bk.mmovies.util.logDebug
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Application.ActivityLifecycleCallbacks {

    @Inject
    lateinit var internetMonitor: InternetMonitor

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            registerActivityLifecycleCallbacks(this)
        }
        internetMonitor.start()
    }

    override fun onActivityCreated(
        activity: Activity,
        savedInstanceState: Bundle?
    ) {
        logDebug(
            MAIN_ACTIVITY_TAG,
            "onCreate: ${activity.localClassName}"
        )
    }

    override fun onActivityStarted(activity: Activity) {
        logDebug(
            MAIN_ACTIVITY_TAG,
            "onStart: ${activity.localClassName}"
        )
    }

    override fun onActivityResumed(activity: Activity) {
        logDebug(
            MAIN_ACTIVITY_TAG,
            "onResume: ${activity.localClassName}"
        )
    }

    override fun onActivityPaused(activity: Activity) {
        logDebug(
            MAIN_ACTIVITY_TAG,
            "onPause: ${activity.localClassName}"
        )
    }

    override fun onActivityStopped(activity: Activity) {
        logDebug(
            MAIN_ACTIVITY_TAG,
            "onStop: ${activity.localClassName}"
        )
    }

    override fun onActivitySaveInstanceState(
        activity: Activity,
        outState: Bundle
    ) {
        logDebug(
            MAIN_ACTIVITY_TAG,
            "onSaveInstanceState: ${activity.localClassName}"
        )
    }

    override fun onActivityDestroyed(activity: Activity) {
        logDebug(
            MAIN_ACTIVITY_TAG,
            "onDestroy: ${activity.localClassName}"
        )
    }

}