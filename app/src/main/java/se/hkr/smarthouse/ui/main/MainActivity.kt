package se.hkr.smarthouse.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.progress_bar
import kotlinx.android.synthetic.main.activity_main.tool_bar
import se.hkr.smarthouse.R
import se.hkr.smarthouse.ui.BaseActivity
import se.hkr.smarthouse.ui.auth.AuthActivity
import se.hkr.smarthouse.util.setVisibility

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tool_bar.setOnClickListener {
            // Temporary
            sessionManager.logout()
        }
        subscribeObservers()
    }

    private fun subscribeObservers() {
        sessionManager.cachedAccountCredentials.observe(this, Observer { accountCredentials ->
            Log.d(TAG, "MainActivity: Account credentials observed change: $accountCredentials")
            if (accountCredentials == null
                || accountCredentials.account_pk == -1
                || accountCredentials.hostUrl == null
            ) {
                navAuthActivity()
            }
        })
    }

    private fun navAuthActivity() {
        Log.d(TAG, "MainActivity: navigating back to AuthActivity")
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun displayProgressBar(boolean: Boolean) {
        progress_bar.setVisibility(boolean)
    }
}