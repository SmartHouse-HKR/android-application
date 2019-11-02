package se.hkr.smarthouse

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import se.hkr.smarthouse.di.DaggerAppComponent

class BaseApplication : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication>/*?*/ {
        return DaggerAppComponent.builder().application(this).build()
        // return null // And add ? on the return type if something goes wrong to see stacktrace
    }
}