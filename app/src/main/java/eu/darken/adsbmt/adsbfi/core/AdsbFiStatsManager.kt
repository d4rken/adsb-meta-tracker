package eu.darken.adsbmt.adsbfi.core

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.darken.adsbmt.adsbfi.ui.widget.AdsbfiStatsWidgetProvider
import eu.darken.adsbmt.common.BuildConfigWrap
import eu.darken.adsbmt.common.coroutine.AppScope
import eu.darken.adsbmt.common.debug.logging.Logging.Priority.ERROR
import eu.darken.adsbmt.common.debug.logging.Logging.Priority.VERBOSE
import eu.darken.adsbmt.common.debug.logging.asLog
import eu.darken.adsbmt.common.debug.logging.log
import eu.darken.adsbmt.common.debug.logging.logTag
import eu.darken.adsbmt.common.flow.throttleLatest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AdsbFiStatsManager @Inject constructor(
    @AppScope private val appScope: CoroutineScope,
    @ApplicationContext private val context: Context,
    private val workManager: WorkManager,
    private val repo: AdsbFiStatsRepo,
) {

    private val widgetManager by lazy { AppWidgetManager.getInstance(context) }

    private val currentWidgetIds: IntArray
        get() = widgetManager.getAppWidgetIds(ComponentName(context, PROVIDER_CLASS))

    private var isInit = false
    fun setup() {
        log(TAG) { "setup()" }
        require(!isInit)
        isInit = true

        setupPeriodicWorker()

        repo.latest
            .throttleLatest(2000)
            .take(1)
            .onEach { refreshWidgets() }
            .catch { log(TAG, ERROR) { "Automatic widget updates failed: ${it.asLog()}" } }
            .launchIn(appScope)

        appScope.launch {
            repo.refresh()
        }
    }

    private fun setupPeriodicWorker() {
        val workRequest = PeriodicWorkRequestBuilder<AdsbFiStatsWorker>(
            Duration.ofHours(6),
            Duration.ofMinutes(60)
        ).apply {
            setInputData(Data.Builder().build())
        }.build()

        log(TAG, VERBOSE) { "Worker request: $workRequest" }

        val operation = workManager.enqueueUniquePeriodicWork(
            "${BuildConfigWrap.APPLICATION_ID}.monitor.worker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest,
        )

        operation.result.get()
    }

    private suspend fun refreshWidgets() {
        log(TAG) { "refreshWidgets()" }

        repo.refresh()

        log(TAG, VERBOSE) { "Notifying these widget IDs: ${currentWidgetIds.toList()}" }

        val intent = Intent(context, PROVIDER_CLASS).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, currentWidgetIds)
        }
        context.sendBroadcast(intent)
    }

    companion object {
        val PROVIDER_CLASS = AdsbfiStatsWidgetProvider::class.java
        val TAG = logTag("Widget", "Manager")
    }
}