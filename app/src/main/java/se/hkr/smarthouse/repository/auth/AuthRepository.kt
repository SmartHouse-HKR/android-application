package se.hkr.smarthouse.repository.auth

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Job
import se.hkr.smarthouse.api.auth.OpenApiAuthService
import se.hkr.smarthouse.api.auth.network_responses.LoginResponse
import se.hkr.smarthouse.api.auth.network_responses.RegistrationResponse
import se.hkr.smarthouse.models.AuthToken
import se.hkr.smarthouse.persistence.AccountPropertiesDao
import se.hkr.smarthouse.persistence.AuthTokenDao
import se.hkr.smarthouse.repository.NetworkBoundResource
import se.hkr.smarthouse.session.SessionManager
import se.hkr.smarthouse.ui.DataState
import se.hkr.smarthouse.ui.Response
import se.hkr.smarthouse.ui.ResponseType
import se.hkr.smarthouse.ui.auth.state.AuthViewState
import se.hkr.smarthouse.ui.auth.state.LoginFields
import se.hkr.smarthouse.ui.auth.state.RegistrationFields
import se.hkr.smarthouse.util.ApiSuccessResponse
import se.hkr.smarthouse.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import se.hkr.smarthouse.util.GenericApiResponse
import javax.inject.Inject

class AuthRepository
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager
) {
    val TAG = "AppDebug"
    private var repositoryJob: Job? = null

    fun attemptLogin(
        email: String,
        password: String
    ): LiveData<DataState<AuthViewState>> {
        val loginFieldErrors = LoginFields(email, password).isValidForLogin()
        if (loginFieldErrors != LoginFields.LoginError.none()) {
            return returnErrorResponse(loginFieldErrors, ResponseType.Dialog())
        }
        return object : NetworkBoundResource<LoginResponse, AuthViewState>(
            sessionManager.isConnectedToTheInternet()
        ) {
            override suspend fun handleApiSuccessResponse(
                response: ApiSuccessResponse<LoginResponse>
            ) {
                Log.d(TAG, "handleApiSuccessResponse: $response")
                // Incorrect login credentials will still respond successfully
                if (response.body.response == GENERIC_AUTH_ERROR) {
                    return onErrorReturn(response.body.errorMessage, true, false)
                }
                // Here they are correctly authenticated
                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(response.body.pk, response.body.token)
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<LoginResponse>> {
                return openApiAuthService.login(email = email, password = password)
            }

            override fun setJob(job: Job) {
                cancelActiveJobs()
                repositoryJob = job
            }
        }.asLiveData()
    }

    fun attemptRegistration(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): LiveData<DataState<AuthViewState>> {
        val registerFieldErrors =
            RegistrationFields(
                email,
                username,
                password,
                confirmPassword
            ).isValidForRegistration()
        if (registerFieldErrors != RegistrationFields.RegistrationError.none()) {
            return returnErrorResponse(registerFieldErrors, ResponseType.Dialog())
        }
        return object : NetworkBoundResource<RegistrationResponse, AuthViewState>(
            sessionManager.isConnectedToTheInternet()
        ) {
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<RegistrationResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: $response")
                // If already exists, we get the generic auth error
                if (response.body.response == GENERIC_AUTH_ERROR) {
                    return onErrorReturn(response.body.errorMessage, true, false)
                }
                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(
                                response.body.pk,
                                response.body.token
                            )
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<RegistrationResponse>> {
                return openApiAuthService.register(email, username, password, confirmPassword)
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