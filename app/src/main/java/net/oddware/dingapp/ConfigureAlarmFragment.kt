package net.oddware.dingapp

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.fragment_configure_alarm.view.*
import timber.log.Timber
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ConfigureAlarmFragment : Fragment() {
    companion object {
        //const val REQ_ALARM_ADD = 0xADD
        //const val REQ_ALARM_EDIT = 0x0DD
        //const val ARG_ALARM_ID = "AlarmID" // might just be the position in the list
        const val REQ_SOUND_ADD = 0x666
    }

    var cfgAction = ConfigureAlarmActivity.CFG_ACTION_ADD

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_configure_alarm, container, false)

        view.btnSave.setOnClickListener {
            when (cfgAction) {
                ConfigureAlarmActivity.CFG_ACTION_ADD -> Timber.d("Save new alarm")
                ConfigureAlarmActivity.CFG_ACTION_EDIT -> Timber.d("Save existing alarm")
                else -> Timber.d("Invalid code for save")
            }
            activity?.finish()
        }

        view.btnCancel.setOnClickListener {
            activity?.finish()
        }

        view.btnSound.setOnClickListener {
            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
                putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Choose sound")
                //putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, object : Uri())
            }
            startActivityForResult(intent, REQ_SOUND_ADD)
        }

        view.lblTime.setOnClickListener {
            //val tm = LocalTime.of(0, 0)
            val picker = TimePickerDialog.newInstance({ _, hour, minute, second ->
                val time = LocalTime.of(hour, minute, second)
                Timber.d("Time picked: ${time.toString()}")
                view.lblTime.text = time.format(DateTimeFormatter.ofPattern("hh:mm:ss"))
            }, 0, 0, true).also {
                it.enableSeconds(true)
            }
            fragmentManager?.run {
                picker.show(this, "Set interval")
            }
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Activity.RESULT_OK == resultCode && REQ_SOUND_ADD == requestCode) {
            data?.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)?.run {
                Timber.d("Sound picked: ${this.toString()}")
            }
        }
    }

}
