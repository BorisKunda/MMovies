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

private const val TAG = "InternetMonitor"

@Singleton
class InternetMonitor @Inject constructor(
        @ApplicationContext context: Context
                                         ) {
    private val connectivityManager: ConnectivityManager =
            context.applicationContext.getSystemService(ConnectivityManager::class.java)

    private val _isInternetAvailable =
            MutableStateFlow(
                    activeNetworkCapabilitiesSnapshot()
                            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
                            )

    val isInternetAvailable: StateFlow<Boolean> =
            _isInternetAvailable.asStateFlow()

    private var isStarted = false

    private val networkCallback =
            object : ConnectivityManager.NetworkCallback() {

                override fun onAvailable(network: Network) {
                    // getNetworkCapabilities(network) here still asks for a
                    // specific, already-tracked Network - unlike activeNetwork
                    // below, this isn't the per-app "what would my traffic use
                    // right now" query, so it isn't subject to that same
                    // background-app answer.
                    setAvailability(
                            "onAvailable",
                            connectivityManager.getNetworkCapabilities(network)
                                    )
                }

                override fun onCapabilitiesChanged(
                        network: Network,
                        networkCapabilities: NetworkCapabilities
                                                  ) {
                    // Use what the callback already handed us instead of
                    // re-querying via activeNetwork - see
                    // activeNetworkCapabilitiesSnapshot's comment for why.
                    setAvailability(
                            "onCapabilitiesChanged",
                            networkCapabilities
                                    )
                }

                override fun onLost(network: Network) {
                    setAvailability(
                            "onLost",
                            capabilities = null
                                    )
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
        // No live Network reference exists until the callback above fires
        // (which happens immediately for the current network, asynchronously)
        // so this cold-start snapshot is the one legitimate use of the
        // per-app activeNetwork query - same as the property initializer
        // below.
        setAvailability(
                "onMonitorStart",
                activeNetworkCapabilitiesSnapshot()
                        )
    }

    // Deliberately not also requiring NET_CAPABILITY_VALIDATED: that flag
    // reflects the OS's own background probe to a Google captive-portal-check
    // URL succeeding, which is a false negative on VPNs, local DNS/firewall
    // apps, and some restrictive networks that block just that probe while
    // real internet access works fine - it can stay false forever on those
    // setups. This is used only as a signal to retry, so callers decide
    // "actually offline" from real request failures instead (see
    // NetworkError.isConnectivityFailure).
    private fun setAvailability(
            message: String,
            capabilities: NetworkCapabilities?
                                ) {
        val isAvailable =
                capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        _isInternetAvailable.value = isAvailable
        logDebug(
                TAG,
                "[ $message ] is Internet available: $isAvailable"
                )
    }

    // connectivityManager.activeNetwork answers "what network would *this
    // app's* traffic use right now" - a live per-app query Android can answer
    // more conservatively for a backgrounded process with no foreground
    // service, independent of the network's real state. That's fine for this
    // one-time cold-start snapshot (self-corrects the moment the callback
    // above fires for the current network), but must never be used for the
    // ongoing checks - onAvailable/onCapabilitiesChanged already carry the
    // real capabilities for a specific, already-tracked Network and don't
    // have this problem.
    private fun activeNetworkCapabilitiesSnapshot(): NetworkCapabilities? {
        val activeNetwork = connectivityManager.activeNetwork ?: return null
        return connectivityManager.getNetworkCapabilities(activeNetwork)
    }
}