package com.bythewayapp.ui.componets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.bythewayapp.R

/**
 * Bouton principal avec support pour l'état désactivé
 */
@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    onclick: () -> Unit = {},
    text: String = "Mon Bouton",
    enabled: Boolean = true
) {
    Button(
        modifier = modifier.fillMaxWidth(),
        onClick = { onclick() },
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp,
            disabledElevation = 0.dp
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
fun MapListToggleButton(
    modifier: Modifier = Modifier,
    isMapView: Boolean,
    onToggle: () -> Unit
) {

    val text = if (isMapView) "List" else "Map"

    Button(
        onClick = { onToggle() },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
           if (isMapView) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.AutoMirrored.Default.List,
                    contentDescription = "Switch to List view",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.baseline_map_24),
                    contentDescription = "Switch to Map view",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
