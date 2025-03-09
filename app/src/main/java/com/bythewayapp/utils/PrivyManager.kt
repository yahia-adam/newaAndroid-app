package com.bythewayapp.utils

import android.content.Context
import android.util.Log
import io.privy.logging.PrivyLogLevel
import io.privy.sdk.Privy
import io.privy.sdk.PrivyConfig

object PrivyManager {
    private var privy: Privy? = null

    fun init(context: Context) {
        if (privy == null) {
            try {
                privy = Privy.init(
                    context = context.applicationContext,
                    config = PrivyConfig(
                        appId = "cm5tvzo6w01m33pi8voq7n9d5",
                        appClientId = "client-WY5fQbv3tYqnZkGA4ggmxjphQwbALT8tRBg5KQrXnRWmW",
                        logLevel = PrivyLogLevel.VERBOSE
                    )
                )
                Log.i("PRIVY", "Privy successfully initialized")
            } catch (e: Exception) {
                Log.e("PRIVY", "Error initializing Privy: ${e.message}")
            }
        }
    }

    fun getInstance(): Privy {
        return privy ?: throw IllegalStateException("Privy not initialized")
    }
}
