package eu.darken.adsbmt.airframes.core.api

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
class AirframesEndpoint @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val baseHttpClient: OkHttpClient,
    private val baseMoshi: Moshi,
) {

    private val httpClient by lazy {
        baseHttpClient.newBuilder().apply {

        }.build()
    }

    private val api: AirframesStatsApiV1 by lazy {
        Retrofit.Builder().apply {
            baseUrl("https://api.airframes.io/v1/")
            client(httpClient)
            addConverterFactory(ScalarsConverterFactory.create())
            addConverterFactory(MoshiConverterFactory.create(baseMoshi).asLenient())
        }.build().create(AirframesStatsApiV1::class.java)
    }

    suspend fun getNetworkStats() = withContext(dispatcherProvider.IO) {
        log(TAG) { "getNetworkStats()" }
        api.getStats()
    }

    companion object {
        private val TAG = logTag("airframes", "Endpoint")
    }
}

