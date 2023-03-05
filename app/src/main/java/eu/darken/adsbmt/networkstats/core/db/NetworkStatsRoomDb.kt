package eu.darken.adsbmt.networkstats.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import eu.darken.adsbmt.adsbfi.core.db.AdsbFiNetworkStatsDao
import eu.darken.adsbmt.adsbfi.core.db.AdsbFiNetworkStatsEntity
import eu.darken.adsbmt.adsbone.core.db.AdsbOneNetworkStatsDao
import eu.darken.adsbmt.adsbone.core.db.AdsbOneNetworkStatsEntity
import eu.darken.adsbmt.common.room.InstantConverter

@Database(
    entities = [
        AdsbFiNetworkStatsEntity::class,
        AdsbOneNetworkStatsEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(InstantConverter::class)
abstract class NetworkStatsRoomDb : RoomDatabase() {
    abstract fun adsbFi(): AdsbFiNetworkStatsDao
    abstract fun adsbOne(): AdsbOneNetworkStatsDao
}