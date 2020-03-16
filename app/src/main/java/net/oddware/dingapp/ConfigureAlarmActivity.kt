package net.oddware.dingapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ConfigureAlarmActivity : AppCompatActivity() {
    companion object {
        const val ALARM_CFG_ACTION = "net.oddware.dingapp.ALARM_CFG_ACTION"
        const val ALARM_CFG_IDX = "net.oddware.dingapp.ALARM_CFG_IDX"
        const val CFG_ACTION_ADD = 0xADD
        const val CFG_ACTION_EDIT = 0xEDD
        const val INVALID_IDX = -1

        @JvmStatic
        fun getLaunchIntent(ctx: Context, action: Int, index: Int = INVALID_IDX): Intent {
            return with(Intent(ctx, ConfigureAlarmActivity::class.java)) {
                putExtra(ALARM_CFG_ACTION, action)
                putExtra(ALARM_CFG_IDX, index)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configure_alarm)

        val cfgFrag = ConfigureAlarmFragment()
        with(supportFragmentManager.beginTransaction()) {
            replace(R.id.flConfigureAlarm, cfgFrag)
            commit()
        }

        // Determine if this activity was started with intent of creating a new alarm, or editing an existing one
        val actionCode: Int
        val alarmIdx: Int
        with(intent) {
            actionCode = getIntExtra(ALARM_CFG_ACTION, INVALID_IDX)
            alarmIdx = getIntExtra(ALARM_CFG_IDX, INVALID_IDX)
        }

        // Now, signal the fragment if we should add or edit...
        cfgFrag.cfgAction = actionCode
    }
}
