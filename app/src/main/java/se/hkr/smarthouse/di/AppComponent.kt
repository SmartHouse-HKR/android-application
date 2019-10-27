package se.hkr.androidjetpack.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import se.hkr.smarthouse.BaseApplication
import se.hkr.smarthouse.di.ActivityBuildersModule
import se.hkr.smarthouse.di.ViewModelFactoryModule
import se.hkr.smarthouse.session.SessionManager
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class,
        ActivityBuildersModule::class,
        ViewModelFactoryModule::class
    ]
)
interface AppComponent : AndroidInjector<BaseApplication> {
    // Instantiated here so it can be injected to Abstract classes
    val sessionManager: SessionManager

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}