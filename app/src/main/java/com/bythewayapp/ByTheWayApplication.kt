package com.bythewayapp

import android.app.Application
import android.util.Log
import android.webkit.WebView
import com.bythewayapp.core.ActivityProvider
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ByTheWayApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate BytheWay application")

        ActivityProvider.init(this)

    }

    companion object {
        const val TAG = "ByTheWayApplication"
    }
}