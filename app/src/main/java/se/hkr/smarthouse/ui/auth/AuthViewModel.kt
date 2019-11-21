package se.hkr.smarthouse.ui.auth

import androidx.lifecycle.LiveData
import se.hkr.smarthouse.models.AccountCredentials
import se.hkr.smarthouse.repository.auth.AuthRepository
import se.hkr.smarthouse.ui.BaseViewModel
import se.hkr.smarthouse.ui.DataState
import se.hkr.smarthouse.ui.auth.state.AuthStateEvent
import se.hkr.smarthouse.ui.auth.state.AuthViewState
import se.hkr.smarthouse.ui.auth.state.LoginFields
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
                    stateEvent.username,
                    stateEvent.password,
                    stateEvent.hostUrl
                )
            }
        }
    }

    override fun initNewViewState(): AuthViewState {
        return AuthViewState()
    }

    fun setLoginFields(loginFields: LoginFields) {
        val newViewState = getCurrentViewStateOrNew()
        if (newViewState.loginFields == loginFields) {
            return
        }
        newViewState.loginFields = loginFields
        setViewState(newViewState)
    }

    fun setAccountCredentials(accountCredentials: AccountCredentials) {
        val newViewState = getCurrentViewStateOrNew()
        if (newViewState.accountCredentials == accountCredentials) {
            return
        }
        newViewState.accountCredentials = accountCredentials
        setViewState(newViewState)
    }

    fun cancelActiveJobs() {
        authRepository.cancelActiveJobs()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}