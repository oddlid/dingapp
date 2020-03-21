package net.oddware.dingapp

import android.net.Uri
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import timber.log.Timber
import java.io.Serializable
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

data class Alarm(
    var id: Int = 0,
    var name: String? = null,
    var repetitions: Int = 0,
    var time: LocalTime? = null,
    var soundUriStr: String? = null,
    var stopTimerWhenDone: Boolean = false
) : Serializable, Comparable<Alarm> {

    // What data do we need to keep the list item UI in sync?
    // - Max repetitions (int)
    // - Current repetition (int)
    // - Interval duration (Duration)
    // - Current duration until next alarm (duration, or some time class)
    // - Overtime duration (Duration): Time since final alarm was reached, if alarm is set to keep timer running
    // - State: Disabled, enabled and counting down, enabled and counting up (overtime)

    data class AlarmData(
        var enabled: Boolean = false,
        var maxReps: Int = 0,
        var currentRep: Int = 0,
        var interval: Duration? = null,
        var timeUntilNextAlarm: Duration? = null,
        var timeOfFinalAlarm: LocalTime? = null,
        var timeSinceFinalAlarm: Duration? = null,
        var stopTimerWhenDone: Boolean = false
    )

    private var _interval = time?.toDuration()
    private val alarmData = AlarmData(
        maxReps = repetitions,
        interval = _interval,
        timeUntilNextAlarm = _interval,
        stopTimerWhenDone = stopTimerWhenDone
    )
    private val mldAlarmData: MutableLiveData<AlarmData> = MutableLiveData(alarmData)
    val ldAlarmData: LiveData<AlarmData> = mldAlarmData

    val soundUri: Uri?
        get() {
            if (null != soundUriStr) {
                return Uri.parse(soundUriStr)
            }
            return null
        }

    private var cdTimer: CountDownTimer? = null

    private inline fun MutableLiveData<AlarmData>.notify() {
        value = value
    }

    inline fun LocalTime.toDuration(): Duration {
        return Duration.ofSeconds((second + (minute * 60) + (hour * 3600)).toLong())
    }

    //private inline fun Duration.toDingString(): String {
    //    val hours = this.toHours()
    //    var d = this.minusHours(hours)
    //    val minutes = d.toMinutes()
    //    d = d.minusMinutes(minutes)
    //    val seconds = d.seconds
    //    return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    //}

    override fun compareTo(other: Alarm): Int {
        //return other.time?.compareTo(time) ?: other.id.compareTo(id)
        return id.compareTo(other.id)
    }

    // For usage after changing mirrored properties
    fun syncAlarmData() {
        val interval = time?.toDuration()
        alarmData.let {
            it.maxReps = repetitions
            it.interval = interval
            it.timeUntilNextAlarm = interval
            it.stopTimerWhenDone = stopTimerWhenDone
        }
        mldAlarmData.notify()
    }

    fun enable(callback: Runnable?) {
        Timber.d("Enabling alarm \"$name\"")
        alarmData.enabled = true
        startTimer(callback)
    }

    fun disable() {
        Timber.d("Disabling alarm \"$name\"")
        cdTimer?.cancel()
        cdTimer = null
        with(alarmData) {
            enabled = false
            currentRep = 0
            timeUntilNextAlarm = null
            timeOfFinalAlarm = null
            timeSinceFinalAlarm = null
        }
        mldAlarmData.notify()
    }

    private fun getNextAlarmTime(now: LocalDateTime): LocalDateTime? {
        Timber.d("Calculating next alarm time...")
        val timeInterval = time ?: return null
        var targetTime = now.plusHours(timeInterval.hour.toLong())
        targetTime = targetTime.plusMinutes(timeInterval.minute.toLong())
        return targetTime.plusSeconds(timeInterval.second.toLong())
    }

    private fun startTimer(callback: Runnable?) {
        if (null != cdTimer) {
            Timber.wtf("Timer already running, returning")
            return
        }
        val now = LocalDateTime.now()
        val later = getNextAlarmTime(now)
        if (null == later) {
            Timber.wtf("startTimer(): Got null for future time. Check that alarm.time is not null!")
            return
        }
        val interval = now.until(later, ChronoUnit.MILLIS)
        val tickTime = 1000L // 1 second
        alarmData.currentRep = 1
        var cbRef = callback // need a var to be able to null the reference later

        Timber.d("Creating CountDownTimer with interval: $interval, tickTime: $tickTime")
        cdTimer = object : CountDownTimer(interval, tickTime) {
            override fun onFinish() {
                Timber.d("onFinish(): Reached alarm time")
                cbRef?.run()
                alarmData.currentRep++

                if (alarmData.currentRep > repetitions) {
                    if (null != cbRef) {
                        Timber.d("Reached max repetitions. Disabling callback")
                        cbRef = null
                    }
                    if (!stopTimerWhenDone) {
                        if (null == alarmData.timeOfFinalAlarm) {
                            alarmData.timeOfFinalAlarm = LocalTime.now()
                        }
                        cdTimer?.start()
                    } else {
                        disable()
                    }
                } else {
                    cdTimer?.start()
                }
                mldAlarmData.notify()
            }

            override fun onTick(millisUntilFinished: Long) {
                alarmData.timeUntilNextAlarm = Duration.ofMillis(millisUntilFinished)
                val finalTime = alarmData.timeOfFinalAlarm
                if (null != finalTime) {
                    alarmData.timeSinceFinalAlarm = Duration.ofMillis(finalTime.until(LocalDateTime.now(), ChronoUnit.MILLIS))
                }
                mldAlarmData.notify()
            }
        }.start()
    }

}
