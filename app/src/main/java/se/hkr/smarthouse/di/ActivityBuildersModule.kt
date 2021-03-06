package se.hkr.smarthouse.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import se.hkr.smarthouse.di.auth.AuthFragmentsBuildersModule
import se.hkr.smarthouse.di.auth.AuthModule
import se.hkr.smarthouse.di.auth.AuthScope
import se.hkr.smarthouse.di.auth.AuthViewModelModule
import se.hkr.smarthouse.di.main.MainFragmentsBuildersModule
import se.hkr.smarthouse.di.main.MainModule
import se.hkr.smarthouse.di.main.MainScope
import se.hkr.smarthouse.di.main.MainViewModelModule
import se.hkr.smarthouse.ui.auth.AuthActivity
import se.hkr.smarthouse.ui.main.MainActivity

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

    // Creating the MainScope
    @MainScope
    @ContributesAndroidInjector(
        modules = [
            MainModule::class,
            MainFragmentsBuildersModule::class,
            MainViewModelModule::class
        ]
    )
    abstract fun contributeMainActivity(): MainActivity
}