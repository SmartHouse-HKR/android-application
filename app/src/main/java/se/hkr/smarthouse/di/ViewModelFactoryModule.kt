package se.hkr.smarthouse.di

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import se.hkr.smarthouse.viewmodels.ViewModelProviderFactory

@Module
abstract class ViewModelFactoryModule {
    @Binds
    abstract fun bindViewModelFactory(
        factory: ViewModelProviderFactory
    ): ViewModelProvider.Factory
}