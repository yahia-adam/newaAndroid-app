package com.bythewayapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bythewayapp.R
import com.bythewayapp.model.Event
import com.bythewayapp.ui.componets.EventList
import com.bythewayapp.ui.componets.ListButton
import com.bythewayapp.ui.componets.MapButton

@Composable
fun EventListView(
    modifier: Modifier = Modifier,
    events: List<Event>,
    onTragleMapClick: () -> Unit,
    onRetryClick: () -> Unit
) {
    Box (
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,

    ) {
        if (events.isNotEmpty()) {
            EventList(events = events)
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column (
                    modifier = Modifier.padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Aucun résultat trouvé. Essayez d'élargir votre recherche ou de modifier vos filtres.",
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRetryClick ) {
                        Text(text = stringResource(R.string.r_essayer))
                    }
                }

            }
        }
        MapButton(
            onClick = onTragleMapClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}