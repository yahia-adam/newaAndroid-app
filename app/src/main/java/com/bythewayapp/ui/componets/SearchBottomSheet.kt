package com.bythewayapp.ui.componets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.format.DateTimeFormatter
import java.time.Instant
import java.time.ZoneId
import androidx.compose.runtime.mutableStateListOf


@Composable
fun SearchBottomSheet(
    modifier: Modifier = Modifier,
    onQueryChanged: (String) -> Unit,
    suggestions: List<String>,
    onQueryClicqed: (String) -> Unit,
    onApplyFilter: (startDate: String?, endDate: String?, selectedGenres: List<String>, radius: Int) -> Unit,
) {
    // État pour contrôler l'affichage du FilterBottomSheet
    var showFilterSheet by remember { mutableStateOf(false) }

    var radius by remember { mutableIntStateOf(50) }

    var startDate by remember { mutableStateOf<String?>(null) }
    var endDate by remember { mutableStateOf<String?>(null) }

    var selectedGenres by remember { mutableStateOf<List<String>>(emptyList()) }


    Column {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(0.dp)
                .background(color = MaterialTheme.colorScheme.surface),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MySearchBar(
                onShowFilter = { showFilterSheet = true },
                onQueryChanged = onQueryChanged,
                suggestions = suggestions,
                onQueryClicqed = onQueryClicqed
            )
        }
    }

    // Afficher le FilterBottomSheet lorsque showFilterSheet est true
    if (showFilterSheet) {
        FilterBottomSheet(
            isVisible = true,
            onDismiss = { showFilterSheet = false },
            onApplyFilter = onApplyFilter,
            radius = radius,
            onRadiusChanged = {radius = it },
            startDate = startDate,
            endDate = endDate,
            onDateChange = { start, end ->
                startDate = start
                endDate = end
            },
            selectedGenres = selectedGenres,
            onGenresSelected = { selectedGenres = it }
        )
    }
}

