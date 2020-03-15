package net.oddware.dingapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

class AlarmListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_list)

        // This should maybe rather be in the startup of the service, when we get to that point
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        with(supportFragmentManager.beginTransaction()) {
            replace(R.id.flAlarmList, AlarmListFragment())
            commit()
        }
    }
}
