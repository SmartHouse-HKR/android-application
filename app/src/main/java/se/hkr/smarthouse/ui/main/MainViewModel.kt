package se.hkr.smarthouse.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import se.hkr.smarthouse.mqtt.MqttConnection
import se.hkr.smarthouse.repository.main.MainRepository
import se.hkr.smarthouse.ui.BaseViewModel
import se.hkr.smarthouse.ui.DataState
import se.hkr.smarthouse.ui.main.state.LampState
import se.hkr.smarthouse.ui.main.state.MainStateEvent
import se.hkr.smarthouse.ui.main.state.MainViewState
import se.hkr.smarthouse.ui.main.state.PublishFields
import se.hkr.smarthouse.ui.main.state.SubscribeFields
import se.hkr.smarthouse.util.AbsentLiveData
import javax.inject.Inject

class MainViewModel
@Inject
constructor(
    val mainRepository: MainRepository
) : BaseViewModel<MainStateEvent, MainViewState>() {
    override fun handleStateEvent(stateEvent: MainStateEvent): LiveData<DataState<MainViewState>> {
        // Temporary until the functionality is fixed
        when (stateEvent) {
            is MainStateEvent.PublishAttemptEvent -> {
                return mainRepository.attemptPublish(
                    stateEvent.topic,
                    stateEvent.message,
                    stateEvent.qos
                )
            }
            is MainStateEvent.SubscribeAttemptEvent -> {
                // TODO Implement subscribe using MVI as well
                return AbsentLiveData.create<DataState<MainViewState>>()
            }
        }
    }

    override fun initNewViewState(): MainViewState {
        return MainViewState()
    }

    fun subscribeTo(topic: String) {
        // TODO avoid breaking the MVI patter
        Log.d(TAG, "Subscribing to $topic")
        MqttConnection.mqttClient.subscribe(topic) { subscribedTopic, messageReceived ->
            val received = messageReceived.toString()
            Log.d(TAG, "Got: $received")
            val state = received == "true"
            _viewState.postValue(MainViewState(lampState = LampState(state)))
        }
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

    fun cancelActiveJobs() {
        mainRepository.cancelActiveJobs()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}