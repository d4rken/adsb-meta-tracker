package eu.darken.adsbmt.adsbfi.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import eu.darken.adsbmt.common.room.InstantConverter

@Database(entities = [AdsbFiNetworkStatsEntity::class], version = 1, exportSchema = false)
@TypeConverters(InstantConverter::class)
abstract class AdsbFiStatsDatabase : RoomDatabase() {
    abstract fun networkStats(): AdsbFiNetworkStatsDao
}