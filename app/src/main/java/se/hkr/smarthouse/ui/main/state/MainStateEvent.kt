package se.hkr.smarthouse.ui.main.state

sealed class MainStateEvent {
    data class SubscribeAttemptEvent(
        val topic: String
    ) : MainStateEvent()

    data class PublishAttemptEvent(
        val topic: String,
        val message: String,
        val qos: Int
    ) : MainStateEvent()
}