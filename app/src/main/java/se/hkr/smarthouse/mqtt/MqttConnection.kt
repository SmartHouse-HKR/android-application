package se.hkr.smarthouse.mqtt

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import se.hkr.smarthouse.mqtt.responses.MqttConnectionResponse
import se.hkr.smarthouse.util.Constants
import java.util.*

object MqttConnection {
    const val TAG: String = "AppDebug"
    lateinit var mqttClient: MqttClient

    fun publish(
        topic: String,
        message: String
    ): LiveData<MqttConnectionResponse> {
        val liveData = MutableLiveData<MqttConnectionResponse>()
        try {
            Log.d(TAG, "MqttConnection: publishing: topic: $topic, message: $message")
            val mqttMessage = MqttMessage(message.toByteArray())
            mqttMessage.qos = Constants.QOS
            mqttMessage.isRetained = true
            mqttClient.publish(topic, mqttMessage)
            liveData.value = MqttConnectionResponse(true)
            Log.d(TAG, "Mqtt connection success")
        } catch (e: Exception) {
            liveData.value = MqttConnectionResponse(false)
            Log.e(TAG, "Mqtt connection failure")
            Log.e(TAG, "Exception stack trace: ", e)
        }
        return liveData
    }

    fun connect(
        username: String,
        password: String,
        hostUrl: String
    ): LiveData<MqttConnectionResponse> {
        val liveData = MutableLiveData<MqttConnectionResponse>()
        try {
            mqttClient = MqttClient(
                hostUrl,
                UUID.randomUUID().toString(), // TODO actually use correct UUID, or take this and insert to DB
                MemoryPersistence()
            )
            val connectionOptions = MqttConnectOptions()
            connectionOptions.isCleanSession = true
            // To be used when authentication is required
            // connectionOptions.userName = username
            // connectionOptions.password = password.toCharArray()
            mqttClient.connect(connectionOptions)
            liveData.value = MqttConnectionResponse(true)
            Log.d(TAG, "Mqtt connection success")
        } catch (e: Exception) {
            liveData.value = MqttConnectionResponse(false)
            Log.e(TAG, "Mqtt connection failure")
            Log.e(TAG, "Exception stack trace: ", e)
        }
        return liveData
    }
}