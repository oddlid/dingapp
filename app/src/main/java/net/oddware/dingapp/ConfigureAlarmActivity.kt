package net.oddware.dingapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ConfigureAlarmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configure_alarm)

        with(supportFragmentManager.beginTransaction()) {
            replace(R.id.flConfigureAlarm, ConfigureAlarmFragment())
            commit()
        }
    }
}
