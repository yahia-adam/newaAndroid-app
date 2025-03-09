package com.bythewayapp.ui.theme.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bythewayapp.ui.viewModels.BythewayUiSate
import com.bythewayapp.ui.screens.utils.ErrorScreen
import com.bythewayapp.ui.screens.utils.LoadingScreen
import com.bythewayapp.ui.screens.ResultScreen

@Composable
fun HomeScreen(
    bythewayUiSate: BythewayUiSate,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    when(bythewayUiSate) {
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


