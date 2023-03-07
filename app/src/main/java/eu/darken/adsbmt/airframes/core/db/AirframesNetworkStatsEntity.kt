package eu.darken.adsbmt.airframes.core.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "stats_airframes")
data class AirframesNetworkStatsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "feeder_active") val feederActive: Int,
    @ColumnInfo(name = "aircraft_active") val aircraftActive: Int,
    @ColumnInfo(name = "created_at") val createdAt: Instant,
)