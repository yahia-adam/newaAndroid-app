package com.bythewayapp.data

import com.bythewayapp.model.TicketmasterResponse
import com.bythewayapp.model.TicketmasterSuggestionResponse
import com.bythewayapp.network.TicketmasterApi

class EventRepository {
    suspend fun getEvents(
        keyword: String?,
        id: List<String>?,
        classificationName: List<String>?,
        classificationId: List<String>?,
        startDateTime: String?,
        endDateTime: String?,
        size: String?,
        city: String?
    ): TicketmasterResponse {
        return TicketmasterApi.getEvents(
            keyword = keyword,
            id = id,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            size = size,
            classificationName = classificationName,
            classificationId = classificationId,
            city = city
        )
    }

    suspend fun getSuggestion(
        keyword: String,
    ): TicketmasterSuggestionResponse {
        return TicketmasterApi.getSuggestion(
            keyword = keyword,
        )
    }
}
