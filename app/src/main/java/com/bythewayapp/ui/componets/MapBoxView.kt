package com.bythewayapp.ui.componets

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.toArgb
import com.bythewayapp.model.Event
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotationGroup
import com.mapbox.maps.extension.style.expressions.dsl.generated.literal
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.AnnotationSourceOptions
import com.mapbox.maps.plugin.annotation.ClusterOptions
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import android.content.Context
import android.graphics.*
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.JsonObject
import com.mapbox.maps.extension.style.expressions.generated.Expression
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarHost
import androidx.compose.ui.zIndex
import androidx.compose.material3.rememberDateRangePickerState

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import androidx.core.graphics.createBitmap

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.bythewayapp.R
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location

/**
 * A composable that displays zoom controls and a location button for MapBox.
 *
 * @param mapViewportState The state of the map's viewport
 * @param onLocationClick Callback when the location button is clicked
 * @param modifier Modifier for this composable
 */
@Composable
fun MapControls(
    currentZoom: Double,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onLocationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(top = 0.dp, bottom = 0.dp, end = 0.dp, start = 0.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Zoom in button
        MapControlButton(
            onClick = onZoomIn,
            icon = painterResource(id=R.drawable.baseline_add_24)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Zoom out button
        MapControlButton(
            onClick = onZoomOut,
            icon = painterResource(id=R.drawable.baseline_remove_24)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Location button
        MapControlButton(
            onClick = onLocationClick,
            icon = painterResource(id=R.drawable.baseline_my_location_24),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun MapControlButton(
    onClick: () -> Unit,
    icon: Painter,
    tint: Color = Color.Black
) {
    Box(
        modifier = Modifier
            .size(35.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(1.dp, Color.LightGray.copy(alpha = 0.5f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * Extension to be used in your existing MapBoxView composable
 */
fun handleZoomIn(mapViewportState: com.mapbox.maps.extension.compose.animation.viewport.MapViewportState) {
    val currentZoom = mapViewportState.cameraState?.zoom ?: 10.0
    mapViewportState.setCameraOptions {
        zoom(currentZoom + 1.0)
    }
}

fun handleZoomOut(mapViewportState: com.mapbox.maps.extension.compose.animation.viewport.MapViewportState) {
    val currentZoom = mapViewportState.cameraState?.zoom ?: 10.0
    mapViewportState.setCameraOptions {
        zoom(currentZoom - 1.0)
    }
}

fun handleLocationClick(
    mapViewportState: com.mapbox.maps.extension.compose.animation.viewport.MapViewportState,
    currentUserLocation: Point?
) {
    currentUserLocation?.let { userLocation ->
        mapViewportState.setCameraOptions {
            center(userLocation)
            zoom(15.0) // Adjust zoom level as needed
        }
    }
}

@Composable
fun MapBoxView(
    keyword: String,
    onKeywordChanged: (String) -> Unit,
    btnSelectedDate: String,
    onDateRangeChanged: (Long, Long) -> Unit,
    onEventClick: (Event) -> Unit,
    modifier: Modifier = Modifier,
    events: List<Event>
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // État pour le BottomSheet des clusters
    var isClusterBottomSheetVisible by remember { mutableStateOf(false) }
    var selectedClusterEvents by remember { mutableStateOf<List<Event>>(emptyList()) }

    // État pour le BottomSheet de détail d'un événement individuel
    var isEventDetailBottomSheetVisible by remember { mutableStateOf(false) }
    var selectedEvent by remember { mutableStateOf<Event?>(null) }

    // État pour suivre la position de l'utilisateur
    var currentUserLocation by remember { mutableStateOf<Point?>(null) }

    val validEvents = events.filter {
        val point = it.getCoordinates()
        point.longitude() != 0.0 || point.latitude() != 0.0
    }

    val eventPoints = validEvents.map { it.getCoordinates() }
    // État pour stocker les bitmaps des marqueurs
    val markerBitmaps = remember { mutableStateMapOf<String, Bitmap?>() }

    val snackState = remember { SnackbarHostState() }
    var showDateRangePicker by remember { mutableStateOf(false) }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(11.0)
            center(Point.fromLngLat(2.3522, 48.8566)) // Coordonnées de Paris
        }
    }

    // Charger les bitmaps des marqueurs de façon asynchrone
    LaunchedEffect(validEvents) {
        validEvents.forEach { event ->
            val imageUrl = event.images?.get(0)?.url
            if (imageUrl != null) {
                coroutineScope.launch {
                    try {
                        val bitmap = createRoundMarkerFromUrl(
                            context = context,
                            imageUrl = imageUrl,
                            markerSize = 100,
                            borderWidth = 4f,
                            borderColor = Color.White.toArgb()
                        )
                        markerBitmaps[event.id] = bitmap
                    } catch (e: Exception) {
                        Log.e("MapBoxView", "Failed to load marker image", e)
                        // Utiliser une image par défaut en cas d'échec
                        markerBitmaps[event.id] = createDefaultMarker(context)
                    }
                }
            } else {
                // Si pas d'URL d'image, utiliser un marqueur par défaut
                markerBitmaps[event.id] = createDefaultMarker(context)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MapboxMap(
            modifier = modifier.fillMaxSize(),
            mapViewportState = mapViewportState
        ) {
            MapEffect(key1 = Unit) { mapView ->
                val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
                    currentUserLocation = it
                }

                mapView.location.apply {
                    enabled = true
                    addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
                    locationPuck = createDefault2DPuck(withBearing = true)
                }
            }

            PointAnnotationGroup(
                annotations = eventPoints.mapIndexed { index, item ->
                    val event = validEvents[index]
                    val bitmap = markerBitmaps[event.id]

                    PointAnnotationOptions()
                        .withPoint(item)
                        .withData(
                            JsonObject().apply {
                                addProperty("index", index)
                            }
                        )
                        // N'appliquer l'image que si elle est disponible
                        .apply {
                            bitmap?.let { withIconImage(it) }
                        }
                },
                annotationConfig = AnnotationConfig(
                    annotationSourceOptions = AnnotationSourceOptions(
                        clusterOptions = ClusterOptions(
                            textColorExpression = Expression.color(Color.Black.toArgb()),
                            textColor = Color.Black.toArgb(),
                            textSize = 20.0,
                            circleRadiusExpression = literal(25.0),
                            colorLevels = listOf(
                                Pair(100, Color.Red.toArgb()),
                                Pair(50, Color.Blue.toArgb()),
                                Pair(0, Color.Green.toArgb())
                            )
                        )
                    )
                )
            ) {
                // Gestionnaire de clic sur un marqueur individuel
                interactionsState.onClicked { clickedPoint ->
                    // Trouver l'événement correspondant au point cliqué
                    val index = eventPoints.indexOfFirst {
                        it.longitude() == clickedPoint.point.longitude() &&
                                it.latitude() == clickedPoint.point.latitude()
                    }

                    if (index != -1) {
                        val event = validEvents[index]
                        // Au lieu de faire onEventClick, on affiche le BottomSheet de détail
                        selectedEvent = event
                        isEventDetailBottomSheetVisible = true
                    }

                    true
                }

                // Gestionnaire de clic sur un cluster
                interactionsState.onClusterClicked { cluster ->
                    Log.d("MapBoxView", "Cluster clicked: ID=${cluster.clusterId}, Count=${cluster.pointCount}")

                    // Pour des raisons de simplicité, nous allons considérer que tous les événements
                    // dans un rayon autour du centre du cluster font partie du cluster
                    val clusterPoint = cluster.originalFeature.geometry() as Point

                    // Trouver tous les événements proches du centre du cluster
                    // Vous devrez peut-être ajuster la distance en fonction de vos besoins
                    val nearbyEvents = findNearbyEvents(
                        clusterPoint,
                        validEvents,
                        maxDistance = 0.1, // Ajuster selon vos besoins
                        maxEvents = cluster.pointCount.toInt()
                    )

                    if (nearbyEvents.isNotEmpty()) {
                        selectedClusterEvents = nearbyEvents
                        isClusterBottomSheetVisible = true
                    } else {
                        mapViewportState.setCameraOptions {
                            zoom((mapViewportState.cameraState?.zoom ?: 10.0) + 2.0)
                            center(clusterPoint)
                        }
                    }
                    true
                }
            }
        }

        // Ajouter les contrôles de carte (zoom et localisation)
        MapControls(
            currentZoom = mapViewportState.cameraState?.zoom ?: 11.0,
            onZoomIn = { handleZoomIn(mapViewportState) },
            onZoomOut = { handleZoomOut(mapViewportState) },
            onLocationClick = { handleLocationClick(mapViewportState, currentUserLocation) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 30.dp, end = 16.dp)
        )

        Column (
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            EnhancedSearchTextField(
                modifier = Modifier,
                value = keyword,
                onValueChange = onKeywordChanged
            )

            Spacer(modifier = Modifier.height(12.dp))

            EnhancedDateButton(
                dateText = btnSelectedDate,
                onClick = { showDateRangePicker = true }
            )

            SnackbarHost(hostState = snackState, Modifier.zIndex(1f))
        }

        // Afficher le BottomSheet des événements du cluster
        EventsBottomSheet(
            isVisible = isClusterBottomSheetVisible,
            events = selectedClusterEvents,
            onClose = { isClusterBottomSheetVisible = false },
            onEventClick = { event ->
                // Au lieu d'appeler onEventClick directement, on affiche le BottomSheet de détail
                selectedEvent = event
                isClusterBottomSheetVisible = false
                isEventDetailBottomSheetVisible = true
            },
            modifier = Modifier.zIndex(2f)
        )

        // Afficher le BottomSheet de détail d'un événement individuel
        EventDetailBottomSheet(
            isVisible = isEventDetailBottomSheetVisible,
            event = selectedEvent,
            onClose = { isEventDetailBottomSheetVisible = false },
            onNavigate = { event ->
                // Rediriger l'utilisateur vers l'URL de réservation de l'événement
                onEventClick(event)
                isEventDetailBottomSheetVisible = false
            },
            modifier = Modifier.zIndex(3f) // Priorité plus élevée que le BottomSheet de cluster
        )

        if (showDateRangePicker) {
            DateRangePickerModal(
                onDismiss = { showDateRangePicker = false },
                onDateRangeSelected = { startDate, endDate ->
                    onDateRangeChanged(startDate, endDate)
                }
            )
        }
    }
}

/**
 * Trouve les événements proches d'un point donné.
 */
private fun findNearbyEvents(
    centerPoint: Point,
    events: List<Event>,
    maxDistance: Double,
    maxEvents: Int
): List<Event> {
    // Calculer la distance entre deux points
    fun distanceBetween(p1: Point, p2: Point): Double {
        val dx = p1.longitude() - p2.longitude()
        val dy = p1.latitude() - p2.latitude()
        return Math.sqrt(dx * dx + dy * dy)
    }

    // Trier les événements par distance
    return events
        .map { event ->
            val point = event.getCoordinates()
            val distance = distanceBetween(centerPoint, point)
            Pair(event, distance)
        }
        .filter { (_, distance) -> distance <= maxDistance }
        .sortedBy { (_, distance) -> distance }
        .take(maxEvents)
        .map { (event, _) -> event }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    onDismiss: () -> Unit,
    onDateRangeSelected: (Long, Long) -> Unit
) {
    val state = rememberDateRangePickerState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .zIndex(2f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select Date Range",
                modifier = Modifier.padding(bottom = 16.dp)
            )

            DateRangePicker(state = state, modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onDismiss) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        if (state.selectedEndDateMillis != null) {
                            onDateRangeSelected(
                                state.selectedStartDateMillis!!,
                                state.selectedEndDateMillis!!
                            )
                            onDismiss()
                        }
                    },
                    enabled = state.selectedEndDateMillis != null
                ) {
                    Text("Save")
                }
            }
        }
    }
}






/**
 * Charge une image depuis une URL et crée un marqueur rond avec bordure.
 */
private suspend fun createRoundMarkerFromUrl(
    context: Context,
    imageUrl: String,
    markerSize: Int = 100,
    borderWidth: Float = 4f,
    borderColor: Int = Color.White.toArgb()
): Bitmap = withContext(Dispatchers.IO) {
    // Charger l'image depuis l'URL
    val imageBitmap = loadImageFromUrl(context, imageUrl, markerSize)

    // Créer un bitmap circulaire avec une bordure
    createRoundMarkerWithBorder(imageBitmap, markerSize, borderWidth, borderColor)
}

/**
 * Charge une image depuis une URL en utilisant Glide.
 */
private suspend fun loadImageFromUrl(context: Context, url: String, size: Int): Bitmap =
    suspendCoroutine { continuation ->
        Glide.with(context)
            .asBitmap()
            .load(url)
            .centerCrop()
            .override(size, size)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    continuation.resume(resource)
                }

                override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {
                    // Non utilisé
                }

                override fun onLoadFailed(errorDrawable: android.graphics.drawable.Drawable?) {
                    // Créer une image de remplacement en cas d'échec
                    val fallbackBitmap = createBitmap(size, size)
                    fallbackBitmap.eraseColor(Color.Gray.toArgb())
                    continuation.resume(fallbackBitmap)
                }
            })
    }

/**
 * Crée un marqueur rond avec une bordure.
 */
private fun createRoundMarkerWithBorder(
    imageBitmap: Bitmap,
    size: Int,
    borderWidth: Float,
    borderColor: Int
): Bitmap {
    val output = createBitmap(size, size)
    val canvas = Canvas(output)

    // Calculer le rayon et le centre
    val radius = (size / 2f) - borderWidth
    val centerX = size / 2f
    val centerY = size / 2f

    // Configurer le Paint pour l'image
    val paint = Paint()
    paint.isAntiAlias = true

    // Dessiner la bordure
    paint.color = borderColor
    canvas.drawCircle(centerX, centerY, radius + borderWidth, paint)

    // Configurer le masque circulaire
    val shader = BitmapShader(imageBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    paint.shader = shader

    // Dessiner l'image circulaire
    canvas.drawCircle(centerX, centerY, radius, paint)

    return output
}

/**
 * Crée un marqueur par défaut lorsqu'aucune image n'est disponible.
 */
private fun createDefaultMarker(context: Context): Bitmap {
    val size = 100
    val bitmap = createBitmap(size, size)
    val canvas = Canvas(bitmap)

    val paint = Paint().apply {
        isAntiAlias = true
        color = Color.Blue.toArgb()
        style = Paint.Style.FILL
    }

    // Dessiner un cercle bleu
    canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)

    // Dessiner une bordure blanche
    paint.apply {
        color = Color.White.toArgb()
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - 2f, paint)

    return bitmap
}
// https://docs.mapbox.com/android/maps/examples/add-point-annotations/