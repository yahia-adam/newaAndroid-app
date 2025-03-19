package com.bythewayapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bythewayapp.R
import com.bythewayapp.ui.componets.MyEmailTextField
import com.bythewayapp.ui.componets.MyOptCodeTextField
import com.bythewayapp.ui.componets.PrimaryButton
import com.bythewayapp.ui.screens.utils.LoadingScreen
import com.bythewayapp.ui.viewModels.PrivyLoginUiState
import com.bythewayapp.ui.viewModels.PrivyLoginViewModel
import com.bythewayapp.ui.screens.utils.PrivyLoginErrorHandler

@Composable
fun PrivyLoginEmailStartScreenContent(
    email: String,
    onEmailChanged: (String) -> Unit,
    onSendEmail: () -> Unit,
    isEmailValid: Boolean,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp)
) {
    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.login_with_email),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        MyEmailTextField (
            modifier = Modifier,
            email = email,
            onEmailChanged = onEmailChanged,
            isError = !isEmailValid,
            errorMessage = if (!isEmailValid) stringResource(R.string.invalid_email_format) else ""
        )

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            modifier = Modifier,
            onclick = onSendEmail,
            text = stringResource(R.string.next),
            enabled = email.isNotBlank() && isEmailValid
        )
    }
}

@Composable
fun PrivyLoginEmailEndScreenContent(
    otpCode: String,
    onCodeChanged: (String) -> Unit,
    onVerifyCode: () -> Unit,
    onResendCode: () -> Unit,
    onGoBack: () -> Unit,
    email: String,
    isOtpValid: Boolean,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
) {
    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding),
    ) {
        Row (
            modifier = Modifier.padding(16.dp)
        ){
            Icon(
                Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = stringResource(R.string.go_back),
                modifier = Modifier.clickable { onGoBack() }
            )
        }

        Column (
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.verify_code),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.code_sent_to, email),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            MyOptCodeTextField(
                modifier = Modifier,
                code = otpCode,
                onCodeChanged = onCodeChanged,
                isError = !isOtpValid,
                errorMessage = if (!isOtpValid) stringResource(R.string.invalid_code) else ""
            )

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                modifier = Modifier,
                onclick = onVerifyCode,
                text = stringResource(R.string.verify),
                enabled = otpCode.isNotBlank() && isOtpValid
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.resend_code),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onResendCode() }
            )
        }
    }
}

@Composable
fun PrivyLoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: PrivyLoginViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp)
) {
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    // Gérer la navigation automatique en cas de succès
    LaunchedEffect(uiState) {
        if (uiState is PrivyLoginUiState.Success) {
            onLoginSuccess()
        }
    }

    // Contenu principal
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Contenu principal
        when (uiState) {
            is PrivyLoginUiState.Loading -> LoadingScreen()

            is PrivyLoginUiState.Ready -> PrivyLoginEmailStartScreenContent(
                email = viewModel.email,
                onEmailChanged = { viewModel.updateEmail(it) },
                onSendEmail = { viewModel.sendOTP() },
                isEmailValid = viewModel.isEmailValid,
                modifier = modifier,
                contentPadding = contentPadding
            )

            is PrivyLoginUiState.OTPSent -> PrivyLoginEmailEndScreenContent(
                otpCode = viewModel.otpCode,
                onCodeChanged = { viewModel.updateOtpCode(it) },
                onVerifyCode = { viewModel.verifyOTP() },
                onResendCode = { viewModel.resendOTP() },
                onGoBack = { viewModel.goBackToEmailInput() },
                email = uiState.email,
                isOtpValid = viewModel.isOtpValid,
                modifier = modifier,
                contentPadding = contentPadding
            )

            is PrivyLoginUiState.Error -> PrivyLoginErrorHandler(
                error = uiState,
                onRetry = { viewModel.retry() },
                modifier = modifier,
                contentPadding = contentPadding
            )

            is PrivyLoginUiState.Success -> {
                // Afficher un écran de chargement pendant que la redirection se fait via LaunchedEffect
                LoadingScreen()
            }
        }

        // Snackbar pour les messages secondaires
        SnackbarHost(hostState = snackbarHostState)
    }
}