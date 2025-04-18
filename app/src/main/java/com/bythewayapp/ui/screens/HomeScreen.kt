package com.bythewayapp.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bythewayapp.core.SettingsHelper
import com.bythewayapp.model.Event
import com.bythewayapp.ui.componets.MapBoxView
import com.bythewayapp.ui.screens.utils.EnableUserLocationScreen
import com.bythewayapp.ui.viewModels.BythewayUiSate
import com.bythewayapp.ui.screens.utils.InternetConnectionErrorScreen
import com.bythewayapp.ui.screens.utils.LoadingScreen
import com.bythewayapp.ui.screens.utils.UnknownErrorScreen
import com.bythewayapp.ui.viewModels.HomeViewModel
import com.bythewayapp.ui.viewModels.LocationErrorType

@Composable
fun ResultScreen(
    keyword: String,
    onKeywordChanged: (String) -> Unit,
    btnSelectedDate: String,
    onDateRangeChanged: (Long, Long) -> Unit,
    events: List<Event>,
    long: Double = 47.233334,
    lat: Double = 2.154925,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
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
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
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
                contentPadding = contentPadding
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