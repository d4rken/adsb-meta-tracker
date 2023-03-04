package eu.darken.adsbmt.common.notifications

import android.app.PendingIntent
import eu.darken.adsbmt.common.hasApiLevel

object PendingIntentCompat {
    val FLAG_IMMUTABLE: Int = if (hasApiLevel(31)) {
        PendingIntent.FLAG_IMMUTABLE
    } else {
        0
    }
}