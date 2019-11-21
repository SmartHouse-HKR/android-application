package se.hkr.smarthouse.mqtt.responses

class MqttConnectionResponse(
    var successful: Boolean = false
) {
    override fun toString(): String {
        return "MqttConnectionResponse(successful=$successful)"
    }
}