@Composable
fun FilterChipExample(
    modifier: Modifier = Modifier,
    text: String
) {
    var selected by remember { mutableStateOf(false) }

    FilterChip(
        modifier = modifier,
        onClick = { selected = !selected },
        label = {
            Text(text = text)
        },
        selected = selected,
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterGenre(
    modifier: Modifier = Modifier,
    selectedGenres: List<String>,
    onGenresSelected: (List<String>) -> Unit
) {

    val selected = remember { mutableStateListOf<String>().apply { addAll(selectedGenres) } }

    val genres = listOf(
        "Musique",
        "Alternatif",
        "Blues",
        "Chanson Francaise",
        "Classique",
        "Country",
        "Hip-Hop/Rap",
        "Jazz",
        "Métal",
        "Reggae",
        "Rock"
    )

    Column (
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        /*
        Text(
            text = "Genre",
            modifier = Modifier.padding(bottom = 8.dp)
        )*/

        FlowRow(
            horizontalArrangement = Arrangement.Start,
            maxItemsInEachRow = Int.MAX_VALUE
        ) {
            genres.forEach { genre ->
                FilterChip(
                    modifier = Modifier.padding(end = 4.dp),
                    onClick = {
                        if (selected.contains(genre)) {
                            selected.remove(genre)
                        } else {
                            selected.add(genre)
                        }
                        onGenresSelected(selected.toList())
                    },
                    label = { Text(genre) },
                    selected = selected.contains(genre),
                    leadingIcon = if (selected.contains(genre)) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = null,
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else null
                )
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDate(
    modifier: Modifier = Modifier,
    startDate: String?,
    endDate: String?,
    onDateChange: (start: String?, end: String?) -> Unit
) {
    var startDateMillis by remember { mutableStateOf<Long?>(null) }
    var endDateMillis by remember { mutableStateOf<Long?>(null) }
    var startDateString by remember { mutableStateOf(startDate) }
    var endDateString by remember { mutableStateOf(endDate) }
    var startDatePickerVisible by remember { mutableStateOf(false) }
    var endDatePickerVisible by remember { mutableStateOf(false) }
    var showErrorSnackbar by remember { mutableStateOf(false) }
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /*Text(
            text = "Dates",
            modifier = Modifier.padding(bottom = 8.dp)
        )*/

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Date de début
            OutlinedCard(
                modifier = Modifier.weight(1f),
                onClick = { startDatePickerVisible = true }
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Date de début",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (startDate.isNullOrEmpty()) "Choisir" else startDate,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Date de fin
            OutlinedCard(
                modifier = Modifier.weight(1f),
                onClick = { endDatePickerVisible = true }
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Date de fin",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (endDate.isNullOrEmpty()) "Choisir" else endDate,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Affichage du message d'erreur si nécessaire
        if (showErrorSnackbar) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "La date de début doit être antérieure à la date de fin",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }

    // Date Picker pour la date de début
    if (startDatePickerVisible) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = startDateMillis
        )

        DatePickerDialog(
            onDismissRequest = { startDatePickerVisible = false },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { newStartDateMillis ->
                            // Vérifier si la date de début est après la date de fin
                            if (endDateMillis != null && newStartDateMillis > endDateMillis!!) {
                                showErrorSnackbar = true
                            } else {
                                startDateMillis = newStartDateMillis
                                val localDate = Instant.ofEpochMilli(newStartDateMillis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                startDateString = localDate.format(formatter)
                                showErrorSnackbar = false

                                // 🟢 Notify parent
                                onDateChange(startDateString, endDateString)
                            }
                        }
                        startDatePickerVisible = false
                    }
                ) {
                    Text("Confirmer")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { startDatePickerVisible = false }
                ) {
                    Text("Annuler")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Date Picker pour la date de fin
    if (endDatePickerVisible) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = endDateMillis
        )

        DatePickerDialog(
            onDismissRequest = { endDatePickerVisible = false },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { newEndDateMillis ->
                            // Vérifier si la date de fin est avant la date de début
                            if (startDateMillis != null && startDateMillis!! > newEndDateMillis) {
                                showErrorSnackbar = true
                            } else {
                                endDateMillis = newEndDateMillis
                                val localDate = Instant.ofEpochMilli(newEndDateMillis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                endDateString = localDate.format(formatter)
                                showErrorSnackbar = false

                                // 🟢 Notify parent
                                onDateChange(startDateString, endDateString)
                            }
                        }
                        endDatePickerVisible = false
                    }
                ) {
                    Text("Confirmer")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { endDatePickerVisible = false }
                ) {
                    Text("Annuler")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    modifier: Modifier = Modifier,
    isVisible: Boolean = false,
    onDismiss: () -> Unit,

    radius: Int,
    onRadiusChanged: (Int) -> Unit,

    startDate: String?,
    endDate: String?,
    onDateChange: (start: String?, end: String?) -> Unit,

    selectedGenres: List<String>,
    onGenresSelected: (List<String>) -> Unit,

    onApplyFilter: (startDate: String?, endDate: String?, selectedGenres: List<String>, radius: Int) -> Unit,
) {

    if (isVisible) {
        val sheetState = rememberModalBottomSheetState()

        ModalBottomSheet(
            onDismissRequest = { onDismiss() },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MyFilterSlider(
                    initialRadius = radius,
                    onRadiusChanged = onRadiusChanged
                )

                // Passer les setters
                FilterDate(
                    startDate = startDate,
                    endDate = endDate,
                    onDateChange = onDateChange
                )

                FilterGenre(
                    selectedGenres = selectedGenres,
                    onGenresSelected = onGenresSelected
                )

                PrimaryButton(
                    modifier = Modifier.padding(16.dp),
                    text = "Appliquer le filtre",
                    onclick = {
                        onApplyFilter(startDate, endDate, selectedGenres, radius)
                        onDismiss()
                    }
                )
            }
        }
    }
}
