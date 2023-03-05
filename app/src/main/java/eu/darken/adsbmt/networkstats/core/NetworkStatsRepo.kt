package eu.darken.adsbmt.networkstats.core

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.darken.adsbmt.adsbfi.core.AdsbFiStats
import eu.darken.adsbmt.adsbfi.core.api.AdsbFiEndpoint
import eu.darken.adsbmt.adsbfi.core.db.AdsbFiNetworkStatsEntity
import eu.darken.adsbmt.adsblol.core.AdsbLolStats
import eu.darken.adsbmt.adsblol.core.api.AdsbLolEndpoint
import eu.darken.adsbmt.adsblol.core.db.AdsbLolNetworkStatsEntity
import eu.darken.adsbmt.adsbone.core.AdsbOneStats
import eu.darken.adsbmt.adsbone.core.api.AdsbOneEndpoint
import eu.darken.adsbmt.adsbone.core.db.AdsbOneNetworkStatsEntity
import eu.darken.adsbmt.common.coroutine.AppScope
import eu.darken.adsbmt.common.debug.logging.log
import eu.darken.adsbmt.common.debug.logging.logTag
import eu.darken.adsbmt.common.flow.replayingShare
import eu.darken.adsbmt.networkstats.core.db.NetworkStatsDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkStatsRepo @Inject constructor(
    @AppScope private val appScope: CoroutineScope,
    @ApplicationContext private val context: Context,
    private val database: NetworkStatsDatabase,
    private val adsbFiEndpoint: AdsbFiEndpoint,
    private val adsbOneEndpoint: AdsbOneEndpoint,
    private val adsbLolEndpoint: AdsbLolEndpoint,
) {

    data class State(
        val stats: Collection<NetworkStats>
    )

    val latest: Flow<State> = combine(
        database.adsbFi.getLatest().map { latest ->
            var previous = database.adsbFi.getOlderStats(
                Instant.now().minus(Duration.ofHours(24)).toEpochMilli()
            )
            if (previous == null) {
                previous = database.adsbFi.getAll().firstOrNull()
            }
            AdsbFiStats(
                beastFeeders = latest?.beastFeeders ?: 0,
                beastFeedersPrevious = previous?.beastFeeders ?: 0,
                mlatFeeders = latest?.mlatFeeders ?: 0,
                mlatFeedersPrevious = previous?.mlatFeeders ?: 0,
                totalAircraft = latest?.totalAircraft ?: 0,
                totalAircraftPrevious = previous?.totalAircraft ?: 0,
                updatedAt = latest?.createdAt ?: Instant.EPOCH
            )
        },
        database.adsbOne.getLatest().map { latest ->
            var previous = database.adsbOne.getOlderStats(
                Instant.now().minus(Duration.ofHours(24)).toEpochMilli()
            )
            if (previous == null) {
                previous = database.adsbOne.getAll().firstOrNull()
            }
            AdsbOneStats(
                beastFeeders = latest?.beastFeeders ?: 0,
                beastFeedersPrevious = previous?.beastFeeders ?: 0,
                mlatFeeders = latest?.mlatFeeders ?: 0,
                mlatFeedersPrevious = previous?.mlatFeeders ?: 0,
                totalAircraft = latest?.totalAircraft ?: 0,
                totalAircraftPrevious = previous?.totalAircraft ?: 0,
                updatedAt = latest?.createdAt ?: Instant.EPOCH
            )
        },
        database.adsbLol.getLatest().map { latest ->
            var previous = database.adsbLol.getOlderStats(
                Instant.now().minus(Duration.ofHours(24)).toEpochMilli()
            )
            if (previous == null) {
                previous = database.adsbLol.getAll().firstOrNull()
            }
            AdsbLolStats(
                beastFeeders = latest?.beastFeeders ?: 0,
                beastFeedersPrevious = previous?.beastFeeders ?: 0,
                mlatFeeders = latest?.mlatFeeders ?: 0,
                mlatFeedersPrevious = previous?.mlatFeeders ?: 0,
                totalAircraft = latest?.totalAircraft ?: 0,
                totalAircraftPrevious = previous?.totalAircraft ?: 0,
                updatedAt = latest?.createdAt ?: Instant.EPOCH
            )
        },
    ) { fi, one, lol ->
        State(stats = listOf(fi, one, lol))
    }
        .onEach { log(TAG) { "New display-stats: $it" } }
        .replayingShare(appScope)

    suspend fun refresh() = withContext(NonCancellable) {
        log(TAG) { "refresh()" }
        coroutineScope {

            val adsbFi = async {
                val stats = try {
                    adsbFiEndpoint.getNetworkStats()
                } catch (e: Exception) {
                    log(TAG) { "Failed to get adsb.fi network stats: $e" }
                    return@async
                }
                log(TAG) { "New network stats: $stats" }
                val statsEntity = AdsbFiNetworkStatsEntity(
                    beastFeeders = stats.beastFeeders,
                    mlatFeeders = stats.mlatFeeders,
                    totalAircraft = stats.totalAircraft,
                    createdAt = Instant.now()
                )
                val rowId = database.adsbFi.insert(statsEntity)
                log(TAG) { "adsb.fi updated with ${statsEntity.copy(id = rowId)}" }
            }

            val adsbOne = async {
                val stats = try {
                    adsbOneEndpoint.getNetworkStats()
                } catch (e: Exception) {
                    log(TAG) { "Failed to get adsb.one network stats: $e" }
                    return@async
                }
                log(TAG) { "New network stats: $stats" }
                val statsEntity = AdsbOneNetworkStatsEntity(
                    beastFeeders = stats.beastFeeders,
                    mlatFeeders = stats.mlatFeeders,
                    totalAircraft = stats.totalAircraft,
                    createdAt = Instant.now()
                )
                val rowId = database.adsbOne.insert(statsEntity)
                log(TAG) { "adsb.one updated with ${statsEntity.copy(id = rowId)}" }
            }

            val adsbLol = async {
                val stats = try {
                    adsbLolEndpoint.getNetworkStats()
                } catch (e: Exception) {
                    log(TAG) { "Failed to get adsb.one network stats: $e" }
                    return@async
                }
                log(TAG) { "New network stats: $stats" }
                val statsEntity = AdsbLolNetworkStatsEntity(
                    beastFeeders = stats.beastFeeders,
                    mlatFeeders = stats.mlatFeeders,
                    totalAircraft = stats.totalAircraft,
                    createdAt = Instant.now()
                )
                val rowId = database.adsbLol.insert(statsEntity)
                log(TAG) { "adsb.lol updated with ${statsEntity.copy(id = rowId)}" }
            }

            awaitAll(adsbFi, adsbOne, adsbLol)
        }

    }

    companion object {
        val TAG = logTag("Network", "Stats", "Repo")
    }
}