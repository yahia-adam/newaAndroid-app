package com.bythewayapp.ui.viewModels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bythewayapp.R
import com.bythewayapp.data.EventRepository
import com.bythewayapp.utils.ConnectionState
import com.bythewayapp.utils.ConnectionStateManager
import com.bythewayapp.utils.PrivyManager
import com.bythewayapp.utils.PrivyState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.privy.auth.PrivyUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PrivyLoginUiState {
    object Loading : PrivyLoginUiState()
    object Ready : PrivyLoginUiState()
    data class Error(val message: String) : PrivyLoginUiState()
}

@HiltViewModel
class PrivyLoginViewModel @Inject constructor(
    private val context: Context,
    private val privyManager: PrivyManager,
    private val connectivityManager: ConnectionStateManager
) : ViewModel() {

    val connectionState = connectivityManager.connectionState
    val privyState = privyManager.state

    private val _emailState = MutableStateFlow("")
    val emailState = _emailState.asStateFlow()

    private val _otpState = MutableStateFlow("")
    val otpState = _otpState.asStateFlow()

    // Utilisateur authentifié
    private val _user = MutableStateFlow<PrivyUser?>(null)
    val user = _user.asStateFlow()

    var privyLoginUiState: PrivyLoginUiState by mutableStateOf(PrivyLoginUiState.Loading)
        private set

    fun updateEmail(email: String) {
        _emailState.value = email
    }

    fun updateOtp(otp: String) {
        _otpState.value = otp
    }

    fun sendOTP() {
        viewModelScope.launch {
            privyLoginUiState = PrivyLoginUiState.Loading
            try {
                val email = _emailState.value
                /*if (email.isBlank()) {
                    _errorMessage.emit("Veuillez entrer une adresse email")
                    return@launch
                }*/

                val result = privyManager.sendOTPCode(email)
                result.fold(
                    onSuccess = {
                        privyLoginUiState = PrivyLoginUiState.Ready

                        //_successMessage.emit("Code envoyé avec succès à $email")
                    },
                    onFailure = { error ->
                        //_errorMessage.emit(error.message ?: "Échec d'envoi du code OTP")
                    }
                )
            } catch (e: Exception) {
                //_errorMessage.emit(e.message ?: "Une erreur inattendue s'est produite")
            } finally {
                //_isLoading.value = false
            }
        }
    }

    fun verifyOTP() {
        viewModelScope.launch {
            privyLoginUiState = PrivyLoginUiState.Loading
            try {
                val email = _emailState.value
                val otp = _otpState.value
    /*
                if (email.isBlank()) {
                    _errorMessage.emit("Veuillez entrer une adresse email")
                    return@launch
                }

                if (otp.isBlank()) {
                    _errorMessage.emit("Veuillez entrer le code OTP")
                    return@launch
                }
*/
                val result = privyManager.verifyOTPCode(email, otp)
                result.fold(
                    onSuccess = { user ->
                        _user.value = user
                        //_isAuthenticated.value = true
                        //_successMessage.emit("Authentification réussie")
                    },
                    onFailure = { error ->
                        //_errorMessage.emit(error.message ?: "Échec de vérification du code OTP")
                    }
                )
            } catch (e: Exception) {
                //_errorMessage.emit(e.message ?: "Une erreur inattendue s'est produite")
            } finally {
                //_isLoading.value = false
            }
        }
    }

    val uiState: StateFlow<PrivyLoginUiState> = combine(
        connectionState,
        privyState
    ) { connectionState, privyState ->
        when {
            connectionState is ConnectionState.Unavailable -> PrivyLoginUiState.Error(context.getString(R.string.erreur_de_connexion))
            privyState is PrivyState.Error -> PrivyLoginUiState.Error((privyState ?: "Une erreur est survenue").toString())
            privyState is PrivyState.Uninitialized -> PrivyLoginUiState.Loading
            privyState is PrivyState.Initialized -> PrivyLoginUiState.Ready
            else -> PrivyLoginUiState.Loading
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PrivyLoginUiState.Loading
    )

    fun retry() {
        privyManager.reinitialize()
    }

    fun sendEmail() {
        val privyInstance = privyManager.getInstance()
        if (privyInstance != null) {

        }
    }
}
