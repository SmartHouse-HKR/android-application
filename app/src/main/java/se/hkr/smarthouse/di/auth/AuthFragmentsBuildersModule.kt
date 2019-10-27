package se.hkr.smarthouse.di.auth

import dagger.Module
import dagger.android.ContributesAndroidInjector
import se.hkr.smarthouse.ui.auth.ForgotPasswordFragment
import se.hkr.smarthouse.ui.auth.LauncherFragment
import se.hkr.smarthouse.ui.auth.LoginFragment
import se.hkr.smarthouse.ui.auth.RegisterFragment

@Module
abstract class AuthFragmentsBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeLauncherFragment(): LauncherFragment

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    abstract fun contributeRegisterFragment(): RegisterFragment

    @ContributesAndroidInjector
    abstract fun contributeForgotPasswordFragment(): ForgotPasswordFragment
}