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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bythewayapp.ui.componets.MyEmailTextField
import com.bythewayapp.ui.componets.MyOptCodeTextField
import com.bythewayapp.ui.componets.PrimaryButton
import com.bythewayapp.ui.screens.utils.ErrorScreen
import com.bythewayapp.ui.screens.utils.LoadingScreen
import com.bythewayapp.ui.viewModels.PrivyLoginUiState
import com.bythewayapp.ui.viewModels.PrivyLoginViewModel

@Composable
fun PrivyLoginEmailStartScreenContent(
    modifier: Modifier = Modifier,
    sendEmail: () -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var email by remember {
            mutableStateOf("")
        }
        Text(text = "Connection par email")

        Spacer(modifier = Modifier.height(16.dp))

        MyEmailTextField (
            modifier = Modifier,
            email,
            onEmailChanged = {
                email = it
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            modifier = Modifier,
            onclick = sendEmail,
            "Suivant"
        )
    }
}

@Composable
@Preview(showBackground = true)
fun PrivyLoginEmailEndScreenContent(
    modifier: Modifier = Modifier,
    verifyCode: () -> Unit = {},
    resendCode: () -> Unit = {},
    goBack: () -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding),

        ) {
        Row (
            modifier = Modifier.padding(16.dp)
        ){
            Icon(Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = "arrow back icons",
                modifier = Modifier.clickable{
                    goBack
                }
            )
        }

        Column (
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            var optCode by remember {
                mutableStateOf("")
            }

            Text(text = "Vérification du code")

            Spacer(modifier = Modifier.height(16.dp))

            MyOptCodeTextField(
                modifier = Modifier,
                optCode,
                onCodeChanged = {
                    optCode = it
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                modifier = Modifier,
                onclick = verifyCode,
                "Vérifier"
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Renvoyer le code",
                modifier = Modifier.clickable {
                    resendCode
                }
            )
        }
    }
}

@Composable
fun PrivyLoginEmailStartScreen(
    viewModel: PrivyLoginViewModel = hiltViewModel<PrivyLoginViewModel>(),
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val uiState = viewModel.uiState

    when (val currentState = uiState) {
        is PrivyLoginUiState.Loading -> LoadingScreen()
        is PrivyLoginUiState.Error -> ErrorScreen(
            errorMessage = currentState.message,
            retryAction = { viewModel.retry() }
        )
        is PrivyLoginUiState.Ready -> PrivyLoginEmailStartScreenContent(
            modifier = Modifier,
            sendEmail = {},
            contentPadding = contentPadding
        )
    }
}

@Composable
fun PrivyLoginEmailEndScreen(
    viewModel: PrivyLoginViewModel = hiltViewModel<PrivyLoginViewModel>(),
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val uiState = viewModel.uiState

    when (val currentState = uiState) {
        is PrivyLoginUiState.Loading -> LoadingScreen()
        is PrivyLoginUiState.Error -> ErrorScreen(
            errorMessage = currentState.message,
            retryAction = { viewModel.retry() }
        )
        is PrivyLoginUiState.Ready -> PrivyLoginEmailEndScreenContent(
            modifier = Modifier,
            verifyCode = {},
            resendCode = {},
            goBack = {},
            contentPadding = contentPadding
        )
    }
}