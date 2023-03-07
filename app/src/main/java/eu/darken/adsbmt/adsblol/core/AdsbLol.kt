package eu.darken.adsbmt.adsblol.core

import eu.darken.adsbmt.R
import eu.darken.adsbmt.networkstats.core.AdsbNetwork

object AdsbLol : AdsbNetwork {
    override val labelRes: Int = R.string.adsblol_label
    override val iconRes: Int = R.drawable.ic_network_adsblol
    override val website: String = "https://adsb.lol/"
}