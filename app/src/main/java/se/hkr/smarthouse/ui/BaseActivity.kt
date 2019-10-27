package se.hkr.smarthouse.ui

import dagger.android.support.DaggerAppCompatActivity

abstract class BaseActivity : DaggerAppCompatActivity() {
    val TAG: String = "AppDebug"
}