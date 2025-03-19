package com.bythewayapp.ui.componets

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bythewayapp.model.Event
import com.bythewayapp.ui.theme.CardBackground
import com.bythewayapp.ui.theme.DarkGray
import com.bythewayapp.ui.theme.LightGray
import com.bythewayapp.utils.formatDate
import com.bythewayapp.utils.formatTime
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.shimmer

/**
 * État d'un BottomSheet
 */
enum class BottomSheetState {
    COLLAPSED,
    EXPANDED
}

/**
 * BottomSheet pour afficher les événements d'un cluster
 */
@Composable
fun EventsBottomSheet(
    isVisible: Boolean,
    events: List<Event>,
    onClose: () -> Unit,
    onEventClick: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    var bottomSheetState by remember { mutableStateOf(BottomSheetState.COLLAPSED) }
    val bottomSheetHeight by animateDpAsState(
        targetValue = when {
            !isVisible -> 0.dp
            bottomSheetState == BottomSheetState.EXPANDED -> 500.dp
            else -> 850.dp
        },
        label = "bottomSheetHeight"
    )



    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        if (isVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { onClose() }
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(bottomSheetHeight)
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Header
                    BottomSheetHeader(
                        title = "Événements",
                        isExpanded = bottomSheetState == BottomSheetState.EXPANDED,
                        onToggle = {
                            bottomSheetState = if (bottomSheetState == BottomSheetState.EXPANDED) {
                                BottomSheetState.COLLAPSED
                            } else {
                                BottomSheetState.EXPANDED
                            }
                        },
                        onClose = onClose
                    )

                    // Liste des événements
                    EventList(events = events, onEventClick = onEventClick)
                }
            }
        }
    }
}

/**
 * En-tête du BottomSheet avec titre et boutons
 */
@Composable
fun BottomSheetHeader(
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Row {

            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Fermer",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

/**
 * Liste des événements dans le BottomSheet
 */
@Composable
fun EventList(
    events: List<Event>,
    onEventClick: (Event) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        items(events) { event ->
            EventCard(event = event, onClick = { onEventClick(event) })
            Spacer(modifier = Modifier.height(12.dp))
        }
        // Espace supplémentaire en bas
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

/**
 * Carte d'un événement dans la liste
 */
@Composable
fun EventCard(
    event: Event,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image de l'événement
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(event.images?.firstOrNull()?.url)
                    .crossfade(true)
                    .build(),
                contentDescription = "Event image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .placeholder(
                        visible = event.images.isNullOrEmpty(),
                        color = LightGray,
                        highlight = PlaceholderHighlight.shimmer(highlightColor = Color.White)
                    )
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Informations sur l'événement
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = event.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                event.dates?.start?.let { startDate ->
                    Text(
                        text = formatDate(startDate.localDate),
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkGray
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = formatTime(startDate.dateTime),
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkGray
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                event.embedded?.venues?.firstOrNull()?.let { venue ->
                    Text(
                        text = venue.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}