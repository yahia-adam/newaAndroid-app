package com.bythewayapp.core

import android.content.Context
import com.mapbox.android.core.permissions.PermissionsManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

sealed class LocationSate {
    object Located : LocationSate()
    object unLocated : LocationSate()
}

class LocationPermissionManager @Inject constructor(
    @ApplicationContext context: Context
) {

    lateinit var permissionsManager: PermissionsManager

    init {
    }

}