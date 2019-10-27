package se.hkr.smarthouse.ui.auth

import android.os.Bundle
import se.hkr.smarthouse.R
import se.hkr.smarthouse.ui.BaseActivity

class AuthActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }
}