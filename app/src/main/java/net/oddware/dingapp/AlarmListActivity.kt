package net.oddware.dingapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class AlarmListActivity : AppCompatActivity() {

    val bcReceiverForClose = DingService.getBroadcastReceiverForClose(this)
    private lateinit var alarmViewModel: AlarmViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_list)

        with(supportFragmentManager.beginTransaction()) {
            replace(R.id.flAlarmList, AlarmListFragment())
            commit()
        }

        // This seems to be a better place to start the service than in App
        DingService.start(applicationContext)

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(bcReceiverForClose, DingService.getIntentFilterForClose())

        alarmViewModel = ViewModelProvider(this).get(AlarmViewModel::class.java)
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
            R.id.action_delete_all -> alarmViewModel.clear() // TODO: confirmation dialog
        }
        return super.onOptionsItemSelected(item)
    }
}
