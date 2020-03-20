package net.oddware.dingapp

import androidx.lifecycle.MutableLiveData
import timber.log.Timber

// See: https://stackoverflow.com/questions/47941537/notify-observer-when-item-is-added-to-list-of-livedata
// For thoughts on this
//fun <T> MutableLiveData<T>.notifyObserver() {
//    this.value = this.value
//}
// This is one way to do it with generics, but for my use here, I'll make it specific for alarms
//inline fun <reified T> MutableLiveData<MutableList<T>>.addItem(item: T) {
//    val lst = this.value ?: mutableListOf()
//    lst.add(item)
//    this.value = lst
//}

object AlarmRepository {
    val alarms: MutableLiveData<MutableList<Alarm>> = MutableLiveData()

    private inline fun MutableLiveData<MutableList<Alarm>>.add(alarm: Alarm) {
        val lst = value ?: mutableListOf()
        lst.add(alarm)
        value = lst
    }

    private inline fun MutableLiveData<MutableList<Alarm>>.remove(alarm: Alarm): Boolean {
        //if (null == value) {
        //    return false
        //}
        // Actually, I think I prefer the alarm to be at least disabled no matter if it's in the list or not
        alarm.disable() // make sure we have nothing running in the background for this alarm
        val result = value?.remove(alarm) ?: false
        value = value
        return result
    }

    private inline fun MutableLiveData<MutableList<Alarm>>.removeAt(index: Int): Boolean {
        val alarm = value?.removeAt(index) ?: return false
        alarm.disable()
        value = value
        return true
    }

    private inline fun MutableLiveData<MutableList<Alarm>>.get(index: Int): Alarm? {
        return value?.get(index)
    }

    private inline fun MutableLiveData<MutableList<Alarm>>.disableAll(notify: Boolean) {
        value?.let {
            for (alarm in it) {
                alarm.disable()
            }
        }
        if (notify && null != value) {
            value = value
        }
    }

    private inline fun MutableLiveData<MutableList<Alarm>>.clear() {
        if (null == value) {
            return
        }
        disableAll(false) // pass false, as we do the "notify" at the end of this function
        value?.clear()
        value = value
    }


    fun add(alarm: Alarm) {
        Timber.d("Adding alarm to list...")
        alarms.add(alarm)
    }

    fun get(index: Int): Alarm? {
        return alarms.get(index)
    }

    fun clear() {
        Timber.d("Deleting all alarms")
        alarms.clear()
    }

    fun remove(alarm: Alarm): Boolean {
        return alarms.remove(alarm)
    }

    fun removeAt(index: Int): Boolean {
        return alarms.removeAt(index)
    }

    fun disableAll() {
        alarms.disableAll(true)
    }
}
