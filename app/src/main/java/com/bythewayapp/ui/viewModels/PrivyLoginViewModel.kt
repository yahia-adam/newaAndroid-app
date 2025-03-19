package com.bythewayapp.ui.viewModels

import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bythewayapp.core.AppError
import com.bythewayapp.core.PrivyAuthState
import com.bythewayapp.core.PrivyManager
import com.bythewayapp.core.PrivyResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PrivyLoginUiState {
    data object Loading : PrivyLoginUiState()
    data object Ready : PrivyLoginUiState()
    data class OTPSent(val email: String) : PrivyLoginUiState()
    data object Success : PrivyLoginUiState()
    sealed class Error : PrivyLoginUiState() {
        data class NetworkError(val message: String) : Error()
        data class ValidationError(val field: String, val message: String) : Error()
        data class AuthError(val message: String) : Error()
        data class GenericError(val message: String) : Error()
    }
}

@HiltViewModel
class PrivyLoginViewModel @Inject constructor(
    private val privyManager: PrivyManager
) : ViewModel() {

    private val TAG = "PrivyLoginViewModel"

    // État de l'UI
    var uiState by mutableStateOf<PrivyLoginUiState>(PrivyLoginUiState.Ready)
        private set

    // Champs de formulaire
    var email by mutableStateOf("")
        private set

    var otpCode by mutableStateOf("")
        private set

    // Validité des champs
    var isEmailValid by mutableStateOf(true)
        private set

    var isOtpValid by mutableStateOf(true)
        private set

    // Job en cours pour gérer correctement les annulations
    private var currentJob: Job? = null

    init {
        observeAuthState()
    }

    // Observer l'état d'authentification Privy
    private fun observeAuthState() {
        viewModelScope.launch {
            privyManager.authState.collectLatest { authState ->
                if (authState == PrivyAuthState.Authenticated) {
                    uiState = PrivyLoginUiState.Success
                    Log.d(TAG, "User already authenticated, redirecting to home")
                } else if (authState == PrivyAuthState.Unauthenticated) {
                    if (uiState !is PrivyLoginUiState.Error && uiState !is PrivyLoginUiState.OTPSent) {
                        uiState = PrivyLoginUiState.Ready
                    }
                    Log.d(TAG, "User not authenticated, showing login screen")
                }
            }
        }
    }

    // Mettre à jour l'email et valider
    fun updateEmail(newEmail: String) {
        email = newEmail
        validateEmail()
    }

    // Mettre à jour le code OTP et valider
    fun updateOtpCode(newCode: String) {
        otpCode = newCode
        isOtpValid = otpCode.length >= 4 // Ajuste selon la longueur de code OTP de Privy
    }

    // Validation de l'email
    private fun validateEmail(): Boolean {
        isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        return isEmailValid
    }

    // Envoi du code OTP
    fun sendOTP() {
        Log.d(TAG, "Attempting to send OTP code")

        // Validation des entrées
        if (!validateEmail()) {
            uiState = PrivyLoginUiState.Error.ValidationError("email", "Format d'email invalide")
            return
        }

        // Annuler le job précédent si nécessaire
        currentJob?.cancel()

        // Afficher le chargement
        uiState = PrivyLoginUiState.Loading

        // Lancer une nouvelle requête
        currentJob = viewModelScope.launch {
            when (val result = privyManager.sendOTPCode(email)) {
                is PrivyResult.Success -> {
                    Log.d(TAG, "OTP sent successfully")
                    uiState = PrivyLoginUiState.OTPSent(email)
                }
                is PrivyResult.Error -> {
                    Log.e(TAG, "Error sending OTP: ${result.message}")
                    handleError(result.error, result.message)
                }
            }
        }
    }

    // Vérification du code OTP
    fun verifyOTP() {
        // Validation du code
        if (otpCode.isBlank()) {
            uiState = PrivyLoginUiState.Error.ValidationError("otp", "Le code de vérification ne peut pas être vide")
            return
        }

        // Annuler le job précédent si nécessaire
        currentJob?.cancel()

        // Afficher le chargement
        uiState = PrivyLoginUiState.Loading

        // Lancer une nouvelle requête
        currentJob = viewModelScope.launch {
            when (val result = privyManager.verifyOTPCode(email, otpCode)) {
                is PrivyResult.Success -> {
                    val user = result.data
                    if (user != null) {
                        Log.d(TAG, "OTP verification successful")
                        uiState = PrivyLoginUiState.Success
                    } else {
                        Log.e(TAG, "Authentication failed: User is null")
                        uiState = PrivyLoginUiState.Error.AuthError("L'authentification a échoué. Veuillez réessayer.")
                    }
                }
                is PrivyResult.Error -> {
                    Log.e(TAG, "Error verifying OTP: ${result.message}")
                    handleError(result.error, result.message)
                }
            }
        }
    }

    // Gestion des erreurs
    private fun handleError(error: AppError, message: String) {
        uiState = when (error) {
            is AppError.NetworkError -> {
                PrivyLoginUiState.Error.NetworkError(message)
            }
            is AppError.ValidationError -> {
                PrivyLoginUiState.Error.ValidationError(error.field, message)
            }
            is AppError.AuthError, is AppError.PrivyError -> {
                PrivyLoginUiState.Error.AuthError(message)
            }
            else -> {
                PrivyLoginUiState.Error.GenericError(message)
            }
        }
    }

    // Renvoyer le code
    fun resendOTP() {
        sendOTP()
    }

    // Retour à l'écran d'email
    fun goBackToEmailInput() {
        otpCode = ""
        uiState = PrivyLoginUiState.Ready
    }

    // Réessayer après une erreur
    fun retry() {
        when (uiState) {
            is PrivyLoginUiState.Error.NetworkError,
            is PrivyLoginUiState.Error.GenericError -> {
                if (otpCode.isNotBlank()) {
                    uiState = PrivyLoginUiState.OTPSent(email)
                } else {
                    uiState = PrivyLoginUiState.Ready
                }
            }
            is PrivyLoginUiState.Error.ValidationError -> {
                uiState = PrivyLoginUiState.Ready
            }
            is PrivyLoginUiState.Error.AuthError -> {
                otpCode = ""
                uiState = PrivyLoginUiState.OTPSent(email)
            }
            else -> {
                uiState = PrivyLoginUiState.Ready
            }
        }
    }

    // Nettoyer les ressources lors de la destruction du ViewModel
    override fun onCleared() {
        super.onCleared()
        currentJob?.cancel()
    }
}