package se.hkr.smarthouse.mqtt.responses

class MqttResponse(
    var successful: Boolean = false
) {
    override fun toString(): String {
        return "MqttResponse(successful=$successful)"
    }
}