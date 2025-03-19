package com.bythewayapp.ui.componets

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.placeholder.shimmer
import androidx.core.net.toUri

/**
 * BottomSheet pour afficher les détails d'un événement individuel
 */
@Composable
fun EventDetailBottomSheet(
    isVisible: Boolean,
    event: Event?,
    onClose: () -> Unit,
    onNavigate: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    var bottomSheetState by remember { mutableStateOf(BottomSheetState.COLLAPSED) }
    val bottomSheetHeight by animateDpAsState(
        targetValue = when {
            !isVisible -> 0.dp
            bottomSheetState == BottomSheetState.EXPANDED -> 600.dp
            else -> 850.dp
        },
        label = "detailBottomSheetHeight"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        if (isVisible && event != null) {
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
                    DetailBottomSheetHeader(
                        title = event.name,
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

                    // Contenu détaillé de l'événement
                    EventDetailContent(
                        event = event,
                    )
                }
            }
        }
    }
}

/**
 * En-tête du BottomSheet de détail avec titre et boutons
 */
@Composable
fun DetailBottomSheetHeader(
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
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
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
 * Contenu détaillé d'un événement
 */
@Composable
fun EventDetailContent(
    event: Event,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ) {
        // Image principale de l'événement
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(event.images?.firstOrNull()?.url)
                .crossfade(true)
                .build(),
            contentDescription = "Event image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
                .placeholder(
                    visible = event.images.isNullOrEmpty(),
                    color = LightGray,
                    highlight = PlaceholderHighlight.shimmer(highlightColor = Color.White)
                )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Date et heure
        event.dates?.start?.let { startDate ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = formatDate(startDate.localDate),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = formatTime(startDate.dateTime),
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lieu et Prix
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Lieu (à gauche)
            event.embedded?.venues?.firstOrNull()?.let { venue ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = venue.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )

                        venue.address?.line1?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkGray
                            )
                        }

                        Row {
                            venue.city?.name?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = DarkGray
                                )
                            }

                            venue.postalCode?.let {
                                Text(
                                    text = " - $it",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = DarkGray
                                )
                            }
                        }
                    }
                }
            }

            // Prix (à droite)
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = "Prix",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                event.priceRanges?.firstOrNull()?.let { priceRange ->
                    val minPrice = "%.2f".format(priceRange.min)
                    val maxPrice = "%.2f".format(priceRange.max)
                    val currency = priceRange.currency ?: "€"

                    Text(
                        text = "$minPrice - $maxPrice $currency",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                } ?: Text(
                    text = "Non disponible",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkGray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Description (si disponible)
        event.description?.let {
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkGray
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Bouton de réservation qui ouvre l'URL dans le navigateur
        val context = LocalContext.current
        Button(
            onClick = {
                event.url?.let { url ->
                    // Créer une Intent pour ouvrir l'URL dans le navigateur
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    context.startActivity(intent)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            enabled = event.url != null
        ) {
            Text(
                text = "Réserver",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // Espace supplémentaire en bas
        Spacer(modifier = Modifier.height(16.dp))
    }
}