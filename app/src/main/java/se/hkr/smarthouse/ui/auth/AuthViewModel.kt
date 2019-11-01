package se.hkr.smarthouse.ui.auth

import androidx.lifecycle.LiveData
import se.hkr.androidjetpack.ui.auth.state.AuthStateEvent
import se.hkr.smarthouse.models.AccountCredentials
import se.hkr.smarthouse.repository.auth.AuthRepository
import se.hkr.smarthouse.ui.BaseViewModel
import se.hkr.smarthouse.ui.DataState
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
                return AbsentLiveData.create()
            }
            is AuthStateEvent.RegisterAttemptEvent -> {
                return AbsentLiveData.create()
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

    fun setAccountCredentials(authToken: AccountCredentials) {
        val newViewState = getCurrentViewStateOrNew()
        if (newViewState.accountCredentials == authToken) {
            return
        }
        newViewState.accountCredentials = authToken
        _viewState.value = newViewState
    }
}