package eu.darken.adsbmt.adsbfi.core

import eu.darken.adsbmt.networkstats.core.AdsbNetwork
import eu.darken.adsbmt.networkstats.core.NetworkStats
import java.time.Instant

data class AdsbFiStats(
    override val network: AdsbNetwork = AdsbFi,
    override val beastFeeders: Int,
    override val beastFeedersPrevious: Int,
    override val mlatFeeders: Int,
    override val mlatFeedersPrevious: Int,
    override val totalAircraft: Int,
    override val totalAircraftPrevious: Int,
    override val updatedAt: Instant,
) : NetworkStats
