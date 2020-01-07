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

    data class Trigger(
        override var topic: String,
        var triggered: Boolean? = null
    ) : Device(topic) {
        companion object {
            const val IDENTIFIER = 7
        }
    }

    data class Microwave(
        override var topic: String,
        var manualStart: String? = null,
        var presetStart: MicrowavePreset? = null,
        var error: Boolean? = null
    ) : Device(topic) {
        companion object {
            const val IDENTIFIER = 8
        }

        enum class MicrowavePreset {
            DEFROST,
            CHICKEN,
            FISH,
            POULTRY
        }
    }

    data class BluetoothFan(
        override var topic: String,
        var state: Boolean? = null,
        var swing: Boolean? = null,
        var speed: Boolean? = null,
        var mode: Boolean = true
    ) : Device(topic) {
        companion object {
            const val IDENTIFIER = 9
        }
    }

    data class BluetoothLamp(
        override var topic: String,
        var state: String = "0000"
    ) : Device(topic) {
        companion object {
            const val IDENTIFIER = 10
        }
    }
}

fun Device.getSimpleName(): String {
    return topic.split("/").last()
}

fun deviceBuilder(topic: String, message: String): Device {
    val splitTopic = topic.split("/")
    if (splitTopic.size != 3) { // Topic should ALWAYS 3 parts as described in the current protocol.
        return Device.UnknownDevice("Unknown_$topic", message)
    }
    val newDevice = when {
        topic.contains("light") || topic.contains("lamp") -> {
            when {
                topic.contains("bt_") -> {
                    when {
                        topic.contains("light") -> {
                            Device.BluetoothLamp(
                                topic = topic,
                                state = message
                            )
                        }
                        else -> Device.Light(topic, (message == "on"))
                    }
                }
                topic.contains("outdoor") -> {
                    when {
                        topic.contains("state") -> {
                            Device.Alarm(topic = topic, active = (message == "on"))
                        }
                        topic.contains("trigger") -> {
                            Device.Alarm(topic = topic, triggered = (message == "true"))
                        }
                        else -> Device.Alarm(topic = topic)
                    }
                }
                else -> Device.Light(topic, (message == "on"))
            }
        }
        topic.contains("lamp") -> Device.Light(topic, (message == "on"))
        topic.contains("temperature") -> Device.Temperature(topic, message)
        topic.contains("voltage") -> {
            when {
                topic.contains("value") -> Device.Voltage(topic, message)
                else -> Device.Voltage(topic)
            }
        }
        topic.contains("oven") -> Device.Oven(topic, (message == "on"))
        topic.contains("fan") -> {
            when {
                topic.contains("bt_") -> {
                    when {
                        topic.contains("state") -> {
                            Device.BluetoothFan(topic = topic, state = (message == "on"))
                        }
                        topic.contains("swing") -> {
                            Device.BluetoothFan(topic = topic, swing = (message == "true"))
                        }
                        topic.contains("speed") -> {
                            Device.BluetoothFan(topic = topic, speed = (message == "higher"))
                        }
                        topic.contains("mode") -> {
                            Device.BluetoothFan(topic = topic, mode = true)
                        }
                        topic.contains("timer") -> {
                            Device.BluetoothFan(topic = topic, mode = true)
                        }
                        else -> Device.BluetoothFan(topic = topic, mode = true)
                    }
                }
                else -> Device.Fan(topic, message)
            }
        }
        topic.contains("heater") -> {
            when {
                topic.contains("state") -> {
                    Device.Heater(topic = topic, state = (message == "on"))
                }
                topic.contains("value") -> Device.Heater(topic = topic, value = message)
                else -> Device.Heater(topic)
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
                else -> Device.Alarm(topic)
            }
        }
        topic.contains("trigger") -> {
            Device.Trigger(topic = topic, triggered = (message == "true"))
        }
        topic.contains("microwave") -> {
            when {
                topic.contains("manual_start") -> {
                    Device.Microwave(
                        topic = topic,
                        manualStart = message
                    )
                }
                topic.contains("preset_start") -> {
                    Device.Microwave(
                        topic = topic,
                        presetStart = when (message) {
                            "defrost" -> Device.Microwave.MicrowavePreset.DEFROST
                            "chicken" -> Device.Microwave.MicrowavePreset.CHICKEN
                            "fish" -> Device.Microwave.MicrowavePreset.FISH
                            "poultry" -> Device.Microwave.MicrowavePreset.POULTRY
                            else -> null
                        }
                    )
                }
                topic.contains("error") -> {
                    Device.Microwave(
                        topic = topic,
                        error = (message != "no error")
                    )
                }
                else -> Device.Microwave(topic)
            }
        }
        else -> Device.UnknownDevice("Unknown_$topic")
    }
    newDevice.topic = newDevice.topic.split("/").take(2).joinToString(separator = "/")
    return newDevice
}
