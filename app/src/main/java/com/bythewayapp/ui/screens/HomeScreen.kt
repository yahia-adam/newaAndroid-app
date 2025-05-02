package com.bythewayapp.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
    keyword: String,
    onKeywordChanged: (String) -> Unit,
    btnSelectedDate: String,
    onDateRangeChanged: (Long, Long) -> Unit,
    events: List<Event>,
    long: Double = 47.233334,
    lat: Double = 2.154925,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    isMapview: Boolean
) {

    if (isMapview) {
        MapBoxView(
            keyword = keyword,
            onKeywordChanged = onKeywordChanged,
            btnSelectedDate = btnSelectedDate,
            onDateRangeChanged = onDateRangeChanged,
            events = events,
            long = long,
            lat = lat,
            onEventClick = {}
        )
    } else {
        EventListView(events = events)
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {

    var isMapView by remember { mutableStateOf(true) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            SearchBottomSheet(
                isMapView = isMapView,
                onSwitchBtnTroggle = {isMapView = !isMapView}
            )
        }
    ) {  innerPadding ->

        when(val bythewayUiSate = viewModel.bythewayUiSate) {
            is BythewayUiSate.Success -> {
                ResultScreen(
                    keyword = viewModel.keyword,
                    onKeywordChanged = { viewModel.onKeywordChanged(it) },
                    btnSelectedDate = viewModel.btnSelectedDate,
                    onDateRangeChanged = { startDate, endDate -> viewModel.onDateRangeChanged(startDate, endDate) },
                    events = bythewayUiSate.events,
                    modifier = modifier,
                    long = bythewayUiSate.long,
                    lat = bythewayUiSate.lat,
                    contentPadding = innerPadding,
                    isMapview = isMapView
                )

            }

            is BythewayUiSate.Loading -> {
                LoadingScreen(
                    modifier = modifier,
                    contentPadding = contentPadding
                )
            }

            is BythewayUiSate.EnableUserLocation -> {
                EnableUserLocationScreen(
                    retryAction = { viewModel.getUserLocation() },
                    errorMessage = bythewayUiSate.message,
                    errorType = bythewayUiSate.type,
                    modifier = modifier,
                    contentPadding = contentPadding
                )
            }

            is BythewayUiSate.InternetConnectionError -> {
                InternetConnectionErrorScreen(
                    retryAction = { viewModel.reInitialise() },
                    errorMessage = bythewayUiSate.message,
                    modifier = modifier,
                    contentPadding = contentPadding
                )
            }

            is BythewayUiSate.UnknownError -> {
                UnknownErrorScreen(
                    retryAction = { viewModel.reInitialise() },
                    errorMessage = bythewayUiSate.message,
                    modifier = modifier,
                    contentPadding = contentPadding
                )
            }
        }

    }
}

