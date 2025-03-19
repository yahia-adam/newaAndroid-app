package com.bythewayapp.ui.viewModels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bythewayapp.R
import com.bythewayapp.core.LocationDisabledException
import com.bythewayapp.core.LocationManager
import com.bythewayapp.core.LocationPermissionDeniedException
import com.bythewayapp.core.NoLocationAvailableException
import com.bythewayapp.data.EventRepository
import com.bythewayapp.model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeoutException
import javax.inject.Inject
import ch.hsr.geohash.GeoHash
import android.location.Location
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

sealed interface BythewayUiSate {
    data class Success(val events: List<Event>): BythewayUiSate
    data class UnknownError(val message: String) : BythewayUiSate
    data class InternetConnectionError(val message: String) : BythewayUiSate
    data class EnableUserLocation(
        val message: String,
        val type: LocationErrorType = LocationErrorType.PERMISSION_DENIED
    ) : BythewayUiSate
    data object Loading: BythewayUiSate
}

enum class LocationErrorType {
    PERMISSION_DENIED,
    LOCATION_DISABLED,
    LOCATION_UNAVAILABLE,
    UNKNOWN
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val locationManager: LocationManager,
    private val context: Context
) : ViewModel() {

    companion object {
        const val DEFAULT_SIZE = "200"
        const val DEFAULT_CITY = "Paris"
        const val BTN_SELECTED_DATE = "Select Date Range"
        private const val TAG = "HOME_VIEW_MODEL"
    }

    var bythewayUiSate: BythewayUiSate by mutableStateOf(BythewayUiSate.Loading)
        private set

    var keyword by mutableStateOf("")
        private set

    private var startDate by mutableStateOf<String?>(null)
    private var endDate by mutableStateOf<String?>(null)

    var btnSelectedDate by mutableStateOf(BTN_SELECTED_DATE)
        private set

    private val dateFormatter = SimpleDateFormat("d MMM", Locale.getDefault())
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

    private var userLocation by mutableStateOf<Location?>(null)
    private var locationAttempts = 0

    init {
        getUserLocation()
    }

    fun getUserLocation() {
        Log.d(TAG, "Demande de localisation utilisateur (tentative: $locationAttempts)")
        bythewayUiSate = BythewayUiSate.Loading
        locationAttempts++

        viewModelScope.launch {
            val result = locationManager.getLastLocation()

            when {
                result.isSuccess -> {
                    // Localisation obtenue avec succès
                    userLocation = result.getOrNull()
                    Log.d(TAG, "Localisation utilisateur obtenue = $userLocation")

                    // Réinitialiser le compteur de tentatives
                    locationAttempts = 0

                    // Maintenant récupérer les événements avec la localisation
                    getEvents(
                        size = DEFAULT_SIZE,
                        startDateTime = startDate ?: "",
                        endDateTime = endDate ?: "",
                        city = DEFAULT_CITY
                    )
                }
                result.isFailure -> {
                    // Gérer les différents types d'erreurs de localisation
                    val exception = result.exceptionOrNull()
                    Log.e(TAG, "Erreur de localisation", exception)

                    when (exception) {
                        is LocationPermissionDeniedException -> {
                            bythewayUiSate = BythewayUiSate.EnableUserLocation(
                                message = context.getString(R.string.location_permission_denied),
                                type = LocationErrorType.PERMISSION_DENIED
                            )
                        }
                        is LocationDisabledException -> {
                            bythewayUiSate = BythewayUiSate.EnableUserLocation(
                                message = context.getString(R.string.location_service_disabled),
                                type = LocationErrorType.LOCATION_DISABLED
                            )
                        }
                        is NoLocationAvailableException -> {
                            bythewayUiSate = BythewayUiSate.EnableUserLocation(
                                message = context.getString(R.string.no_location_available),
                                type = LocationErrorType.LOCATION_UNAVAILABLE
                            )
                        }
                        else -> {
                            bythewayUiSate = BythewayUiSate.EnableUserLocation(
                                message = exception?.message ?: context.getString(R.string.unknown_location_error),
                                type = LocationErrorType.UNKNOWN
                            )
                        }
                    }
                }
            }
        }
    }

    fun getGeohash(latitude: Double, longitude: Double, precision: Int = 12): String {
        val geoHash = GeoHash.withBitPrecision(latitude, longitude, precision * 5)
        return geoHash.toBase32().substring(0, 7)
    }

    fun reInitialise() {
        bythewayUiSate = BythewayUiSate.Loading
        keyword = ""
        btnSelectedDate = BTN_SELECTED_DATE
        getUserLocation()
    }

    fun formatDateRange(startDate: Long, endDate: Long): String {
        val startDateString = dateFormatter.format(Date(startDate))
        val endDateString = dateFormatter.format(Date(endDate))

        return if (startDateString == endDateString) {
            startDateString // Si la plage est sur un seul jour
        } else {
            "$startDateString - $endDateString"
        }
    }

    fun onDateRangeChanged(startDateTime: Long, endDateTime: Long) {
        // Formater la plage de dates pour affichage dans le bouton
        btnSelectedDate = formatDateRange(startDateTime, endDateTime)

        // Formater les timestamps en chaînes pour les requêtes
        val formattedStartDate = Instant.ofEpochMilli(startDateTime)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .format(formatter)
        val formattedEndDate = Instant.ofEpochMilli(endDateTime)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .format(formatter)

        startDate = formattedStartDate
        endDate = formattedEndDate

        // Appeler la fonction pour récupérer les événements
        getEvents(
            keyword = if (keyword.isNotEmpty()) keyword else null,
            startDateTime = formattedStartDate,
            endDateTime = formattedEndDate
        )
    }

    private var searchJob: Job? = null

    fun onKeywordChanged(value: String) {
        keyword = value

        // Annule la recherche précédente si elle est toujours en cours
        searchJob?.cancel()

        if (keyword.length > 2) {
            // Démarre une nouvelle recherche avec un délai
            searchJob = viewModelScope.launch {
                delay(500) // Attend 500ms avant de lancer la recherche
                getEvents(
                    keyword = keyword,
                    size = DEFAULT_SIZE,
                    startDateTime = startDate ?: "",
                    endDateTime = endDate ?: "",
                    city = DEFAULT_CITY
                )
            }
        }
    }

    fun getEvents(
        keyword: String? = null,
        id: List<String>? = null,
        startDateTime: String? = null,
        endDateTime: String? = null,
        classificationName: List<String>? = null,
        classificationId: List<String>? = null,
        size: String? = null,
        city: String? = null,
    ) {
        if (userLocation == null) {
            getUserLocation()
            return
        }

        viewModelScope.launch {
            val geoHash = getGeohash(userLocation!!.latitude, userLocation!!.longitude)
            Log.d(TAG, "GeoHash calculé: $geoHash")

            try {
                val response = eventRepository.getEvents(
                    keyword = keyword,
                    id = id,
                    startDateTime = startDateTime,
                    endDateTime = endDateTime,
                    size = size,
                    classificationName = classificationName,
                    classificationId = classificationId,
                    city = city,
                    geoPoint = geoHash,
                )

                val events = response.embedded?.events ?: emptyList()
                if (events.isNotEmpty()) {
                    bythewayUiSate = BythewayUiSate.Success(events)
                }
            } catch (e: IOException) {
                bythewayUiSate = BythewayUiSate.InternetConnectionError(context.getString(R.string.erreur_de_connexion))
            } catch (e: TimeoutException) {
                bythewayUiSate = BythewayUiSate.InternetConnectionError(context.getString(R.string.error_connection_lent))
            } catch (e: Exception) {
                Log.e(TAG, "Erreur lors de la récupération des événements", e)
                bythewayUiSate = BythewayUiSate.UnknownError(context.getString(R.string.error_inconue))
            }
        }
    }
}