package eu.darken.adsbmt.adsbfi.core.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET

interface AdsbFiStatsApiV1 {
    /**
     * {
     *  "beastFeeders": "797",
     *  "mlatFeeders": "713",
     *  "totalAircraft": 3200
     * }
     */
    @JsonClass(generateAdapter = true)
    data class Stats(
        @Json(name = "beastFeeders") val beastFeeders: Int,
        @Json(name = "mlatFeeders") val mlatFeeders: Int,
        @Json(name = "totalAircraft") val totalAircraft: Int,
    )

    @GET("stats")
    suspend fun getStats(): Stats
}