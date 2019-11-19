package se.hkr.smarthouse.models

import android.util.Log
import se.hkr.smarthouse.mqtt.Topics

const val TAG = "AppDebug"

sealed class Device(
    open val topic: String
) {
    companion object {
        fun builder(topic: String, message: String): Device {
            return when (Topics.hashMap[topic]) {
                is InteractiveOnOff.Companion -> {
                    InteractiveOnOff(topic = topic, state = message == "true")
                }
                is ObservableOnOff.Companion -> {
                    ObservableOnOff(topic = topic, state = message == "true")
                }
                is InteractiveTemperature.Companion -> {
                    InteractiveTemperature(topic = topic, temperature = message.toFloat())
                }
                is ObservableTemperature.Companion -> {
                    ObservableTemperature(topic = topic, temperature = message.toFloat())
                }
                is InteractiveRgb.Companion -> {
                    // Assumes format of "128 50 34" aka "R G B" where R is the number of Red etc.
                    val colorsSplit = message.split(" ")
                    InteractiveRgb(
                        topic = topic,
                        red = colorsSplit[0].toInt(),
                        green = colorsSplit[1].toInt(),
                        blue = colorsSplit[2].toInt()
                    )
                }
                else -> {
                    Log.e(TAG, "Device, unknown device type, check the hashMap values maybe")
                    UnknownDevice(topic, message)
                }
            }
        }
    }

    fun getName(): String {
        return topic.split("/").last()
    }

    data class UnknownDevice(
        override val topic: String,
        var message: String = ""
    ) : Device(topic) {
        companion object {
            const val IDENTIFIER = -1
        }
    }

    data class InteractiveOnOff(
        override val topic: String,
        var state: Boolean = false
    ) : Device(topic) {
        companion object {
            const val IDENTIFIER = 0
        }
    }

    data class ObservableOnOff(
        override val topic: String,
        var state: Boolean = false
    ) : Device(topic) {
        companion object {
            const val IDENTIFIER = 1
        }
    }

    data class InteractiveTemperature(
        override val topic: String,
        var temperature: Float = 0f
    ) : Device(topic) {
        companion object {
            const val IDENTIFIER = 2
        }
    }

    data class ObservableTemperature(
        override val topic: String,
        var temperature: Float = 0f
    ) : Device(topic) {
        companion object {
            const val IDENTIFIER = 3
        }
    }

    data class InteractiveRgb(
        override val topic: String,
        var red: Int = 0,
        var green: Int = 0,
        var blue: Int = 0
    ) : Device(topic) {
        companion object {
            const val IDENTIFIER = 4
        }
    }
}