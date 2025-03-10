package com.bythewayapp.model

import com.google.gson.annotations.SerializedName

data class TicketmasterSuggestionResponse(
    @SerializedName("_embedded") val embedded: TicketmasterEmbeddedSuggest?
)

data class TicketmasterEmbeddedSuggest(
    val events: List<TicketmasterEventSuggest>,
)

data class TicketmasterEventSuggest(
    val name: String,
    val id: String,
)