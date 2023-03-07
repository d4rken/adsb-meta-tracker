package eu.darken.adsbmt.adsbone.core

import eu.darken.adsbmt.networkstats.core.AdsbNetwork
import eu.darken.adsbmt.networkstats.core.NetworkStats
import java.time.Instant

data class AdsbOneStats(
    override val network: AdsbNetwork = AdsbOne,
    override val feederActive: Int,
    override val feederActiveDiff: Int,
    override val mlatActive: Int,
    override val mlatActiveDiff: Int,
    override val updatedAt: Instant,
) : NetworkStats, NetworkStats.Mlat
