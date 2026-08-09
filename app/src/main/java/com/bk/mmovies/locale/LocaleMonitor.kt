package com.bk.mmovies.locale

import android.content.ComponentCallbacks
import android.content.Context
import android.content.res.Configuration
import com.bk.mmovies.util.logDebug
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocaleMonitor @Inject constructor(
        @ApplicationContext private val context: Context
                                       ) : ComponentCallbacks {

    private val TAG = "LocaleMonitor"

    private val _currentLanguage =
            MutableStateFlow(resolveCurrentLanguage())

    val currentLanguage: StateFlow<AppLanguage> =
            _currentLanguage.asStateFlow()

    private var isStarted = false

    fun start() {
        if (isStarted) {
            return
        }

        context.registerComponentCallbacks(this)
        isStarted = true
        logDebug(
                TAG,
                "onMonitorStart current language: ${_currentLanguage.value}"
                )
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        val newLanguage = AppLanguage.fromLocale(newConfig.locales[0])
        if (newLanguage != _currentLanguage.value) {
            _currentLanguage.value = newLanguage
            logDebug(
                    TAG,
                    "onConfigurationChanged new language: $newLanguage"
                    )
        }
    }

    override fun onLowMemory() {
        // no-op
    }

    private fun resolveCurrentLanguage(): AppLanguage =
            AppLanguage.fromLocale(context.resources.configuration.locales[0])
}
