package net.oddware.dingapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AlarmListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_list)

        with(supportFragmentManager.beginTransaction()) {
            replace(R.id.flAlarmList, AlarmListFragment())
            commit()
        }

    }
}
