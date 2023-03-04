package eu.darken.adsbmt.adsbfi.core

import android.content.Context
import androidx.room.Room
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.darken.adsbmt.adsbfi.core.api.AdsbFiEndpoint
import eu.darken.adsbmt.adsbfi.core.db.AdsbFiNetworkStatsEntity
import eu.darken.adsbmt.adsbfi.core.db.AdsbFiStatsDatabase
import eu.darken.adsbmt.common.coroutine.AppScope
import eu.darken.adsbmt.common.coroutine.DispatcherProvider
import eu.darken.adsbmt.common.debug.logging.log
import eu.darken.adsbmt.common.debug.logging.logTag
import eu.darken.adsbmt.common.flow.replayingShare
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdsbFiStatsRepo @Inject constructor(
    @AppScope private val appScope: CoroutineScope,
    @ApplicationContext private val context: Context,
    private val dispatcherProvider: DispatcherProvider,
    private val endpoint: AdsbFiEndpoint,
) {

    private val database by lazy {
        Room.databaseBuilder(
            context,
            AdsbFiStatsDatabase::class.java, "adsbfi-stats"
        ).build()
    }
    val latest: Flow<AdsbFiStats> = database.networkStats().getLatest()
        .map {
            AdsbFiStats(
                beastFeeders = it?.beastFeeders ?: -1,
                mlatFeeders = it?.mlatFeeders ?: -1,
                updatedAt = it?.createdAt ?: Instant.EPOCH
            )
        }
        .replayingShare(appScope)

    suspend fun refresh() = withContext(NonCancellable) {
        log(TAG) { "refresh()" }

        val stats = endpoint.getNetworkStats()
        log(TAG) { "New network stats: $stats" }
        val statsEntity = AdsbFiNetworkStatsEntity(
            beastFeeders = stats.beastFeeders,
            mlatFeeders = stats.mlatFeeders,
            createdAt = Instant.now()
        )
        val rowId = database.networkStats().insert(statsEntity)
        log(TAG) { "Database updated with ${statsEntity.copy(id = rowId)}" }
    }

    companion object {
        val TAG = logTag("AdsbFi", "Stats", "Repo")
    }
}