package se.hkr.smarthouse.mqtt

import se.hkr.smarthouse.models.Device

object Topics {
    val hashMap: HashMap<String, Any> = hashMapOf(
        "livingRoomBluetoothFan" to Device.InteractiveOnOff,
        "livingRoomLamp" to Device.InteractiveOnOff,
        "livingRoomBluetoothCeilingLight" to Device.InteractiveOnOff,
        "atticFan" to Device.InteractiveOnOff,
        "kitchenFireAlarm" to Device.ObservableOnOff,
        "kitchenWaterLeakageSensor" to Device.ObservableOnOff,
        "livingRoomWindowSensor" to Device.ObservableOnOff,
        "outsideAlarm" to Device.ObservableOnOff,
        "outsideSecurityLight" to Device.ObservableOnOff,
        "outsideTwilightSensor" to Device.ObservableOnOff,
        "livingRoomHeater" to Device.InteractiveTemperature,
        "atticHeater" to Device.InteractiveTemperature,
        "livingRoomTempSensor" to Device.ObservableTemperature,
        "outsideTempSensor" to Device.ObservableTemperature,
        "rgbLight" to Device.InteractiveRgb
    )

    val allTopicsList = mutableListOf(
        Device.InteractiveOnOff("livingRoomBluetoothFan"),
        Device.InteractiveOnOff("livingRoomLamp"),
        Device.InteractiveOnOff("livingRoomBluetoothCeilingLight"),
        Device.InteractiveOnOff("atticFan"),
        Device.ObservableOnOff("kitchenFireAlarm"),
        Device.ObservableOnOff("kitchenWaterLeakageSensor"),
        Device.ObservableOnOff("livingRoomWindowSensor"),
        Device.ObservableOnOff("outsideAlarm"),
        Device.ObservableOnOff("outsideSecurityLight"),
        Device.ObservableOnOff("outsideTwilightSensor"),
        Device.InteractiveTemperature("livingRoomHeater"),
        Device.InteractiveTemperature("atticHeater"),
        Device.ObservableTemperature("livingRoomTempSensor"),
        Device.ObservableTemperature("outsideTempSensor"),
        Device.InteractiveRgb("rgbLight", 20, 40, 50)
    )
}
