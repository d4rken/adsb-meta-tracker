package eu.darken.adsbmt.adsbfi.core

import android.content.Context
import androidx.room.Room
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.darken.adsbmt.adsbfi.core.api.AdsbFiEndpoint
import eu.darken.adsbmt.adsbfi.core.db.AdsbFiNetworkStatsEntity
import eu.darken.adsbmt.adsbfi.core.db.AdsbFiStatsDatabase
import eu.darken.adsbmt.common.coroutine.AppScope
import eu.darken.adsbmt.common.debug.logging.log
import eu.darken.adsbmt.common.debug.logging.logTag
import eu.darken.adsbmt.common.flow.replayingShare
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdsbFiStatsRepo @Inject constructor(
    @AppScope private val appScope: CoroutineScope,
    @ApplicationContext private val context: Context,
    private val endpoint: AdsbFiEndpoint,
) {

    private val database by lazy {
        Room.databaseBuilder(
            context,
            AdsbFiStatsDatabase::class.java, "adsbfi-stats"
        ).build()
    }
    val latest: Flow<AdsbFiStats> = database.networkStats().getLatest()
        .map { latest ->
            var previous = database.networkStats().getOlderStats(
                Instant.now().minus(Duration.ofHours(24)).toEpochMilli()
            )
            if (previous == null) {
                previous = database.networkStats().getAll().firstOrNull()
            }
            log(TAG) { "Stats from yesterday are: $previous" }
            AdsbFiStats(
                beastFeeders = latest?.beastFeeders ?: 0,
                beastFeedersPrevious = previous?.beastFeeders ?: 0,
                mlatFeeders = latest?.mlatFeeders ?: 0,
                mlatFeedersPrevious = previous?.mlatFeeders ?: 0,
                totalAircraft = latest?.totalAircraft ?: 0,
                totalAircraftPrevious = previous?.totalAircraft ?: 0,
                updatedAt = latest?.createdAt ?: Instant.EPOCH
            )
        }
        .onEach { log(TAG) { "New display-stats: $it" } }
        .replayingShare(appScope)

    suspend fun refresh() = withContext(NonCancellable) {
        log(TAG) { "refresh()" }

        val stats = endpoint.getNetworkStats()
        log(TAG) { "New network stats: $stats" }
        val statsEntity = AdsbFiNetworkStatsEntity(
            beastFeeders = stats.beastFeeders,
            mlatFeeders = stats.mlatFeeders,
            totalAircraft = stats.totalAircraft,
            createdAt = Instant.now()
        )
        val rowId = database.networkStats().insert(statsEntity)
        log(TAG) { "Database updated with ${statsEntity.copy(id = rowId)}" }
    }

    companion object {
        val TAG = logTag("AdsbFi", "Stats", "Repo")
    }
}