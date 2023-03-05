package eu.darken.adsbmt.adsblol.core.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET

interface AdsbLolStatsApiV1 {

    @GET("receivers")
    suspend fun getBeastClients(): List<List<Double>>

    @JsonClass(generateAdapter = true)
    data class ClientsMlat(
        @Json(name = "0A") val clientCount: Int,
    )

    @GET("mlat-server/totalcount.json")
    suspend fun getMlatClients(): ClientsMlat
}