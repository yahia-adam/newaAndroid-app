package com.bythewayapp.ui.componets

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MyEmailTextField(
    modifier: Modifier = Modifier,
    email: String,
    onEmailChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = email,
        onValueChange = { onEmailChanged(it) },
        label = {
            Text(text = "Entrer votre address email")
        },
        leadingIcon = {
            Icon(
                Icons.Default.Email,
                contentDescription = "email icons"
            )
        }
    )
}

@Composable
fun MyOptCodeTextField(
    modifier: Modifier = Modifier,
    code: String,
    onCodeChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = code,
        onValueChange = { onCodeChanged(it) },
        label = {
            Text(text = "Entrez le code reçu par email")
        },
        leadingIcon = {
            Icon(
                Icons.Default.Lock,
                contentDescription = "code icons"
            )
        }
    )
}