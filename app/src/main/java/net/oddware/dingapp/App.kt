package net.oddware.dingapp

import android.app.Application
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // This should maybe rather be in the startup of the service, when we get to that point
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("Debug logging activated")
        }

        Timber.d("Creating notification  channel...")
        DingService.createNotificationChannel(applicationContext)
        Timber.d("Attempting to start Ding Service...")
        DingService.start(applicationContext)
    }
}
