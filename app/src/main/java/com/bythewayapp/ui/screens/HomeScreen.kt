package com.bythewayapp.ui.theme.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bythewayapp.ui.viewModels.BythewayUiSate
import com.bythewayapp.ui.screens.utils.ErrorScreen
import com.bythewayapp.ui.screens.utils.LoadingScreen
import com.bythewayapp.ui.viewModels.HomeViewModel
import com.bythewayapp.ui.viewModels.PrivyLoginViewModel
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap

@Composable
fun ResultScreen(
    events: String,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    MapboxMap(
        Modifier.fillMaxSize()
            .padding(contentPadding),
        mapViewportState = rememberMapViewportState {
            setCameraOptions {
                zoom(2.0)
                center(Point.fromLngLat(-98.0, 39.5))
                pitch(0.0)
                bearing(0.0)
            }
        },
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
                bythewayUiSate.events,
                modifier = modifier,
                contentPadding = contentPadding
            )
        }
        is BythewayUiSate.Loading -> {
            LoadingScreen(
                modifier = modifier,
                contentPadding = contentPadding
            )
        }
        is BythewayUiSate.Error -> {
            ErrorScreen(
                retryAction = {},
                errorMessage = bythewayUiSate.message,
                modifier = modifier,
                contentPadding = contentPadding
            )
        }
    }
}

