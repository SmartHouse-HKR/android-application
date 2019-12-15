package se.hkr.smarthouse.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import se.hkr.smarthouse.models.deviceBuilder
import se.hkr.smarthouse.mqtt.MqttConnection
import se.hkr.smarthouse.repository.main.MainRepository
import se.hkr.smarthouse.ui.BaseViewModel
import se.hkr.smarthouse.ui.DataState
import se.hkr.smarthouse.ui.main.state.*
import se.hkr.smarthouse.util.AbsentLiveData
import se.hkr.smarthouse.util.Constants
import se.hkr.smarthouse.util.Filters
import javax.inject.Inject

class MainViewModel
@Inject
constructor(
    val mainRepository: MainRepository
) : BaseViewModel<MainStateEvent, MainViewState>() {
    init {
        initializeMqttSubscription()
    }

    override fun handleStateEvent(stateEvent: MainStateEvent): LiveData<DataState<MainViewState>> {
        // Temporary until the functionality is fixed
        return when (stateEvent) {
            is MainStateEvent.PublishAttemptEvent -> {
                mainRepository.attemptPublish(
                    stateEvent.topic,
                    stateEvent.message
                )
            }
            is MainStateEvent.UpdateDeviceListEvent -> {
                mainRepository.addNewDevice(
                    stateEvent.device
                )
            }
            is MainStateEvent.SubscribeAttemptEvent -> {
                // TODO Implement subscribe using MVI as well
                AbsentLiveData.create()
            }
        }
    }

    override fun initNewViewState(): MainViewState {
        return MainViewState()
    }

    private fun initializeMqttSubscription() {
        // Happening inside the ViewModel so that it does not happen more than once on
        // configuration change. Should look into cleaning this up somehow at the end.
        Log.d(BaseMainFragment.TAG, "Initializing MQTT subscription")
        MqttConnection.mqttClient.subscribe("${Constants.BASE_TOPIC}/#") { topic, message ->
            Log.d(
                BaseMainFragment.TAG,
                "MainViewModel: subscription received topic: $topic, message: $message"
            )
            GlobalScope.launch(Main) {
                setStateEvent(
                    MainStateEvent.UpdateDeviceListEvent(
                        device = deviceBuilder(topic, message.toString())
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
        val newDevices = deviceFields.deviceList!!
        newViewState.deviceFields?.mergeLists(newDevices)
        setViewState(newViewState)
    }

    fun getFilter(): Regex {
        return getCurrentViewStateOrNew().deviceFields?.filter ?: Filters.any
    }

    fun setDeviceFilter(filter: Regex) {
        val newViewState = getCurrentViewStateOrNew()
        if (newViewState.deviceFields?.filter == filter) {
            return
        }
        newViewState.deviceFields?.filter = filter
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