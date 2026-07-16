package com.bk.mmovies.connectivity


import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import com.bk.mmovies.util.logDebug
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InternetMonitor @Inject constructor(
    @ApplicationContext context: Context
) {
    private val TAG = "InternetMonitor"
    private val connectivityManager: ConnectivityManager =
        context.applicationContext.getSystemService(ConnectivityManager::class.java)

    private val _isInternetAvailable =
        MutableStateFlow(checkInternetAvailability())

    val isInternetAvailable: StateFlow<Boolean> =
        _isInternetAvailable.asStateFlow()

    private var isStarted = false

    private val networkCallback =
        object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                updateInternetAvailability(message = "onAvailable", isLost = false)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                updateInternetAvailability("onCapabilitiesChanged", false)
            }

            override fun onLost(network: Network) {
                updateInternetAvailability("onLost", true)
            }
        }

    fun start() {
        if (isStarted) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(
                networkCallback
            )
        } else {
            val networkRequest =
                NetworkRequest.Builder()
                    .addCapability(
                        NetworkCapabilities.NET_CAPABILITY_INTERNET
                    )
                    .build()

            connectivityManager.registerNetworkCallback(
                networkRequest,
                networkCallback
            )
        }

        isStarted = true
        updateInternetAvailability("onMonitorStart", false)
    }

    private fun updateInternetAvailability(message: String, isLost: Boolean) {
        val isAvailable = if (isLost) false else checkInternetAvailability()
        _isInternetAvailable.value = isAvailable
        logDebug(TAG, "[ $message ] is Internet available: $isAvailable")
    }

    private fun checkInternetAvailability(): Boolean {
        val activeNetwork =
            connectivityManager.activeNetwork
            ?: return false

        val capabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork)
            ?: return false

        val hasInternetCapability =
            capabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_INTERNET
            )

        val hasValidatedInternet =
            capabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_VALIDATED
            )

        return hasInternetCapability && hasValidatedInternet
    }
}