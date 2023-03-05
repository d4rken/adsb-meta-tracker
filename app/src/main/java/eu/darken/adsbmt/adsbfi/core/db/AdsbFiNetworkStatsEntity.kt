package eu.darken.adsbmt.adsbfi.core.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "stats_adsbfi")
data class AdsbFiNetworkStatsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "beast_feeders") val beastFeeders: Int,
    @ColumnInfo(name = "mlat_feeders") val mlatFeeders: Int,
    @ColumnInfo(name = "totalAircraft", defaultValue = "0") val totalAircraft: Int,
    @ColumnInfo(name = "created_at") val createdAt: Instant,
)