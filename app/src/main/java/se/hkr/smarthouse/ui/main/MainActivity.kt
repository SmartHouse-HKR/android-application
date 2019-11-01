package se.hkr.smarthouse.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.tool_bar
import se.hkr.smarthouse.R
import se.hkr.smarthouse.ui.BaseActivity
import se.hkr.smarthouse.ui.auth.AuthActivity

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "Started Main Activity")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tool_bar.setOnClickListener {
            // Temporary
            sessionManager.logout()
        }
        subscribeObservers()
    }

    private fun subscribeObservers() {
        sessionManager.cachedCredentials.observe(this, Observer { accountCredentials ->
            if (accountCredentials == null
                || accountCredentials.pk == -1
            ) {
                navAuthActivity()
            }
        })
    }

    private fun navAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }
}