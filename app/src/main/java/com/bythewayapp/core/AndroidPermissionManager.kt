package com.bythewayapp.core

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import android.util.Log

class AndroidPermissionManager @Inject constructor(
    private val context: Context
) : PermissionManager {

    override fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override suspend fun requestPermission(permission: String): Boolean {
        if (hasPermission(permission)) return true

        return suspendCancellableCoroutine { continuation ->
            try {
                ActivityProvider.requestPermission(permission, object : ActivityProvider.PermissionCallback {
                    override fun onPermissionResult(isGranted: Boolean) {
                        continuation.resume(isGranted)
                    }
                })
            } catch (e: Exception) {
                Log.e("PermissionManager", "Error requesting permission", e)
                continuation.resume(false)
            }
        }
    }

    override suspend fun requestPermissions(permissions: List<String>): Map<String, Boolean> {
        if (permissions.all { hasPermission(it) }) {
            return permissions.associateWith { true }
        }

        return suspendCancellableCoroutine { continuation ->
            try {
                ActivityProvider.requestPermissions(permissions.toTypedArray(),
                    object : ActivityProvider.MultiplePermissionsCallback {
                        override fun onPermissionsResult(results: Map<String, Boolean>) {
                            continuation.resume(results)
                        }
                    }
                )
            } catch (e: Exception) {
                Log.e("PermissionManager", "Error requesting permissions", e)
                continuation.resume(permissions.associateWith { false })
            }
        }
    }
}

// Domain layer
interface PermissionManager {
    fun hasPermission(permission: String): Boolean
    suspend fun requestPermission(permission: String): Boolean
    suspend fun requestPermissions(permissions: List<String>): Map<String, Boolean>
}
