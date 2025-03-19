package com.bythewayapp.core

import android.content.Context
import com.bythewayapp.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Types d'erreurs possibles dans l'application
 */
sealed class AppError {
    data class NetworkError(val exception: Exception? = null) : AppError()
    data class AuthError(val exception: Exception? = null) : AppError()
    data class ValidationError(val field: String, val message: String) : AppError()
    data class ServerError(val code: Int? = null, val exception: Exception? = null) : AppError()
    data class PrivyError(val code: String? = null, val exception: Exception? = null) : AppError()
    data class UnknownError(val exception: Exception? = null) : AppError()
}

/**
 * Gestionnaire centralisé des erreurs de l'application
 */
@Singleton
class ErrorHandler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Convertit une exception en AppError et retourne un message d'erreur adapté
     */
    suspend fun handleException(exception: Throwable): Pair<AppError, String> = withContext(Dispatchers.Default) {
        val error = when (exception) {
            // Erreurs réseau
            is UnknownHostException, is ConnectException -> {
                AppError.NetworkError(exception as? Exception)
            }
            is SocketTimeoutException -> {
                AppError.NetworkError(exception)
            }
            is IOException -> {
                AppError.NetworkError(exception as? Exception)
            }

            // Erreurs HTTP (si tu utilises Retrofit)
            is HttpException -> {
                when (exception.code()) {
                    in 400..499 -> {
                        if (exception.code() == 401 || exception.code() == 403) {
                            AppError.AuthError(exception)
                        } else {
                            AppError.ServerError(exception.code(), exception)
                        }
                    }
                    in 500..599 -> {
                        AppError.ServerError(exception.code(), exception)
                    }
                    else -> {
                        AppError.UnknownError(exception)
                    }
                }
            }

            // Erreurs spécifiques à Privy
            // Tu peux ajouter des types d'exceptions spécifiques à Privy si nécessaire
            else -> {
                // Détection des erreurs Privy par le message d'erreur (à adapter selon les messages d'erreur de Privy)
                val message = exception.message?.lowercase() ?: ""
                when {
                    message.contains("invalid code") || message.contains("code incorrect") -> {
                        AppError.PrivyError("INVALID_CODE", exception as? Exception)
                    }
                    message.contains("expired") -> {
                        AppError.PrivyError("CODE_EXPIRED", exception as? Exception)
                    }
                    message.contains("authentication") || message.contains("auth") -> {
                        AppError.AuthError(exception as? Exception)
                    }
                    message.contains("email") && (message.contains("invalid") || message.contains("format")) -> {
                        AppError.ValidationError("email", "Format d'email invalide")
                    }
                    else -> {
                        AppError.UnknownError(exception as? Exception)
                    }
                }
            }
        }

        // Générer le message d'erreur approprié
        val errorMessage = getErrorMessage(error)
        Pair(error, errorMessage)
    }

    /**
     * Retourne un message d'erreur adapté en fonction du type d'erreur
     */
    private fun getErrorMessage(error: AppError): String {
        return when (error) {
            is AppError.NetworkError -> context.getString(R.string.error_network_connection)
            is AppError.AuthError -> context.getString(R.string.error_authentication)
            is AppError.ValidationError -> error.message
            is AppError.ServerError -> {
                when (error.code) {
                    404 -> context.getString(R.string.error_resource_not_found)
                    500 -> context.getString(R.string.error_server)
                    else -> context.getString(R.string.error_unknown)
                }
            }
            is AppError.PrivyError -> {
                when (error.code) {
                    "INVALID_CODE" -> context.getString(R.string.error_invalid_code)
                    "CODE_EXPIRED" -> context.getString(R.string.error_code_expired)
                    else -> context.getString(R.string.error_privy_unknown)
                }
            }
            is AppError.UnknownError -> context.getString(R.string.error_unknown)
        }
    }
}