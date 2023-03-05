package eu.darken.adsbmt.networkstats.core.db

import android.content.Context
import androidx.room.Room
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.darken.adsbmt.adsbfi.core.db.AdsbFiNetworkStatsDao
import eu.darken.adsbmt.adsbone.core.db.AdsbOneNetworkStatsDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkStatsDatabase @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val database by lazy {
        Room.databaseBuilder(
            context,
            NetworkStatsRoomDb::class.java, "network-stats"
        ).build()
    }

    val adsbFi: AdsbFiNetworkStatsDao
        get() = database.adsbFi()

    val adsbOne: AdsbOneNetworkStatsDao
        get() = database.adsbOne()
}