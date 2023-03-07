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
import eu.darken.adsbmt.airframes.core.AirframesStats
import eu.darken.adsbmt.airframes.core.api.AirframesEndpoint
import eu.darken.adsbmt.airframes.core.db.AirframesNetworkStatsEntity
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
    private val airframesEndpoint: AirframesEndpoint,
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
                feederActive = latest?.feederActive ?: 0,
                feederActiveDiff = previous?.feederActive ?: 0,
                mlatActive = latest?.mlatActive ?: 0,
                mlatActiveDiff = previous?.mlatActive ?: 0,
                aircraftActive = latest?.aircraftActive ?: 0,
                aircraftActiveDiff = previous?.aircraftActive ?: 0,
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
                feederActive = latest?.feederActive ?: 0,
                feederActiveDiff = previous?.feederActive ?: 0,
                mlatActive = latest?.mlatActive ?: 0,
                mlatActiveDiff = previous?.mlatActive ?: 0,
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
                feederActive = latest?.feederActive ?: 0,
                feederActiveDiff = previous?.feederActive ?: 0,
                mlatActive = latest?.mlatActive ?: 0,
                mlatActiveDiff = previous?.mlatActive ?: 0,
                aircraftActive = latest?.aircraftActive ?: 0,
                aircraftActiveDiff = previous?.aircraftActive ?: 0,
                updatedAt = latest?.createdAt ?: Instant.EPOCH
            )
        },
        database.airframes.getLatest().map { latest ->
            var previous = database.airframes.getOlderStats(
                Instant.now().minus(Duration.ofHours(24)).toEpochMilli()
            )
            if (previous == null) {
                previous = database.airframes.getAll().firstOrNull()
            }
            AirframesStats(
                feederActive = latest?.feederActive ?: 0,
                feederActiveDiff = previous?.feederActive ?: 0,
                aircraftActive = latest?.aircraftActive ?: 0,
                aircraftActiveDiff = previous?.aircraftActive ?: 0,
                updatedAt = latest?.createdAt ?: Instant.EPOCH
            )
        },
    ) { fi, one, lol, air ->
        State(stats = listOf(fi, one, lol, air))
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
                    feederActive = stats.beastFeeders,
                    mlatActive = stats.mlatFeeders,
                    aircraftActive = stats.totalAircraft,
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
                    feederActive = stats.beastFeeders,
                    mlatActive = stats.mlatFeeders,
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
                    feederActive = stats.beastFeeders,
                    mlatActive = stats.mlatFeeders,
                    aircraftActive = stats.aircraftActive,
                    createdAt = Instant.now()
                )
                val rowId = database.adsbLol.insert(statsEntity)
                log(TAG) { "adsb.lol updated with ${statsEntity.copy(id = rowId)}" }
            }

            val airframes = async {
                val stats = try {
                    airframesEndpoint.getNetworkStats()
                } catch (e: Exception) {
                    log(TAG) { "Failed to get airframes.io network stats: $e" }
                    return@async
                }
                log(TAG) { "New network stats: $stats" }
                val statsEntity = AirframesNetworkStatsEntity(
                    feederActive = stats.stations.active,
                    aircraftActive = stats.flights.active,
                    createdAt = Instant.now()
                )
                val rowId = database.airframes.insert(statsEntity)
                log(TAG) { "airframes.io updated with ${statsEntity.copy(id = rowId)}" }
            }

            awaitAll(adsbFi, adsbOne, adsbLol, airframes)
        }

    }

    companion object {
        val TAG = logTag("Network", "Stats", "Repo")
    }
}