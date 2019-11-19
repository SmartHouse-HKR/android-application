package se.hkr.smarthouse.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Job
import se.hkr.smarthouse.models.Device
import se.hkr.smarthouse.mqtt.MqttConnection
import se.hkr.smarthouse.mqtt.responses.MqttResponse
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

    fun attemptPublish(
        topic: String,
        message: String,
        qos: Int
    ): LiveData<DataState<MainViewState>> {
        val publishFieldErrors = PublishFields(topic, message, qos).isValidForPublish()
        if (publishFieldErrors != PublishFields.PublishError.none()) {
            return returnErrorResponse(publishFieldErrors, ResponseType.Dialog())
        }
        return object : NetworkBoundResource<MqttResponse, MainViewState>(
            sessionManager.isConnectedToTheInternet()
        ) {
            override fun handleResponse(response: MqttResponse) {
                Log.d(TAG, "handle response: $response")
                if (!response.successful) {
                    return onErrorReturn("Connection Failed", true, false)
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

            override fun createCall(): LiveData<MqttResponse> {
                return MqttConnection.publish(
                    topic = topic,
                    message = message,
                    qos = qos
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