package com.bythewayapp.ui.screens.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bythewayapp.ui.viewModels.PrivyLoginUiState

/**
 * Composant pour gérer les différents types d'erreurs dans l'écran de connexion Privy
 */
@Composable
fun PrivyLoginErrorHandler(
    error: PrivyLoginUiState.Error,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    when (error) {
        is PrivyLoginUiState.Error.NetworkError -> {
            NetworkErrorScreen(
                errorMessage = error.message,
                retryAction = onRetry,
                modifier = modifier,
                contentPadding = contentPadding
            )
        }
        is PrivyLoginUiState.Error.ValidationError -> {
            ValidationErrorScreen(
                field = error.field,
                errorMessage = error.message,
                retryAction = onRetry,
                modifier = modifier,
                contentPadding = contentPadding
            )
        }
        is PrivyLoginUiState.Error.AuthError -> {
            AuthErrorScreen(
                errorMessage = error.message,
                retryAction = onRetry,
                modifier = modifier,
                contentPadding = contentPadding
            )
        }
        is PrivyLoginUiState.Error.GenericError -> {
            UnknownErrorScreen(
                errorMessage = error.message,
                retryAction = onRetry,
                modifier = modifier,
                contentPadding = contentPadding
            )
        }
    }
}