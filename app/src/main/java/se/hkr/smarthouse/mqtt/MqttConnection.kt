package se.hkr.smarthouse.mqtt

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import se.hkr.smarthouse.mqtt.responses.MqttResponse
import java.util.*

object MqttConnection {
    val TAG: String = "AppDebug"
    var mqttClient: MqttClient? = null

    fun connect(
        username: String,
        password: String,
        hostUrl: String
    ): LiveData<MqttResponse> {
        val liveData = MutableLiveData<MqttResponse>()
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
            mqttClient!!.connect(connectionOptions)
            liveData.value = MqttResponse(true)
            Log.d(TAG, "Mqtt connection success")
        } catch (e: Exception) {
            liveData.value = MqttResponse(false)
            Log.e(TAG, "Mqtt connection failure")
            Log.e(TAG, "Exception stack trace: ", e)
        }
        return liveData
    }
}