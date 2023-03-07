package eu.darken.adsbmt.adsbfi.core

import eu.darken.adsbmt.networkstats.core.AdsbNetwork
import eu.darken.adsbmt.networkstats.core.NetworkStats
import java.time.Instant

data class AdsbFiStats(
    override val network: AdsbNetwork = AdsbFi,
    override val feederActive: Int,
    override val feederActiveDiff: Int,
    override val mlatActive: Int,
    override val mlatActiveDiff: Int,
    override val aircraftActive: Int,
    override val aircraftActiveDiff: Int,
    override val updatedAt: Instant,
) : NetworkStats, NetworkStats.Aircraft, NetworkStats.Mlat
