package net.oddware.dingapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class ConfigureAlarmFragment : Fragment() {

    companion object {
        const val REQ_ALARM_ADD = 0xADD
        const val REQ_ALARM_EDIT = 0x0DD
        const val ARG_ALARM_ID = "AlarmID" // might just be the position in the list
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_configure_alarm, container, false)
    }

}
