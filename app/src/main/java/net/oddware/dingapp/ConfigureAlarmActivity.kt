package net.oddware.dingapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class ConfigureAlarmActivity : AppCompatActivity() {
    companion object {
        const val ALARM_CFG_ACTION = "net.oddware.dingapp.ALARM_CFG_ACTION"
        const val ALARM_CFG_ID = "net.oddware.dingapp.ALARM_CFG_ID"
        const val ALARM_OBJ = "net.oddware.dingapp.ALARM_OBJ"
        const val CFG_ACTION_ADD = 0xADD
        const val CFG_ACTION_EDIT = 0xEDD
        const val INVALID_ID = -1

        @JvmStatic
        fun getLaunchIntent(ctx: Context, action: Int, index: Int = INVALID_ID): Intent {
            return with(Intent(ctx, ConfigureAlarmActivity::class.java)) {
                putExtra(ALARM_CFG_ACTION, action)
                putExtra(ALARM_CFG_ID, index)
            }
        }
    }

    val bcReceiverForClose = DingService.getBroadcastReceiverForClose(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configure_alarm)

        val cfgFrag = ConfigureAlarmFragment()
        with(supportFragmentManager.beginTransaction()) {
            replace(R.id.flConfigureAlarm, cfgFrag)
            commit()
        }

        // Determine if this activity was started with intent of creating a new alarm, or editing an existing one
        with(intent) {
            cfgFrag.cfgAction = getIntExtra(ALARM_CFG_ACTION, INVALID_ID)
            cfgFrag.alarmID = getIntExtra(ALARM_CFG_ID, INVALID_ID)
        }

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(bcReceiverForClose, DingService.getIntentFilterForClose())
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(bcReceiverForClose)
    }

}
