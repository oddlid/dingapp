package net.oddware.dingapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_shutdown -> DingService.stop(applicationContext)
        }
        return super.onOptionsItemSelected(item)
    }
}
