package com.bythewayapp.core

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

/**
 * Utilitaire pour ouvrir différents écrans de paramètres Android
 */
object SettingsHelper {

    /**
     * Ouvre les paramètres de l'application
     * @param context Le contexte de l'application
     * @return true si l'action a réussi, false sinon
     */
    fun openAppSettings(context: Context): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Ouvre les paramètres de localisation
     * @param context Le contexte de l'application
     * @return true si l'action a réussi, false sinon
     */
    fun openLocationSettings(context: Context): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Ouvre les paramètres généraux
     * @param context Le contexte de l'application
     * @return true si l'action a réussi, false sinon
     */
    fun openSettings(context: Context): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Ouvre les paramètres de permissions de l'application
     * @param context Le contexte de l'application
     * @return true si l'action a réussi, false sinon
     */
    fun openAppPermissionsSettings(context: Context): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}