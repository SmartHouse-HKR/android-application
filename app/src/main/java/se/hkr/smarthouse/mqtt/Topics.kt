package se.hkr.smarthouse.mqtt

import se.hkr.smarthouse.models.Device
import se.hkr.smarthouse.util.Constants

object Topics {
    // TODO make it somehow to "Contains" lamp for example, not necessarily just "lamp" might need
    //  to be done on a layer above this, since hashMap needs to be strict with the key values
    val hashMap: HashMap<String, Any> = hashMapOf(
        "bluetoothFan" to Device.InteractiveOnOff,
        "lamp" to Device.InteractiveOnOff,
        "bluetoothCeilingLight" to Device.InteractiveOnOff,
        "fan" to Device.InteractiveOnOff,
        "fireAlarm" to Device.ObservableOnOff,
        "waterLeakageSensor" to Device.ObservableOnOff,
        "windowSensor" to Device.ObservableOnOff,
        "alarm" to Device.ObservableOnOff,
        "securityLight" to Device.ObservableOnOff,
        "twilightSensor" to Device.ObservableOnOff,
        "heater" to Device.InteractiveTemperature,
        "temperatureSensor" to Device.ObservableTemperature,
        "rgbLight" to Device.InteractiveRgb
    )

    val allTopicsList = mutableListOf<Device>(
        Device.InteractiveOnOff("${Constants.BASE_TOPIC}/livingRoom/bluetoothFan"),
        Device.InteractiveOnOff("${Constants.BASE_TOPIC}/livingRoom/lamp"),
        Device.InteractiveOnOff("${Constants.BASE_TOPIC}/livingRoom/bluetoothCeilingLight"),
        Device.ObservableOnOff("${Constants.BASE_TOPIC}/livingRoom/windowSensor"),
        Device.InteractiveTemperature("${Constants.BASE_TOPIC}/livingRoom/heater"),
        Device.ObservableTemperature("${Constants.BASE_TOPIC}/livingRoom/temperatureSensor"),

        Device.InteractiveOnOff("${Constants.BASE_TOPIC}/attic/fan"),
        Device.InteractiveTemperature("${Constants.BASE_TOPIC}/attic/heater"),

        Device.ObservableOnOff("${Constants.BASE_TOPIC}/kitchen/fireAlarm"),
        Device.ObservableOnOff("${Constants.BASE_TOPIC}/kitchen/waterLeakageSensor"),

        Device.ObservableOnOff("${Constants.BASE_TOPIC}/outside/alarm"),
        Device.ObservableOnOff("${Constants.BASE_TOPIC}/outside/securityLight"),
        Device.ObservableOnOff("${Constants.BASE_TOPIC}/outside/twilightSensor"),
        Device.ObservableTemperature("${Constants.BASE_TOPIC}/outside/temperatureSensor"),

        Device.InteractiveRgb("${Constants.BASE_TOPIC}/rgbLight", 20, 40, 50)
    )
}
