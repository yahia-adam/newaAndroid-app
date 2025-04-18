package com.bythewayapp.model

import com.google.gson.annotations.SerializedName
import com.mapbox.geojson.Point

class TicketmasterResponse(
    @SerializedName("_links") val links: TicketmasterLinks?,
    @SerializedName("_embedded") val embedded: TicketmasterEmbedded?,
    val page: TicketmasterPage?
)

class TicketmasterLinks(
    val self: TicketmasterLink,
    val next: TicketmasterLink
)

class TicketmasterEmbedded(
    val events: List<Event>
)

class TicketmasterPage(
    val size: Int,
    val totalElements: Int,
    val totalPages: Int,
    val number: Int
)

class Event(
    val id: String,
    val name: String,
    val description: String?,
    val url: String?,
    val images: List<TicketMasterImages>?,
    val dates: TicketmasterDates?,
    val priceRanges: List<TicketmasterPriceRange>?,
    val classifications: List<TicketmasterClassification>?,
    @SerializedName("_embedded") val embedded: TicketMasterEmbeddedVenue?,
) {

    fun isValidEvent():Boolean {
        if (images?.size == 0) return false
        if (url == null) return false
        if (classifications?.size == 0) return false
        if (priceRanges?.size == 0) return false
        if (dates?.status?.code != StatusCode.ON_SALE) return false
        return true
    }

    fun getTags(): List<String> {
        val tags = mutableSetOf<String>()

        classifications?.forEach { c ->
            c.segment?.name?.let { tags.add(it) }
            c.genre?.name?.let { tags.add(it) }
            c.subGenre?.name?.let { tags.add(it) }
        }

        return tags.toList()
    }

    // Dans ta classe Event
    fun copyWithNewCoordinates(newPoint: Point): Event {
        // Nous devons créer un nouvel objet TicketmasterLocation avec les nouvelles coordonnées
        val newLocation = embedded?.venues?.get(0)?.location?.let { oldLocation ->
            TicketmasterLocation(
                longitude = newPoint.longitude(),
                latitude = newPoint.latitude()
            )
        }

        // Nous devons créer un nouvel objet TicketMasterVenue avec la nouvelle location
        val newVenue = embedded?.venues?.get(0)?.let { oldVenue ->
            TicketMasterVenue(
                name = oldVenue.name,
                postalCode = oldVenue.postalCode,
                city = oldVenue.city,
                address = oldVenue.address,
                location = newLocation ?: oldVenue.location
            )
        }

        // Nous devons créer un nouvel objet TicketMasterEmbeddedVenue avec le nouveau venue
        val newEmbedded = embedded?.let { oldEmbedded ->
            TicketMasterEmbeddedVenue(
                venues = oldEmbedded.venues.mapIndexed { index, venue ->
                    if (index == 0 && newVenue != null) newVenue else venue
                }
            )
        }

        // Retourner un nouvel Event avec toutes les propriétés inchangées sauf l'embedded
        return Event(
            id = this.id,
            name = this.name,
            description = this.description,
            url = this.url,
            images = this.images,
            dates = this.dates,
            priceRanges = this.priceRanges,
            classifications = this.classifications,
            embedded = newEmbedded
        )
    }

    fun getCoordinates() : Point {
        return Point.fromLngLat(embedded?.venues?.get(0)?.location?.longitude ?: 0.0, embedded?.venues?.get(0)?.location?.latitude ?: 0.0)
    }

}

class TicketMasterEmbeddedVenue(
    val venues: List<TicketMasterVenue>,
)

class TicketMasterVenue(
    val name: String,
    val postalCode: String,
    val city: TicketmasterCity,
    val address: TicketmasterAddress,
    val location: TicketmasterLocation,
)

class TicketmasterClassification(
    val segment: TicketmasterSegment?,
    val genre: TicketmasterGenre?,
    val subGenre: TicketmasterSubGenre?,
)

class TicketmasterSegment(
    val genres: List<TicketmasterGenre>?,
    val id: String,
    val name: String,
    val locale: String
)

class TicketmasterGenre(
    val id: String,
    val name: String,
    val locale: String,
)

class TicketmasterSubGenre(
    val id: String,
    val name: String,
    val locale: String
)

class TicketmasterAddress(
    val line1: String?,
)

class TicketmasterCity(
    val name: String
)

class TicketmasterPriceRange(
    val type: String,
    val currency: String,
    val min: Double,
    val max: Double
)

class TicketmasterDates(
    val start: TicketmasterDate,
    val end: TicketmasterDate,
    val status: TicketmasterStatus
)

class TicketmasterStatus(
    val code: StatusCode
)

enum class StatusCode {
    @SerializedName("onsale") ON_SALE,
    @SerializedName("offsale") OFF_SALE,
    @SerializedName("canceled") CANCELED,
    @SerializedName("postponed") POSTPONED,
    @SerializedName("rescheduled") RESCHEDULED
}

class TicketmasterDate(
    val localDate: String,
    val dateTime: String,
    val localTime: String,
)

class TicketMasterImages(
    val url: String,
    val fallback: Boolean,
    val attribution: String
)

class TicketmasterLocation(
    val longitude: Double,
    val latitude: Double
)

class TicketmasterLink(
    val link: String,
    val templated: Boolean
)
