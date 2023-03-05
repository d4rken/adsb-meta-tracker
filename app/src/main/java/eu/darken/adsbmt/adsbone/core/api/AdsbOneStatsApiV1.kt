package eu.darken.adsbmt.adsbone.core.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET

interface AdsbOneStatsApiV1 {

    @JsonClass(generateAdapter = true)
    data class ClientsBeast(
        @Json(name = "clients") val clientCount: Int,
    )

    @GET("clients/beast")
    suspend fun getBeastClients(): ClientsBeast

    @JsonClass(generateAdapter = true)
    data class ClientsMlat(
        @Json(name = "client_count") val clientCount: Int,
    )

    @GET("clients/mlat")
    suspend fun getMlatClients(): ClientsMlat
}