package se.hkr.smarthouse.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Job
import se.hkr.smarthouse.mqtt.MqttConnection
import se.hkr.smarthouse.mqtt.responses.MqttResponse
import se.hkr.smarthouse.persistence.AccountCredentialsDao
import se.hkr.smarthouse.repository.NetworkBoundResource
import se.hkr.smarthouse.session.SessionManager
import se.hkr.smarthouse.ui.DataState
import se.hkr.smarthouse.ui.Response
import se.hkr.smarthouse.ui.ResponseType
import se.hkr.smarthouse.ui.main.state.LampState
import se.hkr.smarthouse.ui.main.state.MainViewState
import se.hkr.smarthouse.ui.main.state.PublishFields
import javax.inject.Inject

class MainRepository
@Inject
constructor(
    val sessionManager: SessionManager,
    val accountCredentialsDao: AccountCredentialsDao // Potentially don't need this here
) {
    val TAG = "AppDebug"
    private var repositoryJob: Job? = null

    fun attemptPublish(
        topic: String,
        message: String,
        qos: Int
    ): LiveData<DataState<MainViewState>> {
        val loginFieldErrors = PublishFields(topic, message, qos).isValidForPublish()
        if (loginFieldErrors != PublishFields.PublishError.none()) {
            return returnErrorResponse(loginFieldErrors, ResponseType.Dialog())
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
                            lampState = LampState(
                                state = message == "true" // TODO Change message to work differently?
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