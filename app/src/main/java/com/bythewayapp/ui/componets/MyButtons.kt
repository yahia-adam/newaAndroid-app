package com.bythewayapp.ui.componets

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    onclick: () -> Unit = {},
    text: String = "Mon Button",
) {
    Button(
        modifier = modifier,
        onClick = {
            onclick()
        }
    ) {
        Text(text = text)
    }
}