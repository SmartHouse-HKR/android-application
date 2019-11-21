package se.hkr.smarthouse.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<StateEvent, ViewState> : ViewModel() {
    val TAG: String = "AppDebug"
    protected val _stateEvent: MutableLiveData<StateEvent> = MutableLiveData()
    protected val _viewState: MutableLiveData<ViewState> = MutableLiveData()
    val viewState: LiveData<ViewState>
        get() = _viewState
    val dataState: LiveData<DataState<ViewState>> =
        Transformations
            .switchMap(_stateEvent) { stateEvent ->
                stateEvent?.let {
                    handleStateEvent(stateEvent)
                }
            }

    fun setStateEvent(event: StateEvent) {
        _stateEvent.value = event
    }

    fun setViewState(viewState: ViewState) {
        _viewState.value = viewState
    }

    fun getCurrentViewStateOrNew(): ViewState {
        return viewState.value?.let {
            it
        } ?: initNewViewState()
    }

    abstract fun handleStateEvent(
        stateEvent: StateEvent
    ): LiveData<DataState<ViewState>>

    abstract fun initNewViewState(): ViewState
}