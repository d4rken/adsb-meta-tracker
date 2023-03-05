package eu.darken.adsbmt.adsbfi.core

import java.time.Instant

data class AdsbFiStats(
    val beastFeeders: Int,
    val beastFeedersPrevious: Int,
    val mlatFeeders: Int,
    val mlatFeedersPrevious: Int,
    val totalAircraft: Int,
    val totalAircraftPrevious: Int,
    val updatedAt: Instant,
)
