package eu.darken.adsbmt.adsblol.core.api

import retrofit2.http.GET

interface AdsbLolStatsApiV1 {

    /**
     * adsb_api_beast_total_receivers 323
     * adsb_api_beast_total_clients 343
     * adsb_api_mlat_total 239
     */
    @GET("metrics")
    suspend fun getMetrics(): String
}