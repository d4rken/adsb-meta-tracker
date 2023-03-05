package eu.darken.adsbmt.adsbfi.core.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AdsbFiNetworkStatsDao {
    @Query("SELECT * FROM stats_adsbfi")
    fun getAll(): List<AdsbFiNetworkStatsEntity>

    @Query("SELECT * FROM stats_adsbfi ORDER BY id DESC LIMIT 1")
    fun getLatest(): Flow<AdsbFiNetworkStatsEntity?>

    @Query("SELECT * FROM stats_adsbfi WHERE created_at < :olderThanEpochMillis ORDER BY created_at DESC LIMIT 1")
    suspend fun getOlderStats(olderThanEpochMillis: Long): AdsbFiNetworkStatsEntity?

    @Insert
    suspend fun insert(stats: AdsbFiNetworkStatsEntity): Long
}