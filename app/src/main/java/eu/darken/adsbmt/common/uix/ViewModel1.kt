package eu.darken.adsbmt.common.uix

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import eu.darken.adsbmt.common.debug.logging.log
import eu.darken.adsbmt.common.debug.logging.logTag

abstract class ViewModel1 : ViewModel() {
    val TAG: String = logTag("VM", javaClass.simpleName)

    init {
        log(TAG) { "Initialized" }
    }

    @CallSuper
    override fun onCleared() {
        log(TAG) { "onCleared()" }
        super.onCleared()
    }
}