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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Singleton

sealed class PrivyState {
    data object Initialized : PrivyState()
    data object Uninitialized : PrivyState()
    sealed class Error : PrivyState() {
        data class NetworkError(val exception: Exception? = null) : Error()
        data class ConfigError(val exception: Exception? = null) : Error()
        data class UnknownError(val exception: Exception? = null) : Error()
    }
}

sealed class PrivyAuthState {
    data object Authenticated : PrivyAuthState()
    data object NotReady : PrivyAuthState()
    data object Unauthenticated : PrivyAuthState()
}

// Résultats d'opérations Privy
sealed class PrivyResult<out T> {
    data class Success<T>(val data: T) : PrivyResult<T>()
    data class Error(val error: AppError, val message: String) : PrivyResult<Nothing>()
}

@Singleton
class PrivyManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val errorHandler: ErrorHandler
) {

    private val TAG = "PrivyManager"
    private var privy: Privy? = null
    private var privyUser: PrivyUser? = null

    // Timeout pour les opérations Privy (en ms)
    private val PRIVY_OPERATION_TIMEOUT = 15000L

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
            Log.i(TAG, "Privy successfully initialized")

            // Lancer l'observation de l'état d'authentification après l'initialisation
            CoroutineScope(Dispatchers.IO).launch {
                observeAuthState()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Privy: ${e.message}")
            _state.value = PrivyState.Error.ConfigError(e)
        }
    }

    fun reinitialize() {
        initializePrivy()
    }

    suspend fun sendOTPCode(email: String): PrivyResult<Unit> = withContext(Dispatchers.IO) {
        try {
            // Vérifier que Privy est initialisé
            if (privy == null) {
                val message = context.getString(R.string.error_privy_not_initialized)
                return@withContext PrivyResult.Error(
                    AppError.UnknownError(Exception(message)),
                    message
                )
            }

            // Ajouter un timeout pour éviter que l'opération ne bloque indéfiniment
            withTimeout(PRIVY_OPERATION_TIMEOUT) {
                privy?.awaitReady()
                val result = privy?.email?.sendCode(email = email)
                result?.fold(
                    onSuccess = {
                        Log.d(TAG, "Code OTP envoyé avec succès à $email")
                        PrivyResult.Success(Unit)
                    },
                    onFailure = { error ->
                        Log.e(TAG, "Échec d'envoi du code OTP: ${error.message}")
                        val (appError, message) = errorHandler.handleException(error)
                        PrivyResult.Error(appError, message)
                    }
                ) ?: run {
                    val message = context.getString(R.string.error_privy_not_initialized)
                    PrivyResult.Error(
                        AppError.UnknownError(Exception(message)),
                        message
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception lors de l'envoi du code OTP: ${e.message}")
            val (appError, message) = errorHandler.handleException(e)
            PrivyResult.Error(appError, message)
        }
    }

    suspend fun verifyOTPCode(email: String, otp: String): PrivyResult<PrivyUser?> = withContext(Dispatchers.IO) {
        try {
            // Vérifier que Privy est initialisé
            if (privy == null) {
                val message = context.getString(R.string.error_privy_not_initialized)
                return@withContext PrivyResult.Error(
                    AppError.UnknownError(Exception(message)),
                    message
                )
            }

            // Ajouter un timeout pour éviter que l'opération ne bloque indéfiniment
            withTimeout(PRIVY_OPERATION_TIMEOUT) {
                privy?.awaitReady()
                val result = privy?.email?.loginWithCode(
                    email = email,
                    code = otp
                )
                result?.fold(
                    onSuccess = { user ->
                        Log.d(TAG, "Vérification OTP réussie pour $email")
                        privyUser = user
                        PrivyResult.Success(user)
                    },
                    onFailure = { error ->
                        Log.e(TAG, "Échec de vérification OTP: ${error.message}")
                        val (appError, message) = errorHandler.handleException(error)
                        PrivyResult.Error(appError, message)
                    }
                ) ?: run {
                    val message = context.getString(R.string.error_privy_not_initialized)
                    PrivyResult.Error(
                        AppError.UnknownError(Exception(message)),
                        message
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception lors de la vérification du code OTP: ${e.message}")
            val (appError, message) = errorHandler.handleException(e)
            PrivyResult.Error(appError, message)
        }
    }

    // Collecte les mises à jour de l'état d'authentification
    suspend fun observeAuthState() {
        try {
            privy?.authState?.collect { authState ->
                when (authState) {
                    is AuthState.Authenticated -> {
                        Log.d(TAG, "User is authenticated")
                        _authState.value = PrivyAuthState.Authenticated
                    }
                    AuthState.NotReady -> {
                        Log.d(TAG, "Auth state is not ready")
                        _authState.value = PrivyAuthState.NotReady
                    }
                    AuthState.Unauthenticated -> {
                        Log.d(TAG, "User is not authenticated")
                        _authState.value = PrivyAuthState.Unauthenticated
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error observing auth state: ${e.message}")
            // Si l'observation échoue, considérer l'utilisateur comme non authentifié
            _authState.value = PrivyAuthState.Unauthenticated
        }
    }

    // Vérification de la connectivité réseau
    fun isNetworkAvailable(): Boolean {
        // Tu peux utiliser ConnectivityManager pour vérifier la connectivité réseau
        // Implémentation simplifiée ici
        return true
    }
}