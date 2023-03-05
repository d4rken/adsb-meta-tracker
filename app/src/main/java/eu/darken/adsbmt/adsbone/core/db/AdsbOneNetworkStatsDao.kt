package eu.darken.adsbmt.adsbone.core.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AdsbOneNetworkStatsDao {
    @Query("SELECT * FROM stats_adsbone")
    fun getAll(): List<AdsbOneNetworkStatsEntity>

    @Query("SELECT * FROM stats_adsbone ORDER BY id DESC LIMIT 1")
    fun getLatest(): Flow<AdsbOneNetworkStatsEntity?>

    @Query("SELECT * FROM stats_adsbone WHERE created_at < :olderThanEpochMillis ORDER BY created_at DESC LIMIT 1")
    suspend fun getOlderStats(olderThanEpochMillis: Long): AdsbOneNetworkStatsEntity?

    @Insert
    suspend fun insert(stats: AdsbOneNetworkStatsEntity): Long
}