package eu.darken.adsbmt.adsbfi.core

import eu.darken.adsbmt.R
import eu.darken.adsbmt.networkstats.core.AdsbNetwork

object AdsbFi : AdsbNetwork {
    override val labelRes: Int = R.string.adsbfi_label
    override val iconRes: Int = R.drawable.ic_network_adsbfi
    override val website: String = "https://adsb.fi/"
}