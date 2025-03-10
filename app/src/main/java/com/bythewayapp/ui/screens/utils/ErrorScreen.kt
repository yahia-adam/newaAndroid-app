package com.bythewayapp.ui.screens.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bythewayapp.R


@Composable
fun ErrorScreen(
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