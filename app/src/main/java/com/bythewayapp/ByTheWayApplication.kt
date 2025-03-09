package com.bythewayapp

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ByTheWayApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate BytheWay application")
    }
    companion object {
        const val TAG = "ByTheWayApplication"
    }
}