package eu.darken.adsbmt.airframes.core.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET

interface AirframesStatsApiV1 {

    /**
     * {"stations":{"active":324,"pending_reception":1,"inactive":276,"total":2052},"flights":{"active":3234},"balloons":{"active":297},"marine_voyages":{"active":7606}}
     */
    @JsonClass(generateAdapter = true)
    data class Stats(
        @Json(name = "stations") val stations: Stations,
        @Json(name = "flights") val flights: Flights,
    ) {
        @JsonClass(generateAdapter = true)
        data class Stations(
            @Json(name = "active") val active: Int
        )

        @JsonClass(generateAdapter = true)
        data class Flights(
            @Json(name = "active") val active: Int
        )
    }

    @GET("stats")
    suspend fun getStats(): Stats
}