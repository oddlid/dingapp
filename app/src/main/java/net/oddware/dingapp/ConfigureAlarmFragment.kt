package net.oddware.dingapp

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.fragment_configure_alarm.view.*
import timber.log.Timber
import java.time.LocalTime
import java.util.*

class ConfigureAlarmFragment : Fragment() {
    companion object {
        //const val REQ_ALARM_ADD = 0xADD
        //const val REQ_ALARM_EDIT = 0x0DD
        //const val ARG_ALARM_ID = "AlarmID" // might just be the position in the list
        const val REQ_SOUND_ADD = 0x666
    }

    var cfgAction = ConfigureAlarmActivity.CFG_ACTION_ADD
    var alarmObj: Alarm? = null

    //var soundUri: Uri? = null
    private lateinit var alarmViewModel: AlarmViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_configure_alarm, container, false)

        if (ConfigureAlarmActivity.CFG_ACTION_ADD == cfgAction) {
            alarmObj = Alarm() // create blank alarm if parent activity indicates to add new
        }

        view.btnSave.setOnClickListener {
            when (cfgAction) {
                ConfigureAlarmActivity.CFG_ACTION_ADD -> {
                    Timber.d("Saving new alarm")
                    //if (null == alarmObj) {
                    //    alarmObj = Alarm()
                    //}
                    alarmObj?.run {
                        name = view.etAlarmName.text.toString().trim()
                        repetitions = view.etRepeatTimes.text.toString().trim().toInt()
                        time = LocalTime.parse(view.lblTime.text)
                        stopTimerWhenDone = view.chkStopWhenDone.isChecked
                        alarmViewModel.add(this)
                    }
                    //with(Intent()) {
                    //    putExtra(ConfigureAlarmActivity.ALARM_OBJ, alarmObj)
                    //    activity?.let {
                    //        it.setResult(RESULT_OK, this)
                    //    }
                    //}
                    activity?.setResult(RESULT_OK, null)
                }
                ConfigureAlarmActivity.CFG_ACTION_EDIT -> {
                    Timber.d("Save existing alarm")
                }
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
                Timber.d("Time picked: ${timeFmt(time)}")
                view.lblTime.text =
                    timeFmt(time) //time.toString() //time.format(DateTimeFormatter.ofPattern("hh:mm:ss"))
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
                alarmObj?.soundUriStr = this.toString()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        alarmViewModel = ViewModelProvider(this).get(AlarmViewModel::class.java)
    }

    private inline fun timeFmt(time: LocalTime?): String {
        if (null == time) {
            return "00:00:00"
        }
        return String.format(Locale.US, "%02d:%02d:%02d", time.hour, time.minute, time.second)
    }

}
