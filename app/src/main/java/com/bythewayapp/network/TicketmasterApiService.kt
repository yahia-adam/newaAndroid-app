package com.bythewayapp.network

import com.bythewayapp.BuildConfig
import com.bythewayapp.model.TicketmasterResponse
import com.bythewayapp.model.TicketmasterSuggestionResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface TicketmasterApiService {
    @GET("discovery/v2/events")
    suspend fun searchEvents(
        @Query("apikey") apiKey: String,
        @Query("id") id: List<String>?,
        @Query("locale") locale: String?,
        @Query("city") city: String?,
        @Query("countryCode") countryCode: String?,
        @Query("includeTest") includeTest: String?,
        @Query("startDateTime") startDateTime: String?,
        @Query("endDateTime") endDateTime: String?,
        @Query("keyword") keyword: String?,
        @Query("classificationName") classificationName: List<String>?,
        @Query("classificationId") classificationId: List<String>?,
        @Query("includeSpellcheck") includeSpellcheck: String?,
        @Query("size") size: String?,
        @Query("geoPoint") geoPoint: String?,
        @Query("radius") radius: String?,
        @Query("unit") unit: String?
    ): TicketmasterResponse

    @GET("/discovery/v2/suggest")
    suspend fun searchSuggestion(
        @Query("apikey") apiKey: String,
        @Query("keyword") keyword: String,
        @Query("resource") resource: List<String>?,
        @Query("includeTest") includeTest: String?,
        @Query("countryCode") countryCode: String?,
        @Query("locale") locale: String?,
    ): TicketmasterSuggestionResponse
}

object TicketmasterApi {
    private const val API_KEY = BuildConfig.TICKETMASTER_API_KEY
    private const val LOCALE: String = "fr"
    private const val COUNTRY_CODE: String = "fr"
    private const val INCLUDE_TEST: String = "no"
    private const val INCLUDE_SPELL_CHECK: String = "yes"
    private const val DEFAULT_RADIUS: String = "100"
    private const val DEFAULT_UNIT: String = "miles"
    private val RESOURCES: List<String> = listOf("events")

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://app.ticketmaster.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(TicketmasterApiService::class.java)

    suspend fun getEvents(
        keyword: String?,
        id: List<String>?,
        startDateTime: String?,
        endDateTime: String?,
        size: String?,
        classificationName: List<String>?,
        classificationId: List<String>?,
        city: String?,
        geoPoint: String? = null
    ) = apiService.searchEvents(
        apiKey = API_KEY,
        id = id,
        startDateTime = startDateTime,
        endDateTime = endDateTime,
        keyword = keyword,
        classificationName= classificationName,
        classificationId = classificationId,
        size = size,
        locale = LOCALE,
        countryCode = COUNTRY_CODE,
        includeTest = INCLUDE_TEST,
        city = city,
        includeSpellcheck = INCLUDE_SPELL_CHECK,
        geoPoint = geoPoint,
        radius = DEFAULT_RADIUS,
        unit = DEFAULT_UNIT
    )

    suspend fun getSuggestion(
        keyword: String,
    ) = apiService.searchSuggestion(
        apiKey = API_KEY,
        keyword = keyword,
        locale = LOCALE,
        includeTest = INCLUDE_TEST,
        countryCode = COUNTRY_CODE,
        resource = RESOURCES
    )
}