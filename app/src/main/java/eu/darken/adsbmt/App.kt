package eu.darken.adsbmt

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import eu.darken.adsbmt.common.BuildConfigWrap
import eu.darken.adsbmt.common.coroutine.AppScope
import eu.darken.adsbmt.common.debug.logging.*
import eu.darken.adsbmt.adsbfi.core.AdsbFiStatsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
open class App : Application(), Configuration.Provider {

    @Inject @AppScope lateinit var appScope: CoroutineScope
    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var adsbFiStatsManager: AdsbFiStatsManager

    override fun onCreate() {
        super.onCreate()
        if (BuildConfigWrap.DEBUG) {
            Logging.install(LogCatLogger())
            log(TAG) { "BuildConfig.DEBUG=true" }
        }

        adsbFiStatsManager.setup()

        log(TAG) { "onCreate() done! ${Exception().asLog()}" }
    }

    override fun getWorkManagerConfiguration(): Configuration = Configuration.Builder()
        .setMinimumLoggingLevel(android.util.Log.VERBOSE)
        .setWorkerFactory(workerFactory)
        .build()

    companion object {
        internal val TAG = logTag("App")
    }
}
