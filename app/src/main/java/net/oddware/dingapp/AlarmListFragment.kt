package net.oddware.dingapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_alarm_list.view.*
import timber.log.Timber

class AlarmListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_alarm_list, container, false)

        view.fabAddAlarm.setOnClickListener {
            // Start activity for adding alarm
            val intent = Intent(activity, ConfigureAlarmActivity::class.java)
            startActivityForResult(intent, ConfigureAlarmFragment.REQ_ALARM_ADD)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.d("onActivityResult(): RequestCode: $requestCode, ResultCode: $resultCode")
    }
}

