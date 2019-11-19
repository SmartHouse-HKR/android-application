package se.hkr.smarthouse.ui.main

import androidx.lifecycle.LiveData
import se.hkr.smarthouse.repository.main.MainRepository
import se.hkr.smarthouse.ui.BaseViewModel
import se.hkr.smarthouse.ui.DataState
import se.hkr.smarthouse.ui.main.state.*
import se.hkr.smarthouse.util.AbsentLiveData
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
                    stateEvent.message,
                    stateEvent.qos
                )
            }
            is MainStateEvent.SubscribeAttemptEvent -> {
                // TODO Implement subscribe using MVI as well
                AbsentLiveData.create()
            }
            is MainStateEvent.UpdateDeviceListEvent -> {
                //TODO put into repository (with actual query of items?)
                return object : LiveData<DataState<MainViewState>>() {
                    override fun onActive() {
                        super.onActive()
                        value = DataState.data(
                            data = MainViewState(
                                deviceFields = DeviceFields(
                                    deviceList = stateEvent.list
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    override fun initNewViewState(): MainViewState {
        return MainViewState()
    }

    fun subscribeTo(topic: String) {
        /*// TODO avoid breaking the MVI patter
        Log.d(TAG, "Subscribing to $topic")
        MqttConnection.mqttClient.subscribe(topic) { subscribedTopic, messageReceived ->
            val received = messageReceived.toString()
            Log.d(TAG, "Got: $received")
            val state = received == "true"
            _viewState.postValue(MainViewState(deviceFields = DeviceFields(state)))
        }*/
    }

    fun setPublishFields(publishFields: PublishFields) {
        val newViewState = getCurrentViewStateOrNew()
        if (newViewState.publishFields == publishFields) {
            return
        }
        newViewState.publishFields = publishFields
        _viewState.value = newViewState
    }

    fun setSubscribeFields(subscribeFields: SubscribeFields) {
        val newViewState = getCurrentViewStateOrNew()
        if (newViewState.subscribeFields == subscribeFields) {
            return
        }
        newViewState.subscribeFields = subscribeFields
        _viewState.value = newViewState
    }

    fun setDevicesFields(deviceFields: DeviceFields) {
        val newViewState = getCurrentViewStateOrNew()
        if (newViewState.deviceFields == deviceFields) {
            return
        }
        // TODO check if the ?. on the devices Fields and the !! on the list is okay to do.
        val newDevices = deviceFields.deviceList!!
        newViewState.deviceFields?.addDevice(newDevices)
        _viewState.value = newViewState
    }

    fun cancelActiveJobs() {
        mainRepository.cancelActiveJobs()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}