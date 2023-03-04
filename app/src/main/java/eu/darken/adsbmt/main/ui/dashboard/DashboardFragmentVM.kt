package eu.darken.adsbmt.main.ui.dashboard

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.darken.adsbmt.adsbfi.core.AdsbFiStatsRepo
import eu.darken.adsbmt.common.coroutine.DispatcherProvider
import eu.darken.adsbmt.common.uix.ViewModel3
import eu.darken.adsbmt.adsbfi.ui.dash.AdsbFiStatsCardVH
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class DashboardFragmentVM @Inject constructor(
    handle: SavedStateHandle,
    dispatcherProvider: DispatcherProvider,
    private val statsRepo: AdsbFiStatsRepo,
) : ViewModel3(dispatcherProvider = dispatcherProvider) {

    val state = statsRepo.latest
        .map { adsbFiStats ->
            val items = mutableListOf<DashCardAdapter.Item>()
            AdsbFiStatsCardVH.Item(
                stats = adsbFiStats
            ).run { items.add(this) }
            State(
                items = items
            )
        }
        .asLiveData2()

    data class State(
        val items: List<DashCardAdapter.Item>
    )

    fun refresh() = launch {
        statsRepo.refresh()
    }

}