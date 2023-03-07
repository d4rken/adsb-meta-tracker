package eu.darken.adsbmt.adsbone.core.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "stats_adsbone")
data class AdsbOneNetworkStatsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "feeder_active") val feederActive: Int,
    @ColumnInfo(name = "mlat_active") val mlatActive: Int,
    @ColumnInfo(name = "created_at") val createdAt: Instant,
)