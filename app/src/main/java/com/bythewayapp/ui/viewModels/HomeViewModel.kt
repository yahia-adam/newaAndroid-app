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
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import com.bythewayapp.model.TicketmasterSuggestionResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.time.LocalDate

sealed interface BythewayUiSate {
    data class Success(val events: List<Event>, val long : Double, val lat: Double): BythewayUiSate
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

    // global
    var bythewayUiSate: BythewayUiSate by mutableStateOf(BythewayUiSate.Loading)
        private set
    companion object {
        const val DEFAULT_SIZE = "200"
        private const val TAG = "HOME_VIEW_MODEL"
    }
    init {
        getUserLocation()
    }

    // menage mapview
    var isMapView by mutableStateOf<Boolean>(true)
    fun TraggleView(isTraggleMapView: Boolean) {
        isMapView = isTraggleMapView
    }

    fun reInitialise() {
        bythewayUiSate = BythewayUiSate.Loading
        keyword = ""
        startDate = null
        endDate = null
        radius = "50"
        genres = emptyList()
        getUserLocation()
    }

    // events
    fun getEvents(
        keyword: String? = null,
        id: List<String>? = null,
        startDateTime: String? = null,
        endDateTime: String? = null,
        classificationName: List<String>? = null,
        classificationId: List<String>? = null,
        size: String? = DEFAULT_SIZE,
        city: String? = null,
        radius: String? = "50",
        ) {
        if (userLocation == null) {
            getUserLocation()
            return
        }

        viewModelScope.launch {

            val geoHash = getGeohash(userLocation!!.latitude, userLocation!!.longitude)
            Log.d(TAG, "GeoHash calculé: $geoHash, cord: ${userLocation!!.latitude}, ${userLocation!!.longitude}")

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
                    radius = radius,
                )

                Log.d(TAG, "response = ${response.links?.self?.href}")
                val events = response.embedded?.events ?: emptyList()
                if (events.isNotEmpty()) {
                    bythewayUiSate = BythewayUiSate.Success(events, userLocation!!.latitude, userLocation!!.longitude)
                } else {
                    val message = "Aucun résultat trouvé. Essayez d'élargir votre recherche ou de modifier vos filtres."
                    bythewayUiSate = BythewayUiSate.Success(emptyList(), userLocation!!.latitude, userLocation!!.longitude)
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
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

    // event suggestion
    var eventsuggestions by mutableStateOf<List<String>>(emptyList())
        private set
    fun fetchEventsSuggestions(query: String) {
        viewModelScope.launch {
            try {
                val suggestions = eventRepository.getSuggestions(query) // à créer dans ton repo
                eventsuggestions = suggestions.embedded?.events?.map { e ->
                    e.name
                } ?: emptyList()
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Erreur suggestions", e)
                eventsuggestions = emptyList()
            }
            Log.d(TAG, "suggestions = ${eventsuggestions.size}")
        }
    }

    // -------------------------- filter --------------------------
    // keyworld
    var keyword by mutableStateOf("")
        private set
    fun onKeywordChanged(value: String) {
        keyword = value
        applyFilter()
    }

    // Filter
    private var startDate by mutableStateOf<String?>(null)
    private var endDate by mutableStateOf<String?>(null)
    private var genres by mutableStateOf<List<String>>(emptyList())
    private var radius by mutableStateOf<String?>(null)

    private val inputDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val apiDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00'Z'")

    fun updateFilters(start: String?, end: String?, selectedGenres: List<String>, selectedRadius: Int) {
        startDate = start?.let {
            LocalDate.parse(it, inputDateFormatter).format(apiDateFormatter)
        }
        endDate = end?.let {
            LocalDate.parse(it, inputDateFormatter).format(apiDateFormatter)
        }
        genres = selectedGenres
        radius = selectedRadius.toString()
    }

    fun applyFilter() {

        Log.d(TAG, "radius = $radius\nstartDate = $startDate\nendDate = $endDate\ngenres = $genres")
        // Appeler la fonction pour récupérer les événements

        getEvents(
            keyword = if (keyword.isNotEmpty()) keyword else null,
            startDateTime = startDate,
            endDateTime = endDate,
            classificationName = genres,
            radius = radius
        )
    }

    // user localtion
    private var userLocation by mutableStateOf<Location?>(null)
    private var locationAttempts = 0
    fun getGeohash(latitude: Double, longitude: Double, precision: Int = 12): String {
        val geoHash = GeoHash.withBitPrecision(latitude, longitude, precision * 5)
        return geoHash.toBase32().substring(0, 7)
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
                        city = null
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

}