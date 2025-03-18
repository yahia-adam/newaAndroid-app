package com.bythewayapp.core

import android.Manifest
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class LocationManager @Inject constructor(
    private val context: Context,
    private val permissionManager: PermissionManager
) {
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val TAG = "LocationManager"

    suspend fun getLastLocation(): Result<Location> {
        // Vérifier si la localisation est activée sur l'appareil
        if (!isLocationEnabled()) {
            return Result.failure(LocationDisabledException("Services de localisation désactivés sur l'appareil"))
        }

        // Vérifier les permissions de localisation
        val permissionResult = checkLocationPermission()
        if (!permissionResult.isSuccess) {
            return Result.failure(permissionResult.exceptionOrNull() ?:
            SecurityException("Permission de localisation non accordée"))
        }

        // Essayer d'obtenir la dernière localisation connue
        val lastLocationResult = getLastKnownLocation()

        // Si la dernière localisation est disponible et récente, la retourner
        lastLocationResult.getOrNull()?.let { location ->
            if (isLocationRecent(location)) {
                Log.d(TAG, "Dernière localisation connue récente trouvée")
                return lastLocationResult
            } else {
                Log.d(TAG, "Dernière localisation connue trop ancienne, demande de localisation actuelle")
            }
        }

        // Sinon, demander une localisation actuelle
        return getCurrentLocation()
    }

    private suspend fun getLastKnownLocation(): Result<Location> {
        return suspendCancellableCoroutine { continuation ->
            try {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            continuation.resume(Result.success(location))
                        } else {
                            continuation.resume(Result.failure(NoLocationAvailableException("Aucune dernière localisation disponible")))
                        }
                    }
                    .addOnFailureListener { exception: Exception ->
                        Log.e(TAG, "Erreur lors de la récupération de la dernière localisation", exception)
                        continuation.resume(Result.failure(exception))
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Exception lors de l'accès à la dernière localisation", e)
                continuation.resume(Result.failure(e))
            }
        }
    }

    private suspend fun getCurrentLocation(): Result<Location> {
        val cancellationTokenSource = CancellationTokenSource()

        return suspendCancellableCoroutine { continuation ->
            try {
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ).addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        continuation.resume(Result.success(location))
                    } else {
                        continuation.resume(Result.failure(
                            NoLocationAvailableException("Impossible d'obtenir la localisation actuelle")
                        ))
                    }
                }.addOnFailureListener { exception: Exception ->
                    Log.e(TAG, "Erreur lors de l'obtention de la localisation actuelle", exception)
                    continuation.resume(Result.failure(exception))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception lors de l'accès à la localisation actuelle", e)
                continuation.resume(Result.failure(e))
            }

            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        }
    }

    private fun isLocationRecent(location: Location): Boolean {
        val currentTimeMillis = System.currentTimeMillis()
        val locationTimeMillis = location.time
        val timeDifference = currentTimeMillis - locationTimeMillis

        // Considérer une localisation comme récente si elle date de moins de 5 minutes
        return timeDifference < 5 * 60 * 1000
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

    private suspend fun checkLocationPermission(): Result<Boolean> {
        val fineLocationPermission = permissionManager.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocationPermission = permissionManager.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)

        // Si aucune permission n'est accordée, les demander
        if (!fineLocationPermission && !coarseLocationPermission) {
            val permissionsToRequest = listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

            val result = permissionManager.requestPermissions(permissionsToRequest)

            return if (result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                result[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                Result.success(true)
            } else {
                Result.failure(LocationPermissionDeniedException("Permissions de localisation refusées"))
            }
        }

        return Result.success(true)
    }
}

// Classes d'exceptions personnalisées pour une meilleure gestion des erreurs
class LocationPermissionDeniedException(message: String) : Exception(message)
class LocationDisabledException(message: String) : Exception(message)
class NoLocationAvailableException(message: String) : Exception(message)