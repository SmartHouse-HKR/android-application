package se.hkr.smarthouse.session

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
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
    private val _cachedToken = MutableLiveData<AccountCredentials>()
    val cachedToken: LiveData<AccountCredentials>
        get() = _cachedToken

    fun setValue(newValue: AccountCredentials?) {
        GlobalScope.launch(Main) {
            if (_cachedToken.value != newValue) {
                _cachedToken.value = newValue
            }
        }
    }

    fun login(newValue: AccountCredentials) {
        setValue(newValue)
    }

    fun logout() {
        Log.d(TAG, "Logging out...")
        CoroutineScope(Main).launch {
            var errorMessage: String? = null
            errorMessage?.let {
                Log.e(TAG, "logout: $errorMessage")
            }
            Log.d(TAG, "Logout: finally was called")
            setValue(null)
        }
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
