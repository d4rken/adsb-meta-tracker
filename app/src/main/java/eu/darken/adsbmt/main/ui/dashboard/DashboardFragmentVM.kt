package eu.darken.adsbmt.main.ui.dashboard

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.darken.adsbmt.adsbfi.core.AdsbFiStatsRepo
import eu.darken.adsbmt.adsbfi.ui.dash.AdsbFiStatsCardVH
import eu.darken.adsbmt.common.BuildConfigWrap
import eu.darken.adsbmt.common.coroutine.DispatcherProvider
import eu.darken.adsbmt.common.debug.logging.Logging.Priority.ERROR
import eu.darken.adsbmt.common.debug.logging.asLog
import eu.darken.adsbmt.common.debug.logging.log
import eu.darken.adsbmt.common.github.GithubReleaseCheck
import eu.darken.adsbmt.common.uix.ViewModel3
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import net.swiftzer.semver.SemVer
import javax.inject.Inject

@HiltViewModel
class DashboardFragmentVM @Inject constructor(
    handle: SavedStateHandle,
    dispatcherProvider: DispatcherProvider,
    private val statsRepo: AdsbFiStatsRepo,
    private val githubReleaseCheck: GithubReleaseCheck,
) : ViewModel3(dispatcherProvider = dispatcherProvider) {

    val newRelease = flow {
        val latestRelease = try {
            githubReleaseCheck.latestRelease("d4rken", "adsb-meta-tracker")
        } catch (e: Exception) {
            log(TAG, ERROR) { "Release check failed: ${e.asLog()}" }
            null
        }
        emit(latestRelease)
    }
        .filterNotNull()
        .filter {
            val current = try {
                SemVer.parse(BuildConfigWrap.VERSION_NAME.removePrefix("v"))
            } catch (e: IllegalArgumentException) {
                log(TAG, ERROR) { "Failed to parse current version: ${e.asLog()}" }
                return@filter false
            }
            log(TAG) { "Current version is $current" }

            val latest = try {
                SemVer.parse(it.tagName.removePrefix("v"))
            } catch (e: IllegalArgumentException) {
                log(TAG, ERROR) { "Failed to parse current version: ${e.asLog()}" }
                return@filter false
            }
            log(TAG) { "Latest version is $latest" }
            current < latest
        }
        .asLiveData2()

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