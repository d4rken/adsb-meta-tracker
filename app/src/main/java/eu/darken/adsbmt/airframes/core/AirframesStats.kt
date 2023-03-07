package eu.darken.adsbmt.airframes.core

import eu.darken.adsbmt.networkstats.core.AdsbNetwork
import eu.darken.adsbmt.networkstats.core.NetworkStats
import java.time.Instant

data class AirframesStats(
    override val network: AdsbNetwork = Airframes,
    override val feederActive: Int,
    override val feederActiveDiff: Int,
    override val aircraftActive: Int,
    override val aircraftActiveDiff: Int,
    override val updatedAt: Instant,
) : NetworkStats, NetworkStats.Aircraft
