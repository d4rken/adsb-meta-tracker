package eu.darken.adsbmt.main.ui

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.darken.adsbmt.common.coroutine.DispatcherProvider
import eu.darken.adsbmt.common.debug.logging.Logging.Priority.VERBOSE
import eu.darken.adsbmt.common.debug.logging.log
import eu.darken.adsbmt.common.uix.ViewModel2
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class MainActivityVM @Inject constructor(
    dispatcherProvider: DispatcherProvider,
    handle: SavedStateHandle,
) : ViewModel2(dispatcherProvider = dispatcherProvider) {

    private val readyStateInternal = MutableStateFlow(true)
    val readyState = readyStateInternal.asLiveData2()

}