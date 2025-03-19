package com.bythewayapp.data

import com.bythewayapp.core.EventsFileLoader
import com.bythewayapp.model.Event
import com.bythewayapp.model.TicketmasterResponse
import com.bythewayapp.model.TicketmasterSuggestionResponse
import com.bythewayapp.network.TicketmasterApi
import javax.inject.Inject

class EventRepository @Inject constructor (
    private val fileLoader: EventsFileLoader
) {
    suspend fun getEvents(
        keyword: String?,
        id: List<String>?,
        classificationName: List<String>?,
        classificationId: List<String>?,
        startDateTime: String?,
        endDateTime: String?,
        size: String?,
        city: String?,
        geoPoint: String?
    ): TicketmasterResponse {
        return TicketmasterApi.getEvents(
            keyword = keyword,
            id = id,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            size = size,
            classificationName = classificationName,
            classificationId = classificationId,
            city = city,
            geoPoint = geoPoint
        )
    }

    // Récupérer les événements des fichiers
    fun getEventsFromFiles(): String {
        val eventsFromAssets = fileLoader.loadEventsFromAssets()
        //val eventsFromStorage = fileLoader.loadEventsFromInternalStorage("cached_events.json")
        return eventsFromAssets
    //return eventsFromAssets + eventsFromStorage
    }

/*
    // Fonction pour combiner toutes les sources
    suspend fun getAllEvents(): List<Event> {
        val apiEvents = getEventsFromApi()
        val fileEvents = getEventsFromFiles()

        // Combiner et déduplicater si nécessaire
        return (apiEvents + fileEvents).distinctBy { it.id }
    }

*/
    suspend fun getSuggestion(
        keyword: String,
    ): TicketmasterSuggestionResponse {
        return TicketmasterApi.getSuggestion(
            keyword = keyword,
        )
    }
}
