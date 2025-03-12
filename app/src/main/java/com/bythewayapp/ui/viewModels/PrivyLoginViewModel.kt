package com.bythewayapp.ui.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bythewayapp.core.PrivyManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.privy.auth.PrivyUser
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PrivyLoginUiState {
    object Loading : PrivyLoginUiState()
    object Ready : PrivyLoginUiState()
    data class OTPSent(val email: String) : PrivyLoginUiState()
    object Success : PrivyLoginUiState()
    data class Error(val message: String) : PrivyLoginUiState()
}

@HiltViewModel
class PrivyLoginViewModel @Inject constructor(
    private val privyManager: PrivyManager
) : ViewModel() {

    var uiState by mutableStateOf<PrivyLoginUiState>(PrivyLoginUiState.Ready)
        private set

    var email by mutableStateOf("")
        private set

    var otpCode by mutableStateOf("")
        private set

    init {
        // Start observing auth state
        privyManager.observeAuthState()
    }

    fun updateEmail(newEmail: String) {
        email = newEmail
    }

    fun updateOtpCode(newCode: String) {
        otpCode = newCode
    }

    fun sendOTP() {
        if (email.isBlank()) {
            uiState = PrivyLoginUiState.Error("Email cannot be empty")
            return
        }

        uiState = PrivyLoginUiState.Loading
        viewModelScope.launch {
            privyManager.sendOTPCode(email).fold(
                onSuccess = {
                    uiState = PrivyLoginUiState.OTPSent(email)
                },
                onFailure = { exception ->
                    Log.e("PrivyLoginViewModel", "Error sending OTP", exception)
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
                    Log.e("PrivyLoginViewModel", "Error verifying OTP", exception)
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
