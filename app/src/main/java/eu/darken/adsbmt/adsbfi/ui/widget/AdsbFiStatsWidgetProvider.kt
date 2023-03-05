package eu.darken.adsbmt.adsbfi.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import eu.darken.adsbmt.R
import eu.darken.adsbmt.adsbfi.core.AdsbFiStats
import eu.darken.adsbmt.common.coroutine.AppScope
import eu.darken.adsbmt.common.debug.logging.Logging.Priority.ERROR
import eu.darken.adsbmt.common.debug.logging.Logging.Priority.VERBOSE
import eu.darken.adsbmt.common.debug.logging.asLog
import eu.darken.adsbmt.common.debug.logging.log
import eu.darken.adsbmt.common.debug.logging.logTag
import eu.darken.adsbmt.common.getColorForAttr
import eu.darken.adsbmt.main.ui.MainActivity
import eu.darken.adsbmt.networkstats.core.NetworkStatsRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.time.Duration
import javax.inject.Inject

@AndroidEntryPoint
class AdsbFiStatsWidgetProvider : AppWidgetProvider() {

    @AppScope @Inject lateinit var appScope: CoroutineScope
    @Inject lateinit var statsRepo: NetworkStatsRepo

    private fun executeAsync(
        tag: String,
        timeout: Duration = Duration.ofSeconds(8),
        block: suspend () -> Unit
    ) {
        val start = System.currentTimeMillis()
        val asyncBarrier = goAsync()
        log(TAG, VERBOSE) { "executeAsync($tag) starting asyncBarrier=$asyncBarrier " }

        appScope.launch {
            try {
                withTimeout(timeout.toMillis()) { block() }
            } catch (e: Exception) {
                log(TAG, ERROR) { "executeAsync($tag) failed: ${e.asLog()}" }
            } finally {
                asyncBarrier.finish()
                val stop = System.currentTimeMillis()
                log(TAG, VERBOSE) { "executeAsync($tag) DONE (${stop - start}ms) " }
            }
        }

        log(TAG, VERBOSE) { "executeAsync($block) leaving" }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        log(TAG) { "onUpdate(appWidgetIds=${appWidgetIds.toList()})" }
        executeAsync("onUpdate") {
            appWidgetIds.forEach { appWidgetId ->
                updateWidget(context, appWidgetManager, appWidgetId, null)
            }
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        log(TAG) { "onAppWidgetOptionsChanged(appWidgetId=$appWidgetId, newOptions=$newOptions)" }
        executeAsync("onAppWidgetOptionsChanged") {
            updateWidget(context, appWidgetManager, appWidgetId, newOptions)
        }
    }

    private suspend fun updateWidget(
        context: Context,
        widgetManager: AppWidgetManager,
        widgetId: Int,
        options: Bundle?
    ) {
        log(TAG) { "updateWidget(widgetId=$widgetId, options=$options)" }

        val stats = statsRepo.latest
            .map { it.stats.filterIsInstance<AdsbFiStats>().singleOrNull() }
            .first() ?: return
        val layout = createLayout(context, stats)
        widgetManager.updateAppWidget(widgetId, layout)
    }

    private fun createLayout(
        context: Context,
        stats: AdsbFiStats
    ) = RemoteViews(context.packageName, R.layout.widget_adsbfi_stats_layout).apply {
        log(TAG, VERBOSE) { "createLayout(context=$context, stats=$stats)" }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        setOnClickPendingIntent(R.id.widget_root, pendingIntent)

        setTextViewText(R.id.total_value, stats.beastFeeders.toString())
        setImageViewTrendIcon(context, R.id.total_trend_icon, stats.beastFeeders - stats.beastFeedersPrevious)
        setTextViewText(R.id.mlat_value, stats.mlatFeeders.toString())
        setImageViewTrendIcon(context, R.id.mlat_trend_icon, stats.mlatFeeders - stats.mlatFeedersPrevious)
        setTextViewText(R.id.aircraft_value, stats.totalAircraft.toString())
        setImageViewTrendIcon(context, R.id.aircraft_trend_icon, stats.totalAircraft - stats.totalAircraftPrevious)
    }

    fun RemoteViews.setImageViewTrendIcon(context: Context, @IdRes id: Int, trend: Int) {
        setImageViewResource(
            id,
            when {
                trend > 0 -> R.drawable.ic_trending_up_24
                trend < 0 -> R.drawable.ic_trending_down_24
                else -> R.drawable.ic_trending_flat_24
            }
        )
        val tintColor = when {
            trend > 0 -> ContextCompat.getColor(context, R.color.md_theme_light_primary)
            trend < 0 -> ContextCompat.getColor(context, R.color.md_theme_light_error)
            else -> context.getColorForAttr(android.R.attr.textColor)
        }
        setInt(id, "setColorFilter", tintColor)
    }

    companion object {
        val TAG = logTag("adsb.fi", "Widget", "Provider")
    }
}