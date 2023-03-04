package eu.darken.adsbmt.common.debug.recording.core

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import eu.darken.adsbmt.R
import eu.darken.adsbmt.common.BuildConfigWrap
import eu.darken.adsbmt.common.coroutine.DispatcherProvider
import eu.darken.adsbmt.common.debug.logging.log
import eu.darken.adsbmt.common.debug.logging.logTag
import eu.darken.adsbmt.common.notifications.PendingIntentCompat
import eu.darken.adsbmt.common.uix.Service2
import eu.darken.adsbmt.main.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class RecorderService : Service2() {
    private lateinit var builder: NotificationCompat.Builder

    @Inject lateinit var recorderModule: RecorderModule
    @Inject lateinit var notificationManager: NotificationManager
    @Inject lateinit var dispatcherProvider: DispatcherProvider
    private val recorderScope by lazy {
        CoroutineScope(SupervisorJob() + dispatcherProvider.IO)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        val compatChannel = NotificationChannelCompat
            .Builder(NOTIF_CHANID_DEBUG, NotificationManagerCompat.IMPORTANCE_MAX)
            .setName(getString(R.string.debug_notification_channel_label))
            .build()
        NotificationManagerCompat.from(this).createNotificationChannel(compatChannel)

        val openIntent = Intent(this, MainActivity::class.java)
        val openPi = PendingIntent.getActivity(
            this,
            0,
            openIntent,
            PendingIntentCompat.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, RecorderService::class.java)
        stopIntent.action = STOP_ACTION
        val stopPi = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntentCompat.FLAG_IMMUTABLE
        )

        builder = NotificationCompat.Builder(this, NOTIF_CHANID_DEBUG)
            .setChannelId(NOTIF_CHANID_DEBUG)
            .setContentIntent(openPi)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setSmallIcon(R.drawable.ic_baseline_bug_report_24)
            .setContentText("Idle")
            .setContentTitle(getString(R.string.app_name))
            .addAction(NotificationCompat.Action.Builder(0, getString(R.string.general_done_action), stopPi).build())

        startForeground(NOTIFICATION_ID, builder.build())

        recorderModule.state
            .onEach {
                if (it.isRecording) {
                    builder.setContentText("Recording debug log: ${it.currentLogPath?.path}")
                    notificationManager.notify(NOTIFICATION_ID, builder.build())
                } else {
                    stopForeground(true)
                    stopSelf()
                }
            }
            .launchIn(recorderScope)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log(TAG) { "onStartCommand(intent=$intent, flags=$flags, startId=$startId" }
        if (intent?.action == STOP_ACTION) {
            recorderScope.launch {
                recorderModule.stopRecorder()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        recorderScope.coroutineContext.cancel()
        super.onDestroy()
    }

    companion object {
        private val TAG =
            logTag("Debug", "Log", "Recorder", "Service")
        private val NOTIF_CHANID_DEBUG = "${BuildConfigWrap.APPLICATION_ID}.notification.channel.debug"
        private const val STOP_ACTION = "STOP_SERVICE"
        private const val NOTIFICATION_ID = 53
    }
}