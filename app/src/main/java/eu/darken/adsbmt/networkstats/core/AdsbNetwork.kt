package eu.darken.adsbmt.networkstats.core

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

interface AdsbNetwork {
    @get:StringRes val labelRes: Int
    @get:DrawableRes val iconRes: Int
    val website: String
}