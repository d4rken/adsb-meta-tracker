package eu.darken.adsbmt.airframes.core.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AirframesNetworkStatsDao {
    @Query("SELECT * FROM stats_airframes")
    fun getAll(): List<AirframesNetworkStatsEntity>

    @Query("SELECT * FROM stats_airframes ORDER BY id DESC LIMIT 1")
    fun getLatest(): Flow<AirframesNetworkStatsEntity?>

    @Query("SELECT * FROM stats_airframes WHERE created_at < :olderThanEpochMillis ORDER BY created_at DESC LIMIT 1")
    suspend fun getOlderStats(olderThanEpochMillis: Long): AirframesNetworkStatsEntity?

    @Insert
    suspend fun insert(stats: AirframesNetworkStatsEntity): Long
}