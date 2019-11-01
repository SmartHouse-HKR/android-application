package se.hkr.smarthouse.session

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import se.hkr.smarthouse.models.AccountCredentials
import se.hkr.smarthouse.persistence.AccountCredentialsDao
import javax.inject.Inject

class SessionManager
@Inject
constructor(
    val accountCredentialsDao: AccountCredentialsDao,
    val application: Application
) {
    private val TAG: String = "AppDebug"
    private val _cachedCredentials = MutableLiveData<AccountCredentials>()
    val cachedCredentials: LiveData<AccountCredentials>
        get() = _cachedCredentials

    fun login(newValue: AccountCredentials) {
        setValue(newValue)
    }

    fun setValue(newValue: AccountCredentials?) {
        GlobalScope.launch(Main) {
            if (_cachedCredentials.value != newValue) {
                _cachedCredentials.value = newValue
            }
        }
    }

    fun logout() {
        Log.d(TAG, "Logging out...")
        CoroutineScope(Main).launch {
            var errorMessage: String? = null
            try {
                _cachedCredentials.value?.pk?.let {
                    accountCredentialsDao.nullifyToken(it)
                } ?: throw CancellationException("Token Error. Logging out user")
            } catch (e: CancellationException) {
                Log.e(TAG, "logout: ${e.message}")
                errorMessage = e.message
            } catch (e: Exception) {
                Log.e(TAG, "logout: ${e.message}")
                errorMessage = errorMessage + "\n" + e.message
            } finally {
                errorMessage?.let {
                    Log.e(TAG, "logout: $errorMessage")
                }
                Log.d(TAG, "Logout: finally was called")
                setValue(null)
            }
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