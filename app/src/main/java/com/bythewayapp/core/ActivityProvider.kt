package com.bythewayapp.core

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import java.util.concurrent.atomic.AtomicReference

object ActivityProvider {
    private val currentActivity = AtomicReference<ComponentActivity?>(null)
    private var permissionLauncher: ActivityResultLauncher<String>? = null
    private var multiplePermissionsLauncher: ActivityResultLauncher<Array<String>>? = null

    // Add callback interfaces
    interface PermissionCallback {
        fun onPermissionResult(isGranted: Boolean)
    }

    interface MultiplePermissionsCallback {
        fun onPermissionsResult(results: Map<String, Boolean>)
    }

    private var permissionCallback: PermissionCallback? = null
    private var multiplePermissionsCallback: MultiplePermissionsCallback? = null

    fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activity is ComponentActivity) {
                    currentActivity.set(activity)
                    // Register launchers early in lifecycle
                    permissionLauncher = activity.registerForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { isGranted ->
                        permissionCallback?.onPermissionResult(isGranted)
                        permissionCallback = null
                    }

                    multiplePermissionsLauncher = activity.registerForActivityResult(
                        ActivityResultContracts.RequestMultiplePermissions()
                    ) { results ->
                        multiplePermissionsCallback?.onPermissionsResult(results)
                        multiplePermissionsCallback = null
                    }
                }
            }

            override fun onActivityResumed(activity: Activity) {
                if (activity is ComponentActivity) {
                    currentActivity.set(activity)
                }
            }

            override fun onActivityDestroyed(activity: Activity) {
                if (activity === currentActivity.get()) {
                    currentActivity.set(null)
                }
            }

            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        })
    }

    fun getCurrentActivity(): ComponentActivity {
        return currentActivity.get() ?: throw IllegalStateException("No ComponentActivity available")
    }

    fun requestPermission(permission: String, callback: PermissionCallback) {
        permissionCallback = callback
        permissionLauncher?.launch(permission)
    }

    fun requestPermissions(permissions: Array<String>, callback: MultiplePermissionsCallback) {
        multiplePermissionsCallback = callback
        multiplePermissionsLauncher?.launch(permissions)
    }
}