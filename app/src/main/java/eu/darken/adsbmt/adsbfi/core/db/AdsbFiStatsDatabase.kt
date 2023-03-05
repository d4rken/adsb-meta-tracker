package eu.darken.adsbmt.adsbfi.core.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import eu.darken.adsbmt.common.room.InstantConverter

@Database(
    entities = [AdsbFiNetworkStatsEntity::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2
        ),
    ],
)
@TypeConverters(InstantConverter::class)
abstract class AdsbFiStatsDatabase : RoomDatabase() {
    abstract fun networkStats(): AdsbFiNetworkStatsDao
}