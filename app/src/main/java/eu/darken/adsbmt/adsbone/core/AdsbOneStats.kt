package eu.darken.adsbmt.adsbone.core

import eu.darken.adsbmt.networkstats.core.AdsbNetwork
import eu.darken.adsbmt.networkstats.core.NetworkStats
import java.time.Instant

data class AdsbOneStats(
    override val network: AdsbNetwork = AdsbOne,
    override val beastFeeders: Int,
    override val beastFeedersPrevious: Int,
    override val mlatFeeders: Int,
    override val mlatFeedersPrevious: Int,
    override val totalAircraft: Int,
    override val totalAircraftPrevious: Int,
    override val updatedAt: Instant,
) : NetworkStats
