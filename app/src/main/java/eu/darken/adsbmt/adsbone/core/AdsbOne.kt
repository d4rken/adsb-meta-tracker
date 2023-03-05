package eu.darken.adsbmt.adsbone.core

import eu.darken.adsbmt.R
import eu.darken.adsbmt.networkstats.core.AdsbNetwork

object AdsbOne : AdsbNetwork {
    override val labelRes: Int = R.string.adsbone_label
    override val iconRes: Int = R.drawable.ic_airplane_24
    override val website: String = "https://adsb.one/"
}