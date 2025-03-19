package com.bythewayapp.core

import android.content.Context
import android.util.Log
import com.bythewayapp.model.Event
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class EventsFileLoader(private val context: Context) {
    // Charger depuis les assets
    fun loadEventsFromAssets(): String {
        try {
            //val inputStream = context.assets.open("events-test.json")
            //val json = inputStream.bufferedReader().use { it.readText() }
            return  "json"
        } catch (e: Exception) {
            Log.e("EventsFileLoader", "Erreur de chargement depuis assets", e)
            return ""
        }
    }

    // Charger depuis les fichiers internes
    fun loadEventsFromInternalStorage(fileName: String): List<Event> {
        try {
            val file = File(context.filesDir, "downloaded_events/$fileName")
            if (!file.exists()) return emptyList()

            val json = file.readText()
            return parseEventsFromJson(json)
        } catch (e: Exception) {
            Log.e("EventsFileLoader", "Erreur de chargement depuis stockage interne", e)
            return emptyList()
        }
    }

    private fun parseEventsFromJson(json: String): List<Event> {
        // Utilisez Gson ou Moshi pour parser le JSON en objets Event
        // Exemple avec Gson:
        return Gson().fromJson(json, object : TypeToken<List<Event>>() {}.type)
    }
}
