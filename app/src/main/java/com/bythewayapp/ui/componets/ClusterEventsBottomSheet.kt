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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.sharp.DateRange
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
import androidx.compose.ui.graphics.Brush
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
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.material3.Surface
import androidx.compose.foundation.shape.CircleShape

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
            MinimalistEventCard(event = event, onClick = { onEventClick(event) })
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
                        text = startDate.localTime,
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

/**
 * Carte d'événement améliorée avec un design moderne
 */
@Composable
fun EnhancedEventCard(
    event: Event,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(180.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Image d'arrière-plan qui occupe toute la carte
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(event.images?.firstOrNull()?.url)
                    .crossfade(true)
                    .build(),
                contentDescription = "Event background",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .placeholder(
                        visible = event.images.isNullOrEmpty(),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        highlight = PlaceholderHighlight.shimmer(highlightColor = Color.White)
                    )
            )

            // Overlay gradient pour améliorer la lisibilité du texte
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 0f,
                            endY = 500f
                        )
                    )
            )

            // Badge de date
            DateBadge(
                date = event.dates?.start?.localDate,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            )

            // Informations de l'événement en bas
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Nom de l'événement
                Text(
                    text = event.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Ligne avec icône et heure
                event.dates?.start?.let { startDate ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Sharp.DateRange,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = startDate.localTime,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Ligne avec icône et lieu
                event.embedded?.venues?.firstOrNull()?.let { venue ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Place,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = venue.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

/**
 * Badge affichant la date de l'événement
 */
@Composable
fun DateBadge(date: String?, modifier: Modifier = Modifier) {
    if (date == null) return

    val formattedDate = formatDateForBadge(date) // Fonction qui retourne jour et mois
    val (day, month) = formattedDate

    Surface(
        modifier = modifier.size(60.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = 4.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = day,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = month,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

/**
 * Fonction pour extraire le jour et le mois à partir d'une date
 */
fun formatDateForBadge(dateString: String): Pair<String, String> {
    // Supposons que dateString est au format "2023-12-25"
    // Cette fonction doit être adaptée selon votre format de date
    val date = try {
        LocalDate.parse(dateString)
    } catch (e: Exception) {
        return Pair("--", "---")
    }

    val day = date.dayOfMonth.toString()
    val month = date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())

    return Pair(day, month)
}

/**
 * Carte d'événement avec un design minimaliste et élégant
 */
@Composable
fun MinimalistEventCard(
    event: Event,
    onClick: () -> Unit
) {
    val cardColor = MaterialTheme.colorScheme.surface
    val primaryColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
    ) {
        // Section supérieure avec l'image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            // Image principale
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(event.images?.firstOrNull()?.url)
                    .crossfade(true)
                    .build(),
                contentDescription = "Event image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .placeholder(
                        visible = event.images.isNullOrEmpty(),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        highlight = PlaceholderHighlight.shimmer(highlightColor = Color.White)
                    )
            )

            // Tags catégorie (optionnel)
            event.classifications?.firstOrNull()?.segment?.name?.let { category ->
                Surface(
                    color = primaryColor.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Prix (si disponible)
            event.priceRanges?.firstOrNull()?.let { priceRange ->
                Surface(
                    color = Color.White.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Text(
                        text = "${priceRange.min} ${priceRange.currency}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Section inférieure avec les détails
        Surface(
            color = cardColor,
            shadowElevation = 2.dp,
            shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Indicateur de date
                event.dates?.start?.let { startDate ->
                    val dateInfo = parseEventDate(startDate.localDate)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .width(42.dp)
                    ) {
                        Text(
                            text = dateInfo.dayOfMonth,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = primaryColor
                        )

                        Text(
                            text = dateInfo.month,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Ligne verticale de séparation
                    // Ligne verticale de séparation
                    Box(
                        modifier = Modifier
                            .height(50.dp)
                            .width(1.dp)
                            .padding(horizontal = 8.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                }

                // Détails de l'événement
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

                    // Heure de l'événement
                    event.dates?.start?.localTime?.let { time ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DateRange,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = formatTime(time),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Lieu de l'événement
                    event.embedded?.venues?.firstOrNull()?.let { venue ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Place,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = venue.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Bouton d'action
                IconButton(
                    onClick = onClick,
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "View details",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

/**
 * Classe pour stocker les informations de date
 */
data class DateInfo(
    val dayOfMonth: String,
    val month: String,
    val dayOfWeek: String
)

/**
 * Fonction pour extraire les informations de date
 */
fun parseEventDate(dateString: String): DateInfo {
    // Supposons que dateString est au format "2023-12-25"
    val date = try {
        LocalDate.parse(dateString)
    } catch (e: Exception) {
        return DateInfo("--", "---", "---")
    }

    val dayOfMonth = date.dayOfMonth.toString()
    val month = date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())

    return DateInfo(dayOfMonth, month, dayOfWeek)
}

/**
 * Fonction pour formater l'heure
 */
fun formatTime(timeString: String): String {
    // Supposons que timeString est au format "18:30:00"
    // Cette fonction doit être adaptée selon votre format d'heure
    return timeString.substringBeforeLast(":") // Retourne "18:30"
}

/**
 * Imports nécessaires pour le code ci-dessus
 */
// import androidx.compose.foundation.background
// import androidx.compose.foundation.layout.*
// import androidx.compose.foundation.shape.CircleShape
// import androidx.compose.foundation.shape.RoundedCornerShape
// import androidx.compose.material.icons.Icons
// import androidx.compose.material.icons.outlined.LocationOn
// import androidx.compose.material.icons.outlined.Schedule
// import androidx.compose.material.icons.rounded.KeyboardArrowRight
// import androidx.compose.material3.*
// import androidx.compose.runtime.Composable
// import androidx.compose.ui.Alignment
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.draw.clip
// import androidx.compose.ui.graphics.Color
// import androidx.compose.ui.layout.ContentScale
// import androidx.compose.ui.text.font.FontWeight
// import androidx.compose.ui.text.style.TextOverflow
// import androidx.compose.ui.unit.dp
// import coil.compose.AsyncImage
// import coil.request.ImageRequest
// import com.google.accompanist.placeholder.PlaceholderHighlight
// import com.google.accompanist.placeholder.material.placeholder
// import com.google.accompanist.placeholder.material.shimmer
// import java.time.LocalDate
// import java.time.format.TextStyle
// import java.util.*