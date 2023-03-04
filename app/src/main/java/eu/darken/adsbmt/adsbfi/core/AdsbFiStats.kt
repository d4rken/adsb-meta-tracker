package eu.darken.adsbmt.adsbfi.core

import java.time.Instant

data class AdsbFiStats(
    val beastFeeders: Int,
    val mlatFeeders: Int,
    val updatedAt: Instant,
)
