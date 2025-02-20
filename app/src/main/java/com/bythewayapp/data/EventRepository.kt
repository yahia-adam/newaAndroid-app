package com.bythewayapp.data

import com.bythewayapp.model.TicketmasterResponse
import com.bythewayapp.network.TicketmasterApi

class EventRepository {
    suspend fun getEvents(
        keyword: String?,
        classificationName: List<String>?,
        startDateTime: String?,
        endDateTime: String?,
        size: String?
    ): TicketmasterResponse {
        return TicketmasterApi.getEvents(
            keyword = keyword,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            size = size,
            classificationName = classificationName
        )
    }
}

