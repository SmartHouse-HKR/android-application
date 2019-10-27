package se.hkr.smarthouse.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import se.hkr.smarthouse.di.auth.AuthFragmentsBuildersModule
import se.hkr.smarthouse.di.auth.AuthModule
import se.hkr.smarthouse.di.auth.AuthScope
import se.hkr.smarthouse.di.auth.AuthViewModelModule
import se.hkr.smarthouse.ui.auth.AuthActivity

@Module
abstract class ActivityBuildersModule {
    // Creating the AuthScope
    @AuthScope
    @ContributesAndroidInjector(
        modules = [
            AuthModule::class,
            AuthFragmentsBuildersModule::class,
            AuthViewModelModule::class
        ]
    )
    abstract fun contributeAuthActivity(): AuthActivity
}