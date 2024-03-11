package com.puras.itoandroidassignment.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

class NetworkStatusTracker @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val connMgr: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isConnectedFlow = MutableStateFlow(false)
    val isConnectedFlow: Flow<Boolean> = _isConnectedFlow

    var isConnected: Boolean = false

    init {
        Timber.d("NetworkStatusTracker init: $isConnected")
        connMgr.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isConnectedFlow.update { true }
                isConnected = true
            }

            override fun onLost(network: Network) {
                _isConnectedFlow.update { false }
                isConnected = false
            }
        })
    }
}