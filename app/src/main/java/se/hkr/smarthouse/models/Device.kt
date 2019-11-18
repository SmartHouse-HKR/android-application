package se.hkr.smarthouse.models

sealed class Device(
    open val topic: String
) {
    fun getName(): String {
        return topic.split("/").last()
    }
}

data class InteractiveOnOff(
    override val topic: String,
    var state: Boolean = false
) : Device(topic) {
    companion object {
        const val IDENTIFIER = 0
    }
}

data class ObservableOnOff(
    override val topic: String,
    var state: Boolean = false
) : Device(topic) {
    companion object {
        const val IDENTIFIER = 1
    }
}

data class InteractiveTemperature(
    override val topic: String,
    var temperature: Float = 0f
) : Device(topic) {
    companion object {
        const val IDENTIFIER = 2
    }
}

data class ObservableTemperature(
    override val topic: String,
    var temperature: Float = 0f
) : Device(topic) {
    companion object {
        const val IDENTIFIER = 3
    }
}

data class InteractiveRgb(
    override val topic: String,
    var red: Int = 0,
    var green: Int = 0,
    var blue: Int = 0
) : Device(topic) {
    companion object {
        const val IDENTIFIER = 4
    }
}
