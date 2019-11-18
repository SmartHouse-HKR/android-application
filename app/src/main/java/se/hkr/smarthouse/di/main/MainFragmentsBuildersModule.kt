package se.hkr.smarthouse.di.main

import dagger.Module
import dagger.android.ContributesAndroidInjector
import se.hkr.smarthouse.ui.main.MainFragment

@Module
abstract class MainFragmentsBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeHouseFragment(): MainFragment
}