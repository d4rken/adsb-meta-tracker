package eu.darken.adsbmt.adsbone.core.api

import com.squareup.moshi.Moshi
import dagger.Reusable
import eu.darken.adsbmt.common.coroutine.DispatcherProvider
import eu.darken.adsbmt.common.debug.logging.log
import eu.darken.adsbmt.common.debug.logging.logTag
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Inject

@Reusable
class AdsbOneEndpoint @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val baseHttpClient: OkHttpClient,
    private val baseMoshi: Moshi,
) {

    private val httpClient by lazy {
        baseHttpClient.newBuilder().apply {

        }.build()
    }

    private val api: AdsbOneStatsApiV1 by lazy {
        Retrofit.Builder().apply {
            baseUrl("https://api.adsb.one/")
            client(httpClient)
            addConverterFactory(ScalarsConverterFactory.create())
            addConverterFactory(MoshiConverterFactory.create(baseMoshi).asLenient())
        }.build().create(AdsbOneStatsApiV1::class.java)
    }

    suspend fun getNetworkStats(): NetworkStats = coroutineScope {
        log(TAG) { "getNetworkStats()" }
        val beastInfo = async { api.getBeastClients() }
        val mlatInfo = async { api.getMlatClients() }

        awaitAll(beastInfo, mlatInfo)

        NetworkStats(
            beastFeeders = beastInfo.getCompleted().clientCount,
            mlatFeeders = mlatInfo.getCompleted().clientCount,
        )
    }

    data class NetworkStats(
        val beastFeeders: Int,
        val mlatFeeders: Int,
        val totalAircraft: Int = 0,
    )

    companion object {
        private val TAG = logTag("adsb.fi", "Endpoint")
    }
}

