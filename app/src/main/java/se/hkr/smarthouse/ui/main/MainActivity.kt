package se.hkr.smarthouse.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*
import se.hkr.smarthouse.R
import se.hkr.smarthouse.mqtt.MqttConnection
import se.hkr.smarthouse.ui.BaseActivity
import se.hkr.smarthouse.ui.auth.AuthActivity
import se.hkr.smarthouse.util.setVisibility
import se.hkr.smarthouse.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

class MainActivity : BaseActivity() {
    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this, providerFactory).get(MainViewModel::class.java)
        tool_bar.setOnClickListener {
            // Temporary
            sessionManager.logout()
        }
        subscribeObservers()
        // TODO use nav for it instead of this simple inflation
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                HouseFragment(),
                "HouseFragment"
            ).commit()
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(this, Observer { dataState ->
            Log.d(TAG, "MainActivity: dataState changed: $dataState")
            onDataStateChange(dataState)
            dataState.data?.let { data ->
                data.data?.let { dataEvent ->
                    dataEvent.getContentIfNotHandled()?.let { eventContent ->
                        eventContent.publishFields?.let { publishFields ->
                            Log.d(TAG, "MainActivity: new publishFields: $publishFields")
                            viewModel.setPublishFields(publishFields)
                        }
                    }
                }
            }
        })
        viewModel.viewState.observe(this, Observer { authViewState ->
            Log.d(TAG, "MainActivity: viewState changed to: $authViewState")
            authViewState.publishFields?.let { publishFields ->
                publishFields.topic?.let {
                    MqttConnection.publish(
                        // TODO not just !!, check how to fix this properly
                        publishFields.topic!!,
                        publishFields.message!!,
                        publishFields.qos
                    )
                }
            }
        })
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