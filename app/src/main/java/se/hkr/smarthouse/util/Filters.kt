package se.hkr.smarthouse.util

import se.hkr.smarthouse.util.Filters.regexAlarms
import se.hkr.smarthouse.util.Filters.regexFans
import se.hkr.smarthouse.util.Filters.regexHeaters
import se.hkr.smarthouse.util.Filters.regexLights

//TODO turn into sealed class to take advantage of exhaustive when statements?
object Filters {
    val regexLights = Regex("[\\w\\W]*(light|lamp)[\\w\\W]*")
    val regexHeaters = Regex("[\\w\\W]*(heater)[\\w\\W]*")
    val regexFans = Regex("[\\w\\W]*(fan)[\\w\\W]*")
    val regexAlarms = Regex("[\\w\\W]*(alarm|leak)[\\w\\W]*")
    val regexEtc = Regex("[\\w\\W]*(etc)[\\w\\W]*")
    val any = Regex("[\\w\\W]*")
}

fun String.isLight(): Boolean {
    return this.matches(regexLights)
}

fun String.isHeater(): Boolean {
    return this.matches(regexHeaters)
}

fun String.isFan(): Boolean {
    return this.matches(regexFans)
}

fun String.isAlarm(): Boolean {
    return this.matches(regexAlarms)
}

fun String.isEtc(): Boolean {
    return (!this.matches(regexLights) &&
            !this.matches(regexHeaters) &&
            !this.matches(regexFans) &&
            !this.matches(regexAlarms))
}
