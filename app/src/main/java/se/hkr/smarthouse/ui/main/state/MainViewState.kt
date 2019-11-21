package se.hkr.smarthouse.ui.main.state

import android.util.Log
import se.hkr.smarthouse.models.Device

const val TAG = "AppDebug"

data class MainViewState(
    var subscribeFields: SubscribeFields? = SubscribeFields(),
    var publishFields: PublishFields? = PublishFields(),
    var deviceFields: DeviceFields? = DeviceFields()
)

data class DeviceFields(
    var deviceList: MutableList<Device>? = null
) {
    fun addDevice(newDeviceList: MutableList<Device>) {
        if (deviceList == null) {
            deviceList = newDeviceList
            return
        }
        // Really unhappy with this, need to figure out how to do this using immutable lists later
        var mutationsDone = 0
        deviceList?.let {
            deviceList?.forEachIndexed { oldListIndex, oldDevice ->
                newDeviceList.forEachIndexed { newListIndex, newDevice ->
                    if (oldDevice.topic == newDevice.topic) {
                        deviceList!![oldListIndex] = newDeviceList[newListIndex]
                        mutationsDone++
                    }
                }
            }
        }
        if (mutationsDone != 0) {
            return
        }
        if (newDeviceList.size != 0 && mutationsDone == 0) { // First condition might be redundant.
            // This is called when there is a brand new topic to the list.
            deviceList?.addAll(newDeviceList)
            return
        }
        Log.e(TAG, "Tried to add device with list: {$newDeviceList}\nBut nothing happened")
    }
}

data class SubscribeFields(
    var topic: String? = null
) {
    // TODO implement this as well
}

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
