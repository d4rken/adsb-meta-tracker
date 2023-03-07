package eu.darken.adsbmt.networkstats.core

import java.time.Instant

interface NetworkStats {
    val network: AdsbNetwork
    val feederActive: Int
    val feederActiveDiff: Int
    val updatedAt: Instant

    interface Mlat {
        val mlatActive: Int
        val mlatActiveDiff: Int
    }

    interface Aircraft {
        val aircraftActive: Int
        val aircraftActiveDiff: Int
    }
}