package se.hkr.smarthouse.repository.auth

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Job
import se.hkr.smarthouse.models.AccountCredentials
import se.hkr.smarthouse.mqtt.MqttConnection
import se.hkr.smarthouse.mqtt.responses.MqttResponse
import se.hkr.smarthouse.persistence.AccountCredentialsDao
import se.hkr.smarthouse.repository.NetworkBoundResource
import se.hkr.smarthouse.session.SessionManager
import se.hkr.smarthouse.ui.DataState
import se.hkr.smarthouse.ui.Response
import se.hkr.smarthouse.ui.ResponseType
import se.hkr.smarthouse.ui.auth.state.AuthViewState
import se.hkr.smarthouse.ui.auth.state.LoginFields
import javax.inject.Inject

class AuthRepository
@Inject
constructor(
    val sessionManager: SessionManager,
    val accountCredentialsDao: AccountCredentialsDao
) {
    val TAG = "AppDebug"
    private var repositoryJob: Job? = null

    fun attemptLogin(
        username: String,
        password: String,
        hostUrl: String
    ): LiveData<DataState<AuthViewState>> {
        val loginFieldErrors = LoginFields(username, password, hostUrl).isValidForLogin()
        if (loginFieldErrors != LoginFields.LoginError.none()) {
            return returnErrorResponse(loginFieldErrors, ResponseType.Dialog())
        }
        return object : NetworkBoundResource<MqttResponse, AuthViewState>(
            sessionManager.isConnectedToTheInternet()
        ) {
            override fun handleResponse(response: MqttResponse) {
                Log.d(TAG, "handle response: $response")
                if (!response.successful) {
                    return onErrorReturn("Connection Failed", true, false)
                }
                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            accountCredentials = AccountCredentials(
                                account_pk = 1,
                                username = username,
                                password = password,
                                hostUrl = hostUrl
                            )
                        )
                    )
                )
            }

            override fun createCall(): LiveData<MqttResponse> {
                return MqttConnection.connect(
                    username = username,
                    password = password,
                    hostUrl = hostUrl
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
    ): LiveData<DataState<AuthViewState>> {
        Log.d(TAG, "returnErrorMessage: $errorMessage")
        return object : LiveData<DataState<AuthViewState>>() {
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