package net.oddware.dingapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AlarmViewModel(application: Application) : AndroidViewModel(application) {
    // The reason for not initializing directly is that when we later set up room and such,
    // we'll need a DB before we can get the DAO which we should pass on to the repo
    private val alarmRepo: AlarmRepository
    val alarms: LiveData<List<Alarm>>
    //val alarms: MutableLiveData<MutableList<Alarm>>

    init {
        // set up repo, db etc.
        alarmRepo = AlarmRepository
        @Suppress("UNCHECKED_CAST")
        alarms = alarmRepo.alarms as LiveData<List<Alarm>>
        //alarms = alarmRepo.alarms
    }

    fun add(alarm: Alarm) = viewModelScope.launch {
        alarmRepo.add(alarm)
    }

    fun clear() = viewModelScope.launch {
        alarmRepo.clear()
    }

    fun removeAt(index: Int) = viewModelScope.launch {
        alarmRepo.removeAt(index)
    }

    fun remove(alarm: Alarm) = viewModelScope.launch {
        alarmRepo.remove(alarm)
    }

    fun disableAll() = viewModelScope.launch {
        alarmRepo.disableAll()
    }


}
