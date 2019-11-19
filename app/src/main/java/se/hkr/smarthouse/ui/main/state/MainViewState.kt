package se.hkr.smarthouse.ui.main.state

import se.hkr.smarthouse.models.Device

data class MainViewState(
    var subscribeFields: SubscribeFields? = SubscribeFields(),
    var publishFields: PublishFields? = PublishFields(),
    var deviceFields: DeviceFields? = DeviceFields()
)

data class DeviceFields(
    var deviceList: MutableList<Device>? = null
) {
    fun addDevice(newDeviceList: MutableList<Device>) {
        deviceList?.let {
            deviceList?.forEach { oldDevice ->
                newDeviceList.forEach { newDevice ->
                    if (oldDevice.topic == newDevice.topic) {
                        deviceList?.remove(oldDevice)
                    }
                }
            }
        }
        if (deviceList == null) {
            deviceList = newDeviceList
            return
        }
        deviceList?.addAll(newDeviceList)
    }
}

data class SubscribeFields(
    var topic: String? = null
) {
    // TODO implement this as well
}

data class PublishFields(
    var topic: String? = null,
    var message: String? = null,
    var qos: Int = 0
) {
    class PublishError {
        companion object {
            fun mustFillAllFields(): String {
                return "You publish without all fields."
            }

            fun mustUseCorrectQos(): String {
                return "Qos must be 0, 1 or 2"
            }

            fun none(): String {
                return "None."
            }
        }
    }

    fun isValidForPublish(): String {
        if (topic.isNullOrEmpty()
            || message.isNullOrEmpty()
            || qos == -1
        ) {
            return PublishError.mustFillAllFields()
        }
        if (qos < 0 || qos > 2) {
            return PublishError.mustUseCorrectQos()
        }
        return PublishError.none()
    }

    override fun toString(): String {
        return "PublishFields(topic=$topic, message=$message, qos=$qos)"
    }
}
