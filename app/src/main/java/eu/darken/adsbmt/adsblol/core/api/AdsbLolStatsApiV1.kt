package eu.darken.adsbmt.adsblol.core.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET

interface AdsbLolStatsApiV1 {


    @JsonClass(generateAdapter = true)
    data class Data(
        @Json(name = "global") val global: Global,
    ) {
        @JsonClass(generateAdapter = true)
        data class Global(
            @Json(name = "beast") val beast: Int,
            @Json(name = "mlat") val mlat: Int,
            @Json(name = "planes") val planes: Int,
        )
    }

    /**
    {
    "client_ip": "...",
    "clients": {
    "beast": [],
    "mlat": []
    },
    "feeding": {
    "beast": false,
    "mlat": false
    },
    "global": {
    "beast": 334,
    "mlat": 239,
    "planes": 2318
    }
    }
     **/
    @GET("api/0/me")
    suspend fun getMe(): Data
}