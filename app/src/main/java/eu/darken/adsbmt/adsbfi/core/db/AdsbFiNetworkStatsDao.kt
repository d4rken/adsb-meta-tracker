package eu.darken.adsbmt.adsbfi.core.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AdsbFiNetworkStatsDao {
    @Query("SELECT * FROM network_stats")
    fun getAll(): Flow<List<AdsbFiNetworkStatsEntity>>

    @Query("SELECT * FROM network_stats ORDER BY id DESC LIMIT 1")
    fun getLatest(): Flow<AdsbFiNetworkStatsEntity?>

    @Insert
    suspend fun insert( stats: AdsbFiNetworkStatsEntity): Long
}