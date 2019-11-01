package se.hkr.smarthouse.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import se.hkr.smarthouse.R
import se.hkr.smarthouse.ui.BaseActivity
import se.hkr.smarthouse.ui.main.MainActivity
import se.hkr.smarthouse.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

class AuthActivity : BaseActivity() {
    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        viewModel = ViewModelProvider(this, providerFactory).get((AuthViewModel::class.java))
        subscribeObservers()
    }

    private fun subscribeObservers() {
        sessionManager.cachedCredentials.observe(this, Observer { accountCredentials ->
            if (accountCredentials != null
                && accountCredentials.pk != -1
            ) {
                navMainActivity()
            }
        })

        viewModel.viewState.observe(this, Observer { authViewState ->
            authViewState.accountCredentials?.let { authToken ->
                sessionManager.login(authToken)
            }
        })
    }

    private fun navMainActivity() {
        Log.d(TAG, "Navigating to Main Activity")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
