package com.bythewayapp.ui.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bythewayapp.core.PrivyAuthState
import com.bythewayapp.core.PrivyManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.privy.auth.PrivyUser
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PrivyLoginUiState {
    data object Loading : PrivyLoginUiState()
    data object Ready : PrivyLoginUiState()
    data class OTPSent(val email: String) : PrivyLoginUiState()
    data object Success : PrivyLoginUiState()
    data class Error(val message: String) : PrivyLoginUiState()
}

@HiltViewModel
class PrivyLoginViewModel @Inject constructor(
    private val privyManager: PrivyManager
) : ViewModel() {

    private val TAG = "PrivyLoginViewModel"
    var uiState by mutableStateOf<PrivyLoginUiState>(PrivyLoginUiState.Ready)
        private set

    var email by mutableStateOf("")
        private set

    var otpCode by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            privyManager.observeAuthState()
            privyManager.authState.collectLatest { authState ->
                if (authState == PrivyAuthState.Authenticated) {
                    uiState = PrivyLoginUiState.Success
                    Log.d(TAG, "User already authenticated, redirecting to home")
                } else if (authState == PrivyAuthState.Unauthenticated) {
                    uiState = PrivyLoginUiState.Ready
                    Log.d(TAG, "User not authenticated, showing login screen")
                }
            }
        }
    }

    fun updateEmail(newEmail: String) {
        email = newEmail
    }

    fun updateOtpCode(newCode: String) {
        otpCode = newCode
    }

    fun sendOTP() {
        Log.d(TAG, "Enter send opt code")

        if (email.isBlank()) {
            Log.e(TAG, "Email cannot be empty")
            uiState = PrivyLoginUiState.Error("Email cannot be empty")
            return
        }

        uiState = PrivyLoginUiState.Loading
        viewModelScope.launch {
            privyManager.sendOTPCode(email).fold(
                onSuccess = {
                    Log.d(TAG, "Opt sended succesffuly")
                    uiState = PrivyLoginUiState.OTPSent(email)
                },
                onFailure = { exception ->
                    Log.e(TAG, "Error sending OTP", exception)
                    uiState = PrivyLoginUiState.Error("Failed to send verification code: ${exception.message}")
                }
            )
        }
    }

    fun verifyOTP() {
        if (otpCode.isBlank()) {
            uiState = PrivyLoginUiState.Error("Verification code cannot be empty")
            return
        }

        uiState = PrivyLoginUiState.Loading
        viewModelScope.launch {
            privyManager.verifyOTPCode(email, otpCode).fold(
                onSuccess = { user ->
                    if (user != null) {
                        uiState = PrivyLoginUiState.Success
                    } else {
                        uiState = PrivyLoginUiState.Error("Authentication failed: User is null")
                    }
                },
                onFailure = { exception ->
                    Log.e(TAG, "Error verifying OTP", exception)
                    uiState = PrivyLoginUiState.Error("Failed to verify code: ${exception.message}")
                }
            )
        }
    }

    fun resendOTP() {
        sendOTP()
    }

    fun goBackToEmailInput() {
        otpCode = ""
        uiState = PrivyLoginUiState.Ready
    }

    fun retry() {
        uiState = PrivyLoginUiState.Ready
    }
}
