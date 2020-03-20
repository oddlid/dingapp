package net.oddware.dingapp

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
import kotlinx.android.synthetic.main.fragment_configure_alarm.*
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
    var alarmID = ConfigureAlarmActivity.INVALID_ID
    private var alarmObj: Alarm? = null

    //var soundUri: Uri? = null
    private lateinit var alarmViewModel: AlarmViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_configure_alarm, container, false)

        // We handle adding in here, but setting values for edit is in onActivityCreated to be able to use
        // the viewModel
        if (ConfigureAlarmActivity.CFG_ACTION_ADD == cfgAction) {
            // create blank alarm if parent activity indicates to add new
            alarmObj = Alarm()
            // ID will get passed from the list activity, simply as the first free list index
            alarmObj?.id = alarmID
        }

        view.btnSave.setOnClickListener {
            alarmObj?.run {
                name = view.etAlarmName.text.toString().trim()
                repetitions = view.etRepeatTimes.text.toString().trim().toInt()
                time = LocalTime.parse(view.lblTime.text)
                stopTimerWhenDone = view.chkStopWhenDone.isChecked
                if (null == soundUriStr) {
                    soundUriStr = RingtoneManager.getActualDefaultRingtoneUri(
                        context,
                        RingtoneManager.TYPE_NOTIFICATION
                    ).toString()
                }
                syncAlarmData()
                //alarmViewModel.add(this)
            }
            activity?.setResult(RESULT_OK,
                Intent().apply {
                    putExtra(ConfigureAlarmActivity.ALARM_CFG_ID, alarmObj?.id)
                }
            )
            when (cfgAction) {
                ConfigureAlarmActivity.CFG_ACTION_ADD -> {
                    Timber.d("Saving new alarm")
                    alarmObj?.run { alarmViewModel.add(this) }
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
                putExtra(
                    RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                    RingtoneManager.getActualDefaultRingtoneUri(
                        context,
                        RingtoneManager.TYPE_NOTIFICATION
                    )
                )
                putExtra(
                    RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI,
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                )
            }
            startActivityForResult(intent, REQ_SOUND_ADD)
        }

        view.lblTime.setOnClickListener {
            val picker = TimePickerDialog.newInstance(
                { _, hour, minute, second ->
                    val time = LocalTime.of(hour, minute, second)
                    Timber.d("Time picked: ${timeFmt(time)}")
                    view.lblTime.text = timeFmt(time)
                },
                alarmObj?.time?.hour ?: 0,
                alarmObj?.time?.minute ?: 0,
                alarmObj?.time?.second ?: 0,
                true
            ).also {
                it.enableSeconds(true)
            }
            activity?.supportFragmentManager?.run {
                picker.show(this, "Set alarm interval")
            }
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (RESULT_OK == resultCode && REQ_SOUND_ADD == requestCode) {
            data?.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)?.run {
                val soundName = RingtoneManager.getRingtone(context, this).getTitle(context)
                Timber.d("Sound picked: $this ($soundName)")
                alarmObj?.soundUriStr = this.toString()
                btnSound.text = soundName
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        alarmViewModel = ViewModelProvider(this).get(AlarmViewModel::class.java)

        // Now that the viewmodel have been set up, we can update the UI to reflect the chosen Alarm
        // if we were opened to edit an existing alarm
        if (ConfigureAlarmActivity.CFG_ACTION_EDIT == cfgAction) {
            alarmObj = alarmViewModel.get(alarmID)
            updateUI(alarmObj)
        }
    }

    private fun updateUI(a: Alarm?) {
        etAlarmName.setText(a?.name)
        etRepeatTimes.setText(a?.repetitions?.toString())
        lblTime.text = a?.time?.toString() // TODO: Make sure this works, or fix
        chkStopWhenDone.isChecked = a?.stopTimerWhenDone ?: false
        if (null == a?.soundUriStr) {
            btnSound.text = "Pick Sound..."
        } else {
            btnSound.text = RingtoneManager.getRingtone(context, a.soundUri).getTitle(context)
        }
    }

    private inline fun timeFmt(time: LocalTime?): String {
        if (null == time) {
            return "00:00:00"
        }
        return String.format(Locale.US, "%02d:%02d:%02d", time.hour, time.minute, time.second)
    }

}
