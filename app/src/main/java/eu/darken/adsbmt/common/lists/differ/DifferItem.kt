package eu.darken.adsbmt.common.lists.differ

import eu.darken.adsbmt.common.lists.ListItem

interface DifferItem : ListItem {
    val stableId: Long

    val payloadProvider: ((DifferItem, DifferItem) -> DifferItem?)?
        get() = { old, new ->
            if (new::class.isInstance(old)) new else null
        }
}