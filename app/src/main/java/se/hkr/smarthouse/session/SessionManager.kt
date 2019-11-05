package se.hkr.smarthouse.session

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import se.hkr.smarthouse.models.AccountCredentials
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager
@Inject
constructor(
    val application: Application
) {
    private val TAG: String = "AppDebug"
    private val _cachedAccountCredentials = MutableLiveData<AccountCredentials>()
    val cachedAccountCredentials: LiveData<AccountCredentials>
        get() = _cachedAccountCredentials

    fun setValue(newValue: AccountCredentials?) {
        GlobalScope.launch(Main) {
            if (_cachedAccountCredentials.value != newValue) {
                _cachedAccountCredentials.value = newValue
            }
        }
    }

    fun login(accountCredentials: AccountCredentials) {
        Log.d(TAG, "Logging in: $accountCredentials")
        setValue(accountCredentials)
    }

    fun logout() {
        Log.d(TAG, "Logging out from: ${cachedAccountCredentials.value}")
        setValue(null)
    }

    fun isConnectedToTheInternet(): Boolean {
        val connectivityManager =
            application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        try {
            return connectivityManager.activeNetworkInfo.isConnected
        } catch (e: Exception) {
            Log.e(TAG, "isConnectedToTheInternet ${e.message}")
        }
        return false
    }
}
