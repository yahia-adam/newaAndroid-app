package com.bythewayapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bythewayapp.model.Event
import com.bythewayapp.ui.componets.EventList

@Composable
fun EventListView(
    modifier: Modifier = Modifier,
    events: List<Event>
) {
    EventList(events = events, onEventClick = {})
}