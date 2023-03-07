package eu.darken.adsbmt.adsblol.core

import eu.darken.adsbmt.networkstats.core.AdsbNetwork
import eu.darken.adsbmt.networkstats.core.NetworkStats
import java.time.Instant

data class AdsbLolStats(
    override val network: AdsbNetwork = AdsbLol,
    override val feederActive: Int,
    override val feederActiveDiff: Int,
    override val mlatActive: Int,
    override val mlatActiveDiff: Int,
    override val aircraftActive: Int,
    override val aircraftActiveDiff: Int,
    override val updatedAt: Instant,
) : NetworkStats, NetworkStats.Mlat, NetworkStats.Aircraft
