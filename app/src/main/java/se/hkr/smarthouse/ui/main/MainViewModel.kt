package se.hkr.smarthouse.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import se.hkr.smarthouse.models.Device
import se.hkr.smarthouse.mqtt.MqttConnection
import se.hkr.smarthouse.repository.main.MainRepository
import se.hkr.smarthouse.ui.BaseViewModel
import se.hkr.smarthouse.ui.DataState
import se.hkr.smarthouse.ui.main.state.*
import se.hkr.smarthouse.util.AbsentLiveData
import se.hkr.smarthouse.util.Constants
import javax.inject.Inject

class MainViewModel
@Inject
constructor(
    val mainRepository: MainRepository
) : BaseViewModel<MainStateEvent, MainViewState>() {
    override fun handleStateEvent(stateEvent: MainStateEvent): LiveData<DataState<MainViewState>> {
        // Temporary until the functionality is fixed
        return when (stateEvent) {
            is MainStateEvent.PublishAttemptEvent -> {
                mainRepository.attemptPublish(
                    stateEvent.topic,
                    stateEvent.message
                )
            }
            is MainStateEvent.SubscribeAttemptEvent -> {
                // TODO Implement subscribe using MVI as well
                AbsentLiveData.create()
            }
            is MainStateEvent.UpdateDeviceListEvent -> {
                mainRepository.updateDeviceList(
                    stateEvent.list
                )
            }
        }
    }

    override fun initNewViewState(): MainViewState {
        return MainViewState()
    }

    fun initializeMqttSubscription() {
        // TODO explore the idea of not doing this in the ViewModel and somehow have the repository
        //  do it instead. Right now feeling like it is breaking the MVI pattern.
        MqttConnection.mqttClient.subscribe("${Constants.BASE_TOPIC}/#") { topic, message ->
            Log.d(TAG, "MainViewModel: subscription received topic: $topic, message: $message")
            GlobalScope.launch(Main) {
                setDevicesFields(
                    deviceFields = DeviceFields(
                        deviceList = mutableListOf(
                            Device.builder(topic, message.toString())
                        )
                    )
                )
            }
        }
    }

    fun setPublishFields(publishFields: PublishFields) {
        val newViewState = getCurrentViewStateOrNew()
        if (newViewState.publishFields == publishFields) {
            return
        }
        newViewState.publishFields = publishFields
        setViewState(newViewState)
    }

    fun setSubscribeFields(subscribeTopics: SubscribeTopics) {
        val newViewState = getCurrentViewStateOrNew()
        if (newViewState.subscribeTopics == subscribeTopics) {
            return
        }
        newViewState.subscribeTopics = subscribeTopics
        setViewState(newViewState)
    }

    fun setDevicesFields(deviceFields: DeviceFields) {
        val newViewState = getCurrentViewStateOrNew()
        if (newViewState.deviceFields == deviceFields) {
            return
        }
        // TODO check if the ?. on the devices Fields and the !! on the list is okay to do.
        val newDevices = deviceFields.deviceList!!
        newViewState.deviceFields?.addDevice(newDevices)
        setViewState(newViewState)
    }

    fun cancelActiveJobs() {
        mainRepository.cancelActiveJobs()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}