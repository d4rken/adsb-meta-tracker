package eu.darken.adsbmt.networkstats.core

import java.time.Instant

interface NetworkStats {
    val network: AdsbNetwork
    val beastFeeders: Int
    val beastFeedersPrevious: Int
    val mlatFeeders: Int
    val mlatFeedersPrevious: Int
    val totalAircraft: Int
    val totalAircraftPrevious: Int
    val updatedAt: Instant
}