package eu.darken.adsbmt.airframes.core

import eu.darken.adsbmt.R
import eu.darken.adsbmt.networkstats.core.AdsbNetwork

object Airframes : AdsbNetwork {
    override val labelRes: Int = R.string.airframes_label
    override val iconRes: Int = R.drawable.ic_network_airframes
    override val website: String = "https://airframes.io/"
}