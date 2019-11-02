package se.hkr.smarthouse.ui.auth

import androidx.lifecycle.LiveData
import se.hkr.smarthouse.models.AuthToken
import se.hkr.smarthouse.repository.auth.AuthRepository
import se.hkr.smarthouse.ui.BaseViewModel
import se.hkr.smarthouse.ui.DataState
import se.hkr.smarthouse.ui.auth.state.AuthStateEvent
import se.hkr.smarthouse.ui.auth.state.AuthViewState
import se.hkr.smarthouse.ui.auth.state.LoginFields
import se.hkr.smarthouse.ui.auth.state.RegistrationFields
import se.hkr.smarthouse.util.AbsentLiveData
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
    val authRepository: AuthRepository
) : BaseViewModel<AuthStateEvent, AuthViewState>() {
    override fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {
        // Temporary until the functionality is fixed
        when (stateEvent) {
            is AuthStateEvent.LoginAttemptEvent -> {
                return authRepository.attemptLogin(
                    stateEvent.email,
                    stateEvent.password
                )
            }
            is AuthStateEvent.RegisterAttemptEvent -> {
                return authRepository.attemptRegistration(
                    stateEvent.email,
                    stateEvent.username,
                    stateEvent.password,
                    stateEvent.confirm_password
                )
            }
            is AuthStateEvent.CheckPreviousAuthEvent -> {
                return AbsentLiveData.create()
            }
        }
    }

    override fun initNewViewState(): AuthViewState {
        return AuthViewState()
    }

    fun setRegistrationFields(registrationFields: RegistrationFields) {
        val newViewState = getCurrentViewStateOrNew()
        if (newViewState.registrationFields == registrationFields) {
            return
        }
        newViewState.registrationFields = registrationFields
        _viewState.value = newViewState
    }

    fun setLoginFields(loginFields: LoginFields) {
        val newViewState = getCurrentViewStateOrNew()
        if (newViewState.loginFields == loginFields) {
            return
        }
        newViewState.loginFields = loginFields
        _viewState.value = newViewState
    }

    fun setAuthToken(authToken: AuthToken) {
        val newViewState = getCurrentViewStateOrNew()
        if (newViewState.authToken == authToken) {
            return
        }
        newViewState.authToken = authToken
        _viewState.value = newViewState
    }

    fun cancelActiveJobs() {
        authRepository.cancelActiveJobs()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}