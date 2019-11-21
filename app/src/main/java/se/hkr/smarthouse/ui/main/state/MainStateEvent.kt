package se.hkr.smarthouse.ui.main.state

import se.hkr.smarthouse.models.Device

sealed class MainStateEvent {
    data class SubscribeAttemptEvent(
        val topic: String
    ) : MainStateEvent()

    data class PublishAttemptEvent(
        val topic: String,
        val message: String
    ) : MainStateEvent()

    data class UpdateDeviceListEvent(
        val list: MutableList<Device>
    ) : MainStateEvent()
}