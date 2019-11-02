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
            onDataStateChange(dataState)
            dataState.data?.let { data ->
                data.data?.let { dataEvent ->
                    dataEvent.getContentIfNotHandled()?.let { eventContent ->
                        eventContent.authToken?.let { authToken ->
                            Log.d(TAG, "AuthActivity: DataState: $eventContent")
                            viewModel.setAuthToken(authToken)
                        }
                    }
                }
            }
        })
        viewModel.viewState.observe(this, Observer { authViewState ->
            authViewState.authToken?.let { authToken ->
                sessionManager.login(authToken)
            }
        })
        sessionManager.cachedToken.observe(this, Observer { authToken ->
            if (authToken != null
                && authToken.account_pk != -1
                && authToken.token != null
            ) {
                Log.d(TAG, "Navigating to main because: ${authToken}")
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
