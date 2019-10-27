package se.hkr.smarthouse.di.auth

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import se.hkr.smarthouse.di.ViewModelKey
import se.hkr.smarthouse.ui.auth.AuthViewModel

@Module
abstract class AuthViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(authViewModel: AuthViewModel): ViewModel
}