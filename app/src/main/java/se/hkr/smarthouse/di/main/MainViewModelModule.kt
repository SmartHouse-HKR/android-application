package se.hkr.smarthouse.di.main

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import se.hkr.smarthouse.di.ViewModelKey
import se.hkr.smarthouse.ui.main.MainViewModel

@Module
abstract class MainViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindAuthViewModel(mainViewModel: MainViewModel): ViewModel
}