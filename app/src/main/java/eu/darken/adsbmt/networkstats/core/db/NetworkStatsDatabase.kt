package eu.darken.adsbmt.networkstats.core.db

import android.content.Context
import androidx.room.Room
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.darken.adsbmt.adsbfi.core.db.AdsbFiNetworkStatsDao
import eu.darken.adsbmt.adsblol.core.db.AdsbLolNetworkStatsDao
import eu.darken.adsbmt.adsbone.core.db.AdsbOneNetworkStatsDao
import eu.darken.adsbmt.airframes.core.db.AirframesNetworkStatsDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkStatsDatabase @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val database by lazy {
        Room.databaseBuilder(
            context,
            NetworkStatsRoomDb::class.java, "network-stats-2"
        ).build()
    }

    val adsbFi: AdsbFiNetworkStatsDao
        get() = database.adsbFi()

    val adsbOne: AdsbOneNetworkStatsDao
        get() = database.adsbOne()

    val adsbLol: AdsbLolNetworkStatsDao
        get() = database.adsbLol()

    val airframes: AirframesNetworkStatsDao
        get() = database.airframes()
}