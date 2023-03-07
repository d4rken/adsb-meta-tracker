package eu.darken.adsbmt.networkstats.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import eu.darken.adsbmt.adsbfi.core.db.AdsbFiNetworkStatsDao
import eu.darken.adsbmt.adsbfi.core.db.AdsbFiNetworkStatsEntity
import eu.darken.adsbmt.adsblol.core.db.AdsbLolNetworkStatsDao
import eu.darken.adsbmt.adsblol.core.db.AdsbLolNetworkStatsEntity
import eu.darken.adsbmt.adsbone.core.db.AdsbOneNetworkStatsDao
import eu.darken.adsbmt.adsbone.core.db.AdsbOneNetworkStatsEntity
import eu.darken.adsbmt.airframes.core.db.AirframesNetworkStatsDao
import eu.darken.adsbmt.airframes.core.db.AirframesNetworkStatsEntity
import eu.darken.adsbmt.common.room.InstantConverter

@Database(
    entities = [
        AdsbFiNetworkStatsEntity::class,
        AdsbOneNetworkStatsEntity::class,
        AdsbLolNetworkStatsEntity::class,
        AirframesNetworkStatsEntity::class,
    ],
    version = 1,
    autoMigrations = [

    ],
    exportSchema = true,
)
@TypeConverters(InstantConverter::class)
abstract class NetworkStatsRoomDb : RoomDatabase() {
    abstract fun adsbFi(): AdsbFiNetworkStatsDao
    abstract fun adsbOne(): AdsbOneNetworkStatsDao
    abstract fun adsbLol(): AdsbLolNetworkStatsDao
    abstract fun airframes(): AirframesNetworkStatsDao
}