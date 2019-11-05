package se.hkr.smarthouse.di.main

import dagger.Module
import dagger.android.ContributesAndroidInjector
import se.hkr.smarthouse.ui.main.HouseFragment

@Module
abstract class MainFragmentsBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeHouseFragment(): HouseFragment
}