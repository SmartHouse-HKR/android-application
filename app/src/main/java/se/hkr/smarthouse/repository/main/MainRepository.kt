package se.hkr.smarthouse.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Job
import se.hkr.smarthouse.models.Device
import se.hkr.smarthouse.mqtt.MqttConnection
import se.hkr.smarthouse.mqtt.responses.MqttConnectionResponse
import se.hkr.smarthouse.repository.NetworkBoundResource
import se.hkr.smarthouse.session.SessionManager
import se.hkr.smarthouse.ui.DataState
import se.hkr.smarthouse.ui.Response
import se.hkr.smarthouse.ui.ResponseType
import se.hkr.smarthouse.ui.main.state.DeviceFields
import se.hkr.smarthouse.ui.main.state.MainViewState
import se.hkr.smarthouse.ui.main.state.PublishFields
import javax.inject.Inject

class MainRepository
@Inject
constructor(
    val sessionManager: SessionManager
) {
    val TAG = "AppDebug"
    private var repositoryJob: Job? = null

    fun updateDeviceList(
        newList: MutableList<Device>
    ): LiveData<DataState<MainViewState>> {
        return object : LiveData<DataState<MainViewState>>() {
            override fun onActive() {
                super.onActive()
                value = DataState.data(
                    data = MainViewState(
                        deviceFields = DeviceFields(
                            deviceList = newList
                        )
                    )
                )
            }
        }
    }

    fun attemptPublish(
        topic: String,
        message: String
    ): LiveData<DataState<MainViewState>> {
        val publishFieldErrors = PublishFields(topic, message).isValidForPublish()
        if (publishFieldErrors != PublishFields.PublishError.none()) {
            return returnErrorResponse(publishFieldErrors, ResponseType.Dialog())
        }
        return object : NetworkBoundResource<MqttConnectionResponse, MainViewState>(
            sessionManager.isConnectedToTheInternet()
        ) {
            override fun handleResponse(response: MqttConnectionResponse) {
                Log.d(TAG, "handle response: $response")
                if (!response.successful) {
                    return onErrorReturn(
                        errorMessage = "Connection Failed",
                        shouldUseDialog = true,
                        shouldUseToast = false
                    )
                }
                onCompleteJob(
                    DataState.data(
                        data = MainViewState(
                            deviceFields = DeviceFields(
                                deviceList = mutableListOf(
                                    Device.builder(topic, message)
                                )
                            )
                        )
                    )
                )
            }

            override fun createCall(): LiveData<MqttConnectionResponse> {
                return MqttConnection.publish(
                    topic = topic,
                    message = message
                )
            }

            override fun setJob(job: Job) {
                cancelActiveJobs()
                repositoryJob = job
            }
        }.asLiveData()
    }

    private fun returnErrorResponse(
        errorMessage: String,
        responseType: ResponseType
    ): LiveData<DataState<MainViewState>> {
        Log.d(TAG, "returnErrorMessage: $errorMessage")
        return object : LiveData<DataState<MainViewState>>() {
            override fun onActive() {
                super.onActive()
                value = DataState.error(
                    Response(
                        message = errorMessage,
                        responseType = responseType
                    )
                )
            }
        }
    }

    fun cancelActiveJobs() {
        // Cancel all old jobs
        Log.d(TAG, "AuthRepository: Cancelling ongoing jobs")
        repositoryJob?.cancel()
    }
}