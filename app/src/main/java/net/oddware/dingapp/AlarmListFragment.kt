package net.oddware.dingapp

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_alarm_list.view.*
import timber.log.Timber

class AlarmListFragment : Fragment() {

    private lateinit var alarmViewModel: AlarmViewModel
    private lateinit var alarmListAdapter: AlarmListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_alarm_list, container, false)

        alarmListAdapter = AlarmListAdapter(viewLifecycleOwner)
        val lloMgr = LinearLayoutManager(view.context)
        with(view.rvAlarmList) {
            addItemDecoration(DividerItemDecoration(view.context, lloMgr.orientation))
            layoutManager = lloMgr
            setHasFixedSize(true)
            adapter = alarmListAdapter
        }

        view.fabAddAlarm.setOnClickListener {
            // Start activity for adding alarm
            //val intent = Intent(activity, ConfigureAlarmActivity::class.java)
            //startActivityForResult(intent, ConfigureAlarmActivity.CFG_ACTION_ADD)
            //startActivityForResult(
            //    ConfigureAlarmActivity.getLaunchIntent(this, ConfigureAlarmActivity.CFG_ACTION_ADD),
            //    ConfigureAlarmActivity.CFG_ACTION_ADD
            //)
            context?.let {
                startActivityForResult(
                    ConfigureAlarmActivity.getLaunchIntent(
                        it,
                        ConfigureAlarmActivity.CFG_ACTION_ADD,
                        alarmListAdapter.itemCount
                    ),
                    ConfigureAlarmActivity.CFG_ACTION_ADD
                )
            }
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Timber.d("onActivityResult(): RequestCode: $requestCode, ResultCode: $resultCode")
        when (requestCode) {
            ConfigureAlarmActivity.CFG_ACTION_ADD -> {
                if (RESULT_OK == resultCode) {
                    Timber.d("Result for adding new alarm:")
                    //if (null != data) {
                    //    val alarm = data.getSerializableExtra(ConfigureAlarmActivity.ALARM_OBJ) as? Alarm
                    //    if (null != alarm) {
                    //        Timber.d("New alarm: ${alarm.toString()}")
                    //    }
                    //}
                }
            }
            ConfigureAlarmActivity.CFG_ACTION_EDIT -> {
                if (RESULT_OK == resultCode) {
                    Timber.d("Result for editing existing alarm")
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        alarmViewModel = ViewModelProvider(this).get(AlarmViewModel::class.java)
        alarmViewModel.alarms.observe(viewLifecycleOwner, Observer { alarms ->
            Timber.d("Observer notified...")
            //alarms?.let {
            //    Timber.d("Observed change to alarm list")
            //}
            if (null != alarms) {
                Timber.d("Alarm list is non-null")
            } else {
                Timber.d("Alarm list is null")
            }
            alarmListAdapter.swapData(alarms)
        })
    }
}

