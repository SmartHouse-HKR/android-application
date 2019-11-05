package se.hkr.smarthouse.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import kotlinx.android.synthetic.main.activity_auth.progress_bar
import se.hkr.smarthouse.R
import se.hkr.smarthouse.ui.BaseActivity
import se.hkr.smarthouse.ui.auth.state.LoginFields
import se.hkr.smarthouse.ui.main.MainActivity
import se.hkr.smarthouse.util.displayToast
import se.hkr.smarthouse.util.setVisibility
import se.hkr.smarthouse.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

class AuthActivity : BaseActivity(), NavController.OnDestinationChangedListener {
    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        viewModel = ViewModelProvider(this, providerFactory).get((AuthViewModel::class.java))
        subscribeObservers()
        // For fast testing
        viewModel.setLoginFields(LoginFields("fast@fast.se", "fast"))
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(this, Observer { dataState ->
            Log.d(TAG, "AuthActivity: dataState changed: $dataState")
            onDataStateChange(dataState)
            dataState.data?.let { data ->
                data.data?.let { dataEvent ->
                    dataEvent.getContentIfNotHandled()?.let { eventContent ->
                        eventContent.accountCredentials?.let { accountCredentials ->
                            Log.d(TAG, "AuthActivity: new accountCredentials: $accountCredentials")
                            viewModel.setAccountCredentials(accountCredentials)
                        }
                    }
                }
            }
        })
        viewModel.viewState.observe(this, Observer { authViewState ->
            Log.d(TAG, "AuthActivity: viewState changed to: $authViewState")
            authViewState.accountCredentials?.let { accountCredentials ->
                sessionManager.login(accountCredentials)
            }
        })
        sessionManager.cachedAccountCredentials.observe(this, Observer { accountCredentials ->
            Log.d(TAG, "AuthActivity: cached acc cred $accountCredentials")
            if (accountCredentials != null
                && accountCredentials.account_pk != -1
                && accountCredentials.hostUrl != null
            ) {
                displayToast("Connected to: ${accountCredentials.hostUrl}")
                Log.d(TAG, "Navigating to main because: $accountCredentials")
                navMainActivity()
            }
        })
    }

    private fun navMainActivity() {
        Log.d(TAG, "Navigating to Main Activity")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun displayProgressBar(boolean: Boolean) {
        progress_bar.setVisibility(boolean)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        viewModel.cancelActiveJobs()
    }
}
