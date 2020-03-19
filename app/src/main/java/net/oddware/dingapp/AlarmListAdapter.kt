package net.oddware.dingapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_alarm_list_item.view.*
import timber.log.Timber
import java.time.Duration
import java.time.LocalTime
import java.util.*

class AlarmListAdapter(val viewLifecycleOwner: LifecycleOwner) :
    ListAdapter<Alarm, AlarmListAdapter.ViewHolder>(AlarmDC()) {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var alarm: Alarm? = null

        fun setItem(a: Alarm) {
            alarm = a
            with(itemView) {
                txtAlarmName.text = a.name
                txtReps.text = getRepsText(a)
                txtTime.text = getTimeText(a)
                chkEnabled.isChecked = a.enabled
                if (a.stopTimerWhenDone) {
                    ivStopWhenDone.setImageDrawable(null)
                } else {
                    ivStopWhenDone.setImageResource(R.drawable.ic_infinity)
                }
            }
            //a.liveDataTimeUntilNextAlarm.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            //    itemView.txtTime.text = getTimeText(a) // this is stupid - should act on the duration passed as livedata
            //})
        }

        private fun getRepsText(a: Alarm): String {
            return String.format(Locale.US, "X: %d/%d", a.currentRepetition, a.repetitions)
        }

        private fun getTimeText(a: Alarm): String {
            //return when {
            //    a.overtime -> fmtLocalTime("+", a.time)
            //    a.enabled -> fmtDuration("-", a.timeUntilAlarm)
            //    else -> fmtLocalTime("", a.time)
            //}
            if (a.enabled && !a.overtime) {
                return fmtDuration("-", a.timeUntilAlarm)
            }
            if (a.overtime) {
                // not how it should be
                return fmtLocalTime("+", a.time)
            }
            return fmtLocalTime("", a.time)
        }

        private inline fun fmtLocalTime(prefix: String, time: LocalTime?): String {
            if (null == time) {
                return "00:00:00"
            }
            return String.format(
                Locale.US,
                "%s%02d:%02d:%02d",
                prefix,
                time.hour,
                time.minute,
                time.second
            )
        }

        private inline fun fmtDuration(prefix: String, time: Duration?): String {
            if (null == time) {
                return "00:00:00"
            }
            val hours = time.toHours()
            var d = time.minusHours(hours)
            val minutes = d.toMinutes()
            d = d.minusMinutes(minutes)
            val seconds = d.seconds
            return String.format(Locale.US, "%s%02d:%02d:%02d", prefix, hours, minutes, seconds)
        }
    }

    private class AlarmDC : DiffUtil.ItemCallback<Alarm>() {
        override fun areItemsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vh = ViewHolder(LayoutInflater.from(parent.context).inflate(viewType, parent, false))

        vh.itemView.chkEnabled.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                vh.alarm?.let {
                    it.enable(Runnable { Timber.d("Callback activated! Alarm sound: ${it.soundUriStr}") })
                }
            } else {
                vh.alarm?.disable()
            }
        }

        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        holder.setItem(item)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.fragment_alarm_list_item
    }

    fun swapData(data: List<Alarm>) {
        submitList(data.toMutableList())
    }
}
