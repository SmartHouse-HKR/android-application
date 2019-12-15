package se.hkr.smarthouse.ui.main.state

import se.hkr.smarthouse.models.Device

const val TAG = "AppDebug"

data class MainViewState(
    var subscribeTopics: SubscribeTopics? = SubscribeTopics(),
    var publishFields: PublishFields? = PublishFields(),
    var deviceFields: DeviceFields? = DeviceFields()
)

data class DeviceFields(
    var deviceList: MutableList<Device>? = null,
    var filter: Regex? = null
) {
    fun mergeLists(newDeviceList: MutableList<Device>) {
        newDeviceList.forEach { addDevice(it) }
    }

    private fun addDevice(newDevice: Device) {
        if (deviceList == null) {
            deviceList = mutableListOf(newDevice)
            return
        }
        deviceList!!.forEachIndexed { oldListIndex, oldDevice ->
            if (oldDevice.topic == newDevice.topic) {
                deviceList!![oldListIndex] = mergeDevice(oldDevice, newDevice)
                return
            }
        }
        deviceList!!.add(newDevice)
        return
    }

    private fun <T : Device> mergeDevice(oldDevice: T, newDevice: T): T {
        return when (newDevice) {
            is Device.Heater -> {
                oldDevice as Device.Heater
                if (newDevice.state == null) newDevice.state = oldDevice.state
                if (newDevice.value == null) newDevice.value = oldDevice.value
                newDevice
            }
            is Device.Alarm -> {
                oldDevice as Device.Alarm
                if (newDevice.active == null) newDevice.active = oldDevice.active
                if (newDevice.triggered == null) newDevice.triggered = oldDevice.triggered
                newDevice
            }
            else -> newDevice
        }
    }
}

data class SubscribeTopics(
    var topics: MutableList<String>? = null
)

data class PublishFields(
    var topic: String? = null,
    var message: String? = null
) {
    class PublishError {
        companion object {
            fun mustFillAllFields(): String {
                return "You publish without all fields."
            }

            fun none(): String {
                return "None."
            }
        }
    }

    fun isValidForPublish(): String {
        if (topic.isNullOrEmpty()
            || message.isNullOrEmpty()
        ) {
            return PublishError.mustFillAllFields()
        }
        return PublishError.none()
    }

    override fun toString(): String {
        return "PublishFields(topic=$topic, message=$message)"
    }
}
