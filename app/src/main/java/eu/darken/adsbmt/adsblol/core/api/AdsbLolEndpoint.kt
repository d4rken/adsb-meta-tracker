package eu.darken.adsbmt.adsblol.core.api

import com.squareup.moshi.Moshi
import dagger.Reusable
import eu.darken.adsbmt.common.coroutine.DispatcherProvider
import eu.darken.adsbmt.common.debug.logging.log
import eu.darken.adsbmt.common.debug.logging.logTag
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Inject

@Reusable
class AdsbLolEndpoint @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val baseHttpClient: OkHttpClient,
    private val baseMoshi: Moshi,
) {

    private val httpClient by lazy {
        baseHttpClient.newBuilder().apply {

        }.build()
    }

    private val api: AdsbLolStatsApiV1 by lazy {
        Retrofit.Builder().apply {
            baseUrl("https://api.adsb.lol/")
            client(httpClient)
            addConverterFactory(ScalarsConverterFactory.create())
            addConverterFactory(MoshiConverterFactory.create(baseMoshi).asLenient())
        }.build().create(AdsbLolStatsApiV1::class.java)
    }

    suspend fun getNetworkStats(): NetworkStats = withContext(dispatcherProvider.IO) {
        log(TAG) { "getNetworkStats()" }

        val metrics = api.getMe()

        NetworkStats(
            beastFeeders = metrics.global.beast,
            mlatFeeders = metrics.global.mlat,
            aircraftActive = metrics.global.planes,
        )

    }

    data class NetworkStats(
        val beastFeeders: Int,
        val mlatFeeders: Int,
        val aircraftActive: Int,
    )

    companion object {
        private val TAG = logTag("adsb.lol", "Endpoint")
    }
}

