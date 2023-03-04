package eu.darken.adsbmt.main.ui.main

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.darken.adsbmt.adsbfi.core.AdsbFiStatsRepo
import eu.darken.adsbmt.common.coroutine.DispatcherProvider
import eu.darken.adsbmt.common.uix.ViewModel3
import javax.inject.Inject

@HiltViewModel
class MainFragmentVM @Inject constructor(
    handle: SavedStateHandle,
    dispatcherProvider: DispatcherProvider,
    private val statsRepo: AdsbFiStatsRepo,
) : ViewModel3(dispatcherProvider = dispatcherProvider) {

    fun refresh() = launch {
        statsRepo.refresh()
    }

}