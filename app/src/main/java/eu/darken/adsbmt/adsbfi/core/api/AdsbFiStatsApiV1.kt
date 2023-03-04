package eu.darken.adsbmt.adsbfi.core.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET

interface AdsbFiStatsApiV1 {
    /**
     * {"beastFeeders":"781","mlatFeeders":"712"}
     */
    @JsonClass(generateAdapter = true)
    data class Stats(
        @Json(name = "beastFeeders") val beastFeeders: Int,
        @Json(name = "mlatFeeders") val mlatFeeders: Int,
    )

    @GET("stats")
    suspend fun getStats(): Stats
}