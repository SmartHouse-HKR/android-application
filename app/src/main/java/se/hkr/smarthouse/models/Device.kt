package se.hkr.smarthouse.models

const val TAG = "AppDebug"

// One data class per different view inflated on the recycler view.
sealed class Device(
    open var topic: String
) {
    data class UnknownDevice(
        override var topic: String,
        var message: String = ""
    ) : Device(topic) {
        companion object {
            const val IDENTIFIER = -1
        }
    }

    data class Light(
        override var topic: String,
        var state: Boolean = false
    ) : Device(topic) {
        companion object {
            const val IDENTIFIER = 0
        }
    }

    data class Temperature(
        override var topic: String,
        var temperature: String = "0"
    ) : Device(topic) {
        companion object {
            const val IDENTIFIER = 1
        }
    }

    data class Voltage(
        override var topic: String,
        var voltage: String = "0"
    ) : Device(topic) {
        companion object {
            const val IDENTIFIER = 2
        }
    }

    data class Oven(
        override var topic: String,
        var state: Boolean = false
    ) : Device(topic) {
        companion object {
            const val IDENTIFIER = 3
        }
    }

    data class Fan(
        override var topic: String,
        var speed: String = "0"
    ) : Device(topic) {
        companion object {
            const val IDENTIFIER = 4
        }
    }

    data class Heater(
        override var topic: String,
        var state: Boolean? = null,
        var value: String? = null
    ) : Device(topic) {
        companion object {
            const val IDENTIFIER = 5
        }
    }

    data class Alarm(
        override var topic: String,
        var active: Boolean? = null,
        var triggered: Boolean? = null
    ) : Device(topic) {
        companion object {
            const val IDENTIFIER = 6
        }
    }
}

fun Device.getSimpleName(): String {
    return topic.split("/").last()
}

fun topicToDevice(topic: String): Device {
    val splitTopic = topic.split("/")
    if (splitTopic.size != 3) { // Topic should ALWAYS 3 parts as described in the current protocol.
        return Device.UnknownDevice(topic)
    }
    return when {
        topic.contains("light") -> Device.Light(topic)
        topic.contains("temperature") -> Device.Temperature(topic)
        topic.contains("voltage") -> Device.Voltage(topic)
        topic.contains("oven") -> Device.Oven(topic)
        topic.contains("fan") -> Device.Fan(topic)
        topic.contains("heater") -> Device.Heater(topic)
        topic.contains("alarm") -> Device.Alarm(topic)
        else -> Device.UnknownDevice(topic)
    }
}

fun deviceBuilder(topic: String, message: String): Device {
    val splitTopic = topic.split("/")
    if (splitTopic.size != 3) { // Topic should ALWAYS 3 parts as described in the current protocol.
        return Device.UnknownDevice(topic, message)
    }
    val newDevice = when {
        topic.contains("light") -> Device.Light(topic, (message == "on"))
        topic.contains("temperature") -> Device.Temperature(topic, message)
        topic.contains("voltage") -> Device.Voltage(topic, message)
        topic.contains("oven") -> Device.Oven(topic, (message == "on"))
        topic.contains("fan") -> Device.Fan(topic, message)
        topic.contains("heater") -> {
            when {
                topic.contains("state") -> {
                    Device.Heater(topic = topic, state = (message == "on"))
                }
                topic.contains("value") -> Device.Heater(topic = topic, value = message)
                else -> Device.UnknownDevice(topic)
            }
        }
        topic.contains("alarm") -> {
            when {
                topic.contains("state") -> {
                    Device.Alarm(topic = topic, active = (message == "on"))
                }
                topic.contains("trigger") -> {
                    Device.Alarm(topic = topic, triggered = (message == "true"))
                }
                else -> Device.UnknownDevice(topic)
            }
        }
        else -> Device.UnknownDevice(topic)
    }
    newDevice.topic = newDevice.topic.split("/").take(2).joinToString(separator = "/")
    return newDevice
}
