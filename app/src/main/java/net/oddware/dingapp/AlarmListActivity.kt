package net.oddware.dingapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class AlarmListActivity : AppCompatActivity() {

    val bcReceiverForClose = DingService.getBroadcastReceiverForClose(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_list)

        with(supportFragmentManager.beginTransaction()) {
            replace(R.id.flAlarmList, AlarmListFragment())
            commit()
        }

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(bcReceiverForClose, DingService.getIntentFilterForClose())
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(bcReceiverForClose)
    }
}
