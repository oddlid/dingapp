package net.oddware.dingapp

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import timber.log.Timber

class DingService : Service() {
    companion object {
        const val ACTION_START = "net.oddware.dingapp.ACTION_START"
        const val ACTION_STOP = "net.oddware.dingapp.ACTION_STOP"
        const val ACTION_CLOSE_APP = "net.oddware.dingapp.ACTION_CLOSE_APP"

        //...
        @JvmStatic
        var isRunning = false
            private set

        @JvmStatic
        private fun sendIntent(ctx: Context, action: String) {
            Timber.d("sendIntent(): Sending intent with action: $action")
            val intent = Intent(ctx, DingService::class.java).also {
                it.action = action
            }
            ctx.startService(intent)
        }

        @JvmStatic
        private fun sendBroadcast(ctx: Context, action: String): Boolean {
            return LocalBroadcastManager.getInstance(ctx).sendBroadcast(Intent(action))
        }

        @JvmStatic
        fun start(ctx: Context) {
            Timber.d("start(): Sending intent for starting service...")
            sendIntent(ctx, ACTION_START)
        }

        @JvmStatic
        fun stop(ctx: Context) {
            sendIntent(ctx, ACTION_STOP)
        }

        @JvmStatic
        fun closeApp(ctx: Context): Boolean {
            return sendBroadcast(ctx, ACTION_CLOSE_APP)
        }

        @JvmStatic
        fun getBroadcastReceiverForClose(parent: Activity): BroadcastReceiver {
            return object : BroadcastReceiver() {
                override fun onReceive(p0: Context?, p1: Intent?) {
                    if (p1?.action == ACTION_CLOSE_APP) {
                        parent.finish()
                    }
                }

            }
        }

        @JvmStatic
        fun getIntentFilterForClose(): IntentFilter {
            return IntentFilter().also {
                it.addAction(ACTION_CLOSE_APP)
            }
        }

        @JvmStatic
        fun getLaunchIntent(ctx: Context): Intent {
            return Intent(ctx, AlarmListActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                action = Intent.ACTION_MAIN
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
        }

        @JvmStatic
        fun createNotificationChannel(ctx: Context) {
            Thread {
                Timber.d("Creating notification channel in background thread, if needed...")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val chan = NotificationChannel(
                        ctx.packageName,
                        "Notification channel name", //ctx.getString(""),
                        NotificationManager.IMPORTANCE_DEFAULT
                    ).apply {
                        description =
                            "Notification channel description" //ctx.getString(R.string.notification_channel_desc)
                    }
                    ctx.getSystemService(NotificationManager::class.java)
                        ?.createNotificationChannel(chan)
                }
            }.start()
        }

    }

    inner class DingBinder : Binder() {
        fun getService(): DingService? = this@DingService
    }

    private val binder = DingBinder()

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("DingService is now in state RUNNING")
        isRunning = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START -> {
                    Timber.d("onStartCommand(): calling startForegroundService()...")
                    startForegroundService()
                }
                ACTION_STOP -> stopForegroundService()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }


    private fun startForegroundService() {
        Timber.d("Starting foreground Ding Service")
        startForeground(
            1,
            NotificationCompat.Builder(this, packageName).apply {
                priority = NotificationCompat.PRIORITY_DEFAULT
                setWhen(System.currentTimeMillis())
                setSmallIcon(R.drawable.ic_timer)
                setStyle(
                    NotificationCompat.BigTextStyle().also {
                        it.setBigContentTitle(getString(R.string.srv_notification_title))
                        it.bigText(getString(R.string.srv_notification_text))
                    }
                )
                setFullScreenIntent(
                    TaskStackBuilder.create(this@DingService).let {
                        it.addNextIntent(getLaunchIntent(this@DingService))
                        it.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
                    },
                    true
                )
                addAction(
                    NotificationCompat.Action(
                        android.R.drawable.ic_delete,
                        getString(R.string.srv_notification_stop_title),
                        PendingIntent.getService(
                            this@DingService,
                            0,
                            Intent(this@DingService, DingService::class.java).also {
                                it.action = ACTION_STOP
                            },
                            0
                        )
                    )
                )
            }.build()
        )
        isRunning = true
    }


    private fun stopForegroundService() {
        Timber.d("Stopping foreground Ding Service")
        stopSelf()
        isRunning = false
    }
}
