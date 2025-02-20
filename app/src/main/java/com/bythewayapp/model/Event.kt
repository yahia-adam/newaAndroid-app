package com.bythewayapp.model

import com.google.gson.annotations.SerializedName


data class TicketmasterResponse(
    @SerializedName("_links") val links: TicketmasterLinks?,
    @SerializedName("_embedded") val embedded: TicketmasterEmbedded?,
    val page: TicketmasterPage?
)

data class TicketmasterLinks(
    val self: TicketmasterLink,
    val next: TicketmasterLink
)

data class TicketmasterEmbedded(
    val events: List<Event>
)

data class TicketmasterPage(
    val size: Int,
    val totalElements: Int,
    val totalPages: Int,
    val number: Int
)

data class Event(
    val location: TicketmasterLocation?,
    val id: String,
    val name: String,
    val description: String?,
    val url: String?,
    val images: List<TicketMasterImages>?,
    val dates: TicketmasterDates?,
    val priceRange: TicketmasterPriceRange?,
    val place: TicketmasterPlace?
)


data class TicketmasterPlace(
    val address: TicketmasterAddress,
    val city: TicketmasterCity,
    val postalCode: String,
    val location: TicketmasterLocation
)

data class TicketmasterAddress(
    val line1: String,
    val line2: String
)

data class TicketmasterCity(
    val name: String
)

data class TicketmasterPriceRange(
    val type: String,
    val currency: String,
    val min: Double,
    val max: Double
)

data class TicketmasterDates(
    val start: TicketmasterDate,
    val end: TicketmasterDate,
    val status: TicketmasterStatus
)

data class TicketmasterStatus(
    val code: StatusCode
)

enum class StatusCode {
    @SerializedName("onsale") ON_SALE,
    @SerializedName("offsale") OFF_SALE,
    @SerializedName("canceled") CANCELED,
    @SerializedName("postponed") POSTPONED,
    @SerializedName("rescheduled") RESCHEDULED
}

data class TicketmasterDate(
    val localDate: String,
    val dateTime: String
)

data class TicketMasterImages(
    val url: String,
    val fallback: Boolean,
    val attribution: String
)

data class TicketmasterLocation(
    val longitude: Float,
    val latitude: Float
)

data class TicketmasterLink(
    val link: String,
    val templated: Boolean
)
