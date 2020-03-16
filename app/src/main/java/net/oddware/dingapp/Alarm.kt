package net.oddware.dingapp

import android.net.Uri
import java.io.Serializable
import java.time.LocalTime

data class Alarm(
    var name: String? = null,
    var repetitions: Int = 0,
    var time: LocalTime? = null,
    var soundUri: Uri? = null
) : Serializable {

    //fun getTimeString(): String =
    //    time?.format(DateTimeFormatter.ofPattern("hh:mm:ss")) ?: "00:00:00"
}
