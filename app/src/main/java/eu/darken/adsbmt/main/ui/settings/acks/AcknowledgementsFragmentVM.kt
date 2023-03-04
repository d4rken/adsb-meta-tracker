package eu.darken.adsbmt.main.ui.settings.acks

import androidx.lifecycle.SavedStateHandle
import dagger.assisted.AssistedInject
import eu.darken.adsbmt.common.coroutine.DispatcherProvider
import eu.darken.adsbmt.common.debug.logging.logTag
import eu.darken.adsbmt.common.uix.ViewModel3

class AcknowledgementsFragmentVM @AssistedInject constructor(
    private val handle: SavedStateHandle,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel3(dispatcherProvider) {

    companion object {
        private val TAG = logTag("Settings", "Acknowledgements", "VM")
    }
}