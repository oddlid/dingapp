package net.oddware.dingapp

import android.media.RingtoneManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_alarm_list_item.view.*
import java.time.Duration
import java.util.*

class AlarmListAdapter(val viewLifecycleOwner: LifecycleOwner) :
    ListAdapter<Alarm, AlarmListAdapter.ViewHolder>(AlarmDC()) {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var alarm: Alarm? = null

        fun setItem(a: Alarm) {
            alarm = a
            with(itemView) {
                txtAlarmName.text = a.name
                txtReps.text = getRepsText(a.ldAlarmData.value)
                txtTime.text = getTimeText(a.ldAlarmData.value)
                chkEnabled.isChecked = a.ldAlarmData.value?.enabled ?: false
                if (a.stopTimerWhenDone) {
                    ivStopWhenDone.setImageDrawable(null)
                } else {
                    ivStopWhenDone.setImageResource(R.drawable.ic_infinity)
                }
            }
            a.ldAlarmData.observe(viewLifecycleOwner, androidx.lifecycle.Observer { ad ->
                with(itemView) {
                    txtReps.text = getRepsText(ad)
                    txtTime.text = getTimeText(ad)
                    chkEnabled.isChecked = ad.enabled
                }
            })
        }

        private inline fun getRepsText(ad: Alarm.AlarmData?): String {
            if (null == ad) {
                return "X: 0/0"
            }
            return String.format(Locale.US, "X: %d/%d", ad.currentRep, ad.maxReps)
        }

        private inline fun getTimeText(ad: Alarm.AlarmData?): String {
            val data = ad ?: return "00:00:00"
            if (null != data.timeSinceFinalAlarm) {
                return fmtDuration("+", data.timeSinceFinalAlarm)
            } else if (data.enabled && null != data.timeUntilNextAlarm) {
                return fmtDuration("-", data.timeUntilNextAlarm)
            }
            return fmtDuration("", data.interval)
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
                vh.alarm?.let { a ->
                    a.enable(Runnable {
                        RingtoneManager.getRingtone(vh.itemView.context, a.soundUri).play()
                    })
                }
            } else {
                vh.alarm?.disable()
            }
        }

        vh.itemView.setOnClickListener {
            it.context.let { ctx ->
                ctx.startActivity(
                    ConfigureAlarmActivity.getLaunchIntent(
                        ctx,
                        ConfigureAlarmActivity.CFG_ACTION_EDIT,
                        vh.adapterPosition
                    )
                )
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
