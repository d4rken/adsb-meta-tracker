package eu.darken.adsbmt.common.error

import eu.darken.adsbmt.common.livedata.SingleLiveEvent

interface ErrorEventSource {
    val errorEvents: SingleLiveEvent<Throwable>
}