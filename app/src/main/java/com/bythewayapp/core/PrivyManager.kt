package com.bythewayapp.core

import android.content.Context
import android.util.Log
import com.bythewayapp.R
import dagger.hilt.android.qualifiers.ApplicationContext
import io.privy.auth.AuthState
import io.privy.auth.PrivyUser
import io.privy.logging.PrivyLogLevel
import io.privy.sdk.Privy
import io.privy.sdk.PrivyConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PrivyState {
    object Initialized : PrivyState()
    object Uninitialized : PrivyState()
    sealed class Error : PrivyState() {
        data class NetworkError(val exception: Exception? = null) : Error()
        data class ConfigError(val exception: Exception? = null) : Error()
        data class UnknownError(val exception: Exception? = null) : Error()
    }
}

sealed class PrivyAuthState {
    object Authenticated : PrivyAuthState()
    object NotReady : PrivyAuthState()
    object Unauthenticated : PrivyAuthState()
}

class PrivyManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private var privy: Privy? = null
    private var privyUser: PrivyUser? = null

    private val _state = MutableStateFlow<PrivyState>(PrivyState.Uninitialized)
    val state: StateFlow<PrivyState> = _state
    private val _authState = MutableStateFlow<PrivyAuthState>(PrivyAuthState.NotReady)
    val authState: StateFlow<PrivyAuthState> = _authState

    init {
        initializePrivy()
    }

    private fun initializePrivy() {
        try {
            privy = Privy.init(
                context = context.applicationContext,
                config = PrivyConfig(
                    appId = "cm5tvzo6w01m33pi8voq7n9d5",
                    appClientId = "client-WY5fQbv3tYqnZkGA4ggmxjphQwbALT8tRBg5KQrXnRWmW",
                    logLevel = PrivyLogLevel.VERBOSE
                )
            )
            _state.value = PrivyState.Initialized
            Log.i("PRIVY", "Privy successfully initialized")
        } catch (e: Exception) {
            Log.e("PRIVY", "Error initializing Privy: ${e.message}")
            _state.value = PrivyState.Error.ConfigError(e)
        }
    }

    fun reinitialize() {
        initializePrivy()
    }

    suspend fun sendOTPCode(email: String) : Result<Unit>{
        return try {
            privy?.awaitReady()
            val result = privy?.email?.sendCode(email = email)
            result?.fold(
                onSuccess = {
                    Log.d("PrivyManager", "Code OTP envoyé avec succès à $email")
                    Result.success(Unit)
                },
                onFailure = { error ->
                    Log.e("PrivyManager", "Échec d'envoi du code OTP: ${error.message}")
                    Result.failure(error)
                }
            ) ?: Result.failure(Exception("Privy n'est pas initialisé correctement"))
        } catch (e: Exception) {
            Log.e("PrivyManager", "Exception lors de l'envoi du code OTP: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun verifyOTPCode(email: String, otp: String) : Result<PrivyUser?> {
        return try {
            privy?.awaitReady()
            val result = privy?.email?.loginWithCode(
                email = email,
                code = otp
            )
            result?.fold(
                onSuccess = { user ->
                    Log.d("PrivyManager", "Vérification OTP réussie pour $email")
                    privyUser = user
                    Result.success(user)
                },
                onFailure = { error ->
                    Log.e("PrivyManager", "Échec de vérification OTP: ${error.message}")
                    Result.failure(error)
                }
            ) ?: Result.failure(Exception("Privy n'est pas initialisé correctement"))
        } catch (e: Exception) {
            Log.e("PrivyManager", "Exception lors de la vérification du code OTP: ${e.message}")
            Result.failure(e)
        }
    }

    // Collecte les mises à jour de l'état d'authentification
    fun observeAuthState() {
        CoroutineScope(Dispatchers.IO).launch {
            privy?.authState?.collect { authState ->
                when (authState) {
                    is AuthState.Authenticated -> {
                        Log.d("PrivyManager", "User is authenticated")
                        _authState.value = PrivyAuthState.Authenticated
                    }
                    AuthState.NotReady -> {
                        Log.d("PrivyManager", "Auth state is not ready")
                        _authState.value = PrivyAuthState.NotReady
                    }
                    AuthState.Unauthenticated -> {
                        Log.d("PrivyManager", "User is not authenticated")
                        _authState.value = PrivyAuthState.Unauthenticated
                    }
                }
            }
        }
    }
}
