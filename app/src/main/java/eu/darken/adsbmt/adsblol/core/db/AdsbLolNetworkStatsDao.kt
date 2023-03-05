package eu.darken.adsbmt.adsblol.core.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AdsbLolNetworkStatsDao {
    @Query("SELECT * FROM stats_adsblol")
    fun getAll(): List<AdsbLolNetworkStatsEntity>

    @Query("SELECT * FROM stats_adsblol ORDER BY id DESC LIMIT 1")
    fun getLatest(): Flow<AdsbLolNetworkStatsEntity?>

    @Query("SELECT * FROM stats_adsblol WHERE created_at < :olderThanEpochMillis ORDER BY created_at DESC LIMIT 1")
    suspend fun getOlderStats(olderThanEpochMillis: Long): AdsbLolNetworkStatsEntity?

    @Insert
    suspend fun insert(stats: AdsbLolNetworkStatsEntity): Long
}