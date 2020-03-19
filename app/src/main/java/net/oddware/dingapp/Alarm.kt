package net.oddware.dingapp

import android.net.Uri
import android.os.CountDownTimer
import timber.log.Timber
import java.io.Serializable
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.*

data class Alarm(
    var id: Int = 0,
    var name: String? = null,
    var repetitions: Int = 0,
    var time: LocalTime? = null,
    var soundUriStr: String? = null,
    var stopTimerWhenDone: Boolean = false
) : Serializable, Comparable<Alarm> {

    // Intention here is to use these for exposing via livedata
    //data class RepetitionPair(var current: Int = 0, var total: Int = 0)
    //data class TimePair()

    var enabled = false
        private set
    var currentRepetition = 0
    val overtime: Boolean
        get() {
            return (enabled && currentRepetition > repetitions)
        }

    val soundUri: Uri?
        get() {
            if (null != soundUriStr) {
                return Uri.parse(soundUriStr)
            }
            return null
        }

    var timeUntilAlarm: Duration? = null
        private set
    //var doneAt: LocalDateTime? = null

    //private val mldTimeLeft: MutableLiveData<Duration> = MutableLiveData()
    //val liveDataTimeUntilNextAlarm: LiveData<Duration> = mldTimeLeft // public reference is RO
    //private val mldCurrentRepetition: MutableLiveData<Int> = MutableLiveData()
    //val liveDataCurrentRepetition = mldCurrentRepetition

    private var cdTimer: CountDownTimer? = null

    private inline fun Duration.toDingString(): String {
        val hours = this.toHours()
        var d = this.minusHours(hours)
        val minutes = d.toMinutes()
        d = d.minusMinutes(minutes)
        val seconds = d.seconds
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    }

    override fun compareTo(other: Alarm): Int {
        return other.time?.compareTo(time) ?: other.id.compareTo(id)
    }

    fun enable(callback: Runnable?) {
        Timber.d("Enabling alarm \"$name\"")
        enabled = true
        startTimer(callback)
    }

    fun disable() {
        Timber.d("Disabling alarm \"$name\"")
        enabled = false
        cdTimer?.cancel()
        cdTimer = null
        currentRepetition = 0
        timeUntilAlarm = null
        //mldTimeLeft.value = null
    }

    fun getNextAlarmTime(now: LocalDateTime): LocalDateTime? {
        Timber.d("Calculating next alarm time...")
        val timeInterval = time ?: return null
        var targetTime = now.plusHours(timeInterval.hour.toLong())
        targetTime = targetTime.plusMinutes(timeInterval.minute.toLong())
        return targetTime.plusSeconds(timeInterval.second.toLong())
    }

    fun startTimer(callback: Runnable?) {
        if (null != cdTimer) {
            Timber.wtf("Timer already running, returning")
            return
        }
        val now = LocalDateTime.now()
        val later = getNextAlarmTime(now)
        if (null == later) {
            Timber.wtf("startTimer(): Got null or future time. Check that alarm.time is not null!")
            return
        }
        val interval = now.until(later, ChronoUnit.MILLIS)
        val tickTime = 1000L // 1 second
        currentRepetition = 1
        var cbRef = callback // need a var to be able to null later

        Timber.d("Creating CountDownTimer with interval: $interval, tickTime: $tickTime")
        cdTimer = object : CountDownTimer(interval, tickTime) {
            override fun onFinish() {
                Timber.d("onFinish(): Reached alarm time")
                cbRef?.run()
                currentRepetition++
                //mldCurrentRepetition.value = currentRepetition

                if (currentRepetition > repetitions) {
                    if (null != cbRef) {
                        Timber.d("Reached max repetitions. Disabling callback")
                        cbRef = null
                    }
                    if (!stopTimerWhenDone) {
                        cdTimer?.start()
                    } else {
                        //cdTimer = null
                        disable()
                    }
                } else {
                    // Just loop forever until user presses disable
                    cdTimer?.start()
                }
            }

            override fun onTick(millisUntilFinished: Long) {
                timeUntilAlarm = Duration.ofMillis(millisUntilFinished)
                //mldTimeLeft.postValue(timeUntilAlarm)
                //mldTimeLeft.value = timeUntilAlarm
                Timber.d("onTick(): Time until alarm: ${timeUntilAlarm?.toDingString()}")
            }
        }.start()
    }

}

// Just keeping this down here for reference
// This was not the best solution. See below for the version used in ConfigureAlarmFragment right now
//fun getTimeString(): String =
//    time?.format(DateTimeFormatter.ofPattern("hh:mm:ss")) ?: "00:00:00"

//private inline fun timeFmt(time: LocalTime?): String {
//    // Unlike when using DateTimeFormatter + time.format, this way will return 00 when that is
//    // what's chosen, and not convert it to 12. Also, seconds are included not matter if 0 or not.
//    if (null == time) {
//        return "00:00:00"
//    }
//    return String.format(Locale.US, "%02d:%02d:%02d", time.hour, time.minute, time.second)
//}

