package se.hkr.smarthouse.util

import androidx.lifecycle.LiveData

// A LiveData class that has null value.
class AbsentLiveData<T : Any?>
private constructor(
) : LiveData<T>() {
    init {
        // Use postValue as it could be created in any Thread
        postValue(null)
    }

    companion object {
        fun <T> create(): LiveData<T> {
            return AbsentLiveData()
        }
    }
} 