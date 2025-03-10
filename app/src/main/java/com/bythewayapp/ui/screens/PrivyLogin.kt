package com.bythewayapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController

@Composable
fun PrivyLoginEmailStartScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Column (
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var text by remember {
            mutableStateOf("")
        }

        Text(text = "Connection par email")
        TextField(
            value = text,
            onValueChange = { text = it},
            leadingIcon = {
                Icon(
                    Icons.Default.Email,
                    contentDescription = "email icons"
                )
            }
        )
        Button(
            onClick = {}
        ) {
            Text(text = "Connection")
        }
    }
}

@Composable
fun PrivyLoginEmailEndScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    email: String
) {
    
}