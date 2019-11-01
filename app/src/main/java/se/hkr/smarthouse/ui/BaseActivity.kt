package se.hkr.smarthouse.ui

import dagger.android.support.DaggerAppCompatActivity
import se.hkr.smarthouse.session.SessionManager
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity() {
    val TAG: String = "AppDebug"
    @Inject
    lateinit var sessionManager: SessionManager
}