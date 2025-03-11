package com.bythewayapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectionStateManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // État de la connexion exposé via StateFlow
    private val _connectionState = MutableStateFlow(getInitialConnectionState())
    val connectionState: StateFlow<ConnectionState> = _connectionState

    private fun getInitialConnectionState(): ConnectionState {
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return if (networkCapabilities != null &&
            (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
        ) {
            ConnectionState.Available
        } else {
            ConnectionState.Unavailable
        }
    }

    init {
        observeNetworkChanges()
    }

    private fun observeNetworkChanges() {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _connectionState.value = ConnectionState.Available
            }

            override fun onLost(network: Network) {
                _connectionState.value = ConnectionState.Unavailable
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                if (hasInternet) {
                    _connectionState.value = ConnectionState.Available
                } else {
                    _connectionState.value = ConnectionState.Unavailable
                }
            }
        }

        val networkRequest = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }
}

sealed class ConnectionState {
    object Available : ConnectionState()
    object Unavailable : ConnectionState()
}
