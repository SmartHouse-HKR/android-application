package se.hkr.smarthouse.mqtt

import se.hkr.smarthouse.models.Device

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
        Device.InteractiveOnOff("Smarthome/livingRoom/bluetoothFan"),
        Device.InteractiveOnOff("Smarthome/livingRoom/lamp"),
        Device.InteractiveOnOff("Smarthome/livingRoom/bluetoothCeilingLight"),
        Device.ObservableOnOff("Smarthome/livingRoom/windowSensor"),
        Device.InteractiveTemperature("Smarthome/livingRoom/heater"),
        Device.ObservableTemperature("Smarthome/livingRoom/temperatureSensor"),

        Device.InteractiveOnOff("Smarthome/attic/fan"),
        Device.InteractiveTemperature("Smarthome/attic/heater"),

        Device.ObservableOnOff("Smarthome/kitchen/fireAlarm"),
        Device.ObservableOnOff("Smarthome/kitchen/waterLeakageSensor"),

        Device.ObservableOnOff("Smarthome/outside/alarm"),
        Device.ObservableOnOff("Smarthome/outside/securityLight"),
        Device.ObservableOnOff("Smarthome/outside/twilightSensor"),
        Device.ObservableTemperature("Smarthome/outside/temperatureSensor"),

        Device.InteractiveRgb("Smarthome/rgbLight", 20, 40, 50)
    )
}
