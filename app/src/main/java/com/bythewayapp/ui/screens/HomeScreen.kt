package com.bythewayapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bythewayapp.model.Event
import com.bythewayapp.ui.componets.SearchBottomSheet
import com.bythewayapp.ui.screens.utils.EnableUserLocationScreen
import com.bythewayapp.ui.viewModels.BythewayUiSate
import com.bythewayapp.ui.screens.utils.InternetConnectionErrorScreen
import com.bythewayapp.ui.screens.utils.LoadingScreen
import com.bythewayapp.ui.screens.utils.UnknownErrorScreen
import com.bythewayapp.ui.viewModels.HomeViewModel

@Composable
fun ResultScreen(
    modifier: Modifier = Modifier,
    events: List<Event>,
    long: Double = 47.233334,
    lat: Double = 2.154925,
    onRetryClick: () -> Unit
) {
    var isMapView by remember { mutableStateOf(true) }

    if (isMapView) {
        MapBoxView(
            events = events,
            long = long,
            lat = lat,
            onTragleListClick = { isMapView = false }
        )
    } else {
        EventListView(
            events = events,
            onTragleMapClick = {isMapView = true},
            onRetryClick = onRetryClick
        )
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {

    Scaffold(
        modifier = modifier,
        bottomBar = {
            SearchBottomSheet(
                onQueryChanged = { query -> viewModel.fetchEventsSuggestions(query) },
                suggestions = viewModel.eventsuggestions,
                onQueryClicqed = { value -> viewModel.onKeywordChanged(value) },
                onApplyFilter = { start, end, genres, radius ->
                    viewModel.updateFilters(start, end, genres, radius)
                    viewModel.applyFilter()
                }
            )
        }
    ) {  innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            when (val bythewayUiSate = viewModel.bythewayUiSate) {

                is BythewayUiSate.Success -> {
                    ResultScreen(
                        events = bythewayUiSate.events,
                        modifier = modifier,
                        long = bythewayUiSate.long,
                        lat = bythewayUiSate.lat,
                        onRetryClick = {viewModel.reInitialise()}
                    )
                }

                is BythewayUiSate.Loading -> {
                    LoadingScreen(
                        modifier = modifier,
                    )
                }

                is BythewayUiSate.EnableUserLocation -> {
                    EnableUserLocationScreen(
                        retryAction = { viewModel.getUserLocation() },
                        errorMessage = bythewayUiSate.message,
                        errorType = bythewayUiSate.type,
                        modifier = modifier,
                    )
                }

                is BythewayUiSate.InternetConnectionError -> {
                    InternetConnectionErrorScreen(
                        retryAction = { viewModel.reInitialise() },
                        errorMessage = bythewayUiSate.message,
                        modifier = modifier,
                    )
                }

                is BythewayUiSate.UnknownError -> {
                    UnknownErrorScreen(
                        retryAction = { viewModel.reInitialise() },
                        errorMessage = bythewayUiSate.message,
                        modifier = modifier,
                    )
                }
            }
        }
    }
}

