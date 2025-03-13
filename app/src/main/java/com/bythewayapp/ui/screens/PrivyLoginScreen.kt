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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bythewayapp.ui.componets.MyEmailTextField
import com.bythewayapp.ui.componets.MyOptCodeTextField
import com.bythewayapp.ui.componets.PrimaryButton
import com.bythewayapp.ui.screens.utils.ErrorScreen
import com.bythewayapp.ui.screens.utils.LoadingScreen
import com.bythewayapp.ui.viewModels.PrivyLoginUiState
import com.bythewayapp.ui.viewModels.PrivyLoginViewModel

@Composable
fun PrivyLoginEmailStartScreenContent(
    email: String,
    onEmailChanged: (String) -> Unit,
    onSendEmail: () -> Unit,
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
        Text(text = "Connection par email")

        Spacer(modifier = Modifier.height(16.dp))

        MyEmailTextField (
            modifier = Modifier,
            email = email,
            onEmailChanged = onEmailChanged
        )

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            modifier = Modifier,
            onclick = onSendEmail,
            text = "Suivant"
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
                contentDescription = "Go back",
                modifier = Modifier.clickable { onGoBack() }
            )
        }

        Column (
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Vérification du code")

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Un code a été envoyé à $email")

            Spacer(modifier = Modifier.height(16.dp))

            MyOptCodeTextField(
                modifier = Modifier,
                code = otpCode,
                onCodeChanged = onCodeChanged
            )

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                modifier = Modifier,
                onclick = onVerifyCode,
                text = "Vérifier"
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Renvoyer le code",
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
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val uiState = viewModel.uiState

    // Handle automatic navigation to home when already authenticated
    LaunchedEffect(uiState) {
        if (uiState is PrivyLoginUiState.Success) {
            onLoginSuccess()
        }
    }

    when (uiState) {
        is PrivyLoginUiState.Loading -> LoadingScreen()

        is PrivyLoginUiState.Error -> ErrorScreen(
            errorMessage = uiState.message,
            retryAction = { viewModel.retry() }
        )

        is PrivyLoginUiState.Ready -> PrivyLoginEmailStartScreenContent(
            email = viewModel.email,
            onEmailChanged = { viewModel.updateEmail(it) },
            onSendEmail = { viewModel.sendOTP() },
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
            modifier = modifier,
            contentPadding = contentPadding
        )

        is PrivyLoginUiState.Success -> {
            // Just show loading while redirection happens via LaunchedEffect
            LoadingScreen()
        }
    }
}
