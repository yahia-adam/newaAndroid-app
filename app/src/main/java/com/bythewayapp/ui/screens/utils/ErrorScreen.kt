package com.bythewayapp.ui.screens.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bythewayapp.R
import com.bythewayapp.core.SettingsHelper
import com.bythewayapp.ui.viewModels.LocationErrorType
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bythewayapp.ui.viewModels.PrivyLoginUiState

/**
 * Écran d'erreur réseau pour l'authentification
 */
@Composable
fun NetworkErrorScreen(
    errorMessage: String,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id=R.drawable.baseline_wifi_off_24),
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.error_network_title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        NetworkTroubleshootingCard()

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = retryAction,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(stringResource(R.string.retry))
        }
    }
}


/**
 * Carte de dépannage réseau
 */
@Composable
private fun NetworkTroubleshootingCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.network_troubleshooting_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.network_troubleshooting_step1),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.network_troubleshooting_step2),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.network_troubleshooting_step3),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Écran d'erreur d'authentification pour l'authentification
 */
@Composable
fun AuthErrorScreen(
    errorMessage: String,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id= R.drawable.baseline_error_outline_24),
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.error_authentication_title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = retryAction,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(stringResource(R.string.retry))
        }
    }
}

/**
 * Écran d'erreur de validation pour l'authentification
 */
@Composable
fun ValidationErrorScreen(
    field: String,
    errorMessage: String,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.error_validation_title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = retryAction,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.retry))
        }
    }
}

@Composable
fun EnableUserLocationScreen(
    retryAction: () -> Unit,
    errorMessage: String,
    errorType: LocationErrorType = LocationErrorType.PERMISSION_DENIED,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icône différente selon le type d'erreur
        val icon = when (errorType) {
            LocationErrorType.PERMISSION_DENIED -> R.drawable.baseline_location_off_24
            LocationErrorType.LOCATION_DISABLED -> R.drawable.baseline_gps_off_24
            LocationErrorType.LOCATION_UNAVAILABLE -> R.drawable.baseline_location_searching_24
            LocationErrorType.UNKNOWN -> R.drawable.baseline_error_outline_24
        }

        val iconTint = when (errorType) {
            LocationErrorType.UNKNOWN -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.primary
        }

        Icon(
            painter = painterResource(id= icon),
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = iconTint
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.location_required_title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Instructions spécifiques selon le type d'erreur
        when (errorType) {
            LocationErrorType.PERMISSION_DENIED -> {
                PermissionDeniedInstructions()
            }
            LocationErrorType.LOCATION_DISABLED -> {
                LocationDisabledInstructions()
            }
            else -> {
                // Instructions génériques pour les autres types d'erreurs
                Text(
                    text = stringResource(R.string.location_required_description),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Boutons d'action selon le type d'erreur
        when (errorType) {
            LocationErrorType.PERMISSION_DENIED, LocationErrorType.LOCATION_DISABLED -> {
                Button(
                    onClick = {
                        when (errorType) {
                            LocationErrorType.PERMISSION_DENIED -> SettingsHelper.openAppPermissionsSettings(context)
                            LocationErrorType.LOCATION_DISABLED -> SettingsHelper.openLocationSettings(context)
                            else -> SettingsHelper.openSettings(context)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(stringResource(R.string.open_settings))
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = retryAction,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(stringResource(R.string.retry))
                }
            }
            else -> {
                Button(
                    onClick = retryAction,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(stringResource(R.string.retry))
                }
            }
        }
    }
}

@Composable
private fun PermissionDeniedInstructions() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.enable_location_instructions),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "1. Allez dans 'Réglages' > 'Applications' > 'By The Way'",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "2. Sélectionnez 'Autorisations' > 'Localisation'",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "3. Choisissez 'Autoriser seulement pendant l'utilisation de l'application'",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun LocationDisabledInstructions() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.enable_location_instructions),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "1. Ouvrez le panneau des paramètres rapides en balayant vers le bas",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "2. Activez la localisation (GPS)",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "3. Vous pouvez également aller dans 'Réglages' > 'Localisation' et l'activer",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun InternetConnectionErrorScreen(
    modifier: Modifier = Modifier,
    errorMessage: String,
    retryAction: () -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_connexion_error),
            contentDescription = stringResource(R.string.error_connection_icon)
        )
        Text(
            text = errorMessage,
            modifier = Modifier.padding(16.dp)
        )
        Button(onClick = retryAction ) {
            Text(text = stringResource(R.string.r_essayer))
        }
    }
}

@Composable
fun UnknownErrorScreen(
    modifier: Modifier = Modifier,
    errorMessage: String,
    retryAction: () -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_error_outline_24),
            contentDescription = stringResource(R.string.error_connection_icon)
        )
        Text(
            text = errorMessage,
            modifier = Modifier.padding(16.dp)
        )
        Button(onClick = retryAction ) {
            Text(text = stringResource(R.string.r_essayer))
        }
    }
}