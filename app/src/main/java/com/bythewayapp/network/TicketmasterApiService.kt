package com.bythewayapp.network

import com.bythewayapp.BuildConfig
import com.bythewayapp.model.TicketmasterResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface TicketmasterApiService {
    @GET("discovery/v2/events")
    suspend fun searchEvents(
        @Query("apikey") apiKey: String,
        @Query("local") local: String?,
        //@Query("countryCode") countryCode: String?,
        @Query("includeTest") includeTest: String?,
        @Query("startDateTime") startDateTime: String?,
        @Query("endDateTime") endDateTime: String?,
        @Query("keyword") keyword: String?,
        @Query("classificationName") classificationName: List<String>?,
        @Query("size") size: String?,
    ): TicketmasterResponse
}

object TicketmasterApi {
    private const val API_KEY = BuildConfig.TICKETMASTER_API_KEY
    private const val LOCAL: String = "fr-fr"
    //private const val COUNTRY_CODE: String = "fr"
    private const val INCLUDE_TEST: String = "no"

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://app.ticketmaster.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(TicketmasterApiService::class.java)

    suspend fun getEvents(
        keyword: String?,
        startDateTime: String?,
        endDateTime: String?,
        size: String?,
        classificationName: List<String>?
    ) =
        apiService.searchEvents(
            apiKey = API_KEY,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            keyword = keyword,
            classificationName= classificationName,
            size = size,
            local = LOCAL,
            //countryCode = COUNTRY_CODE,
            includeTest = INCLUDE_TEST
        )
}