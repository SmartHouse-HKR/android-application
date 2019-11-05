package se.hkr.smarthouse.di.main

import dagger.Module
import dagger.Provides
import se.hkr.smarthouse.persistence.AccountCredentialsDao
import se.hkr.smarthouse.repository.main.MainRepository
import se.hkr.smarthouse.session.SessionManager

@Module
class MainModule {
    @MainScope
    @Provides
    fun provideMainRepository(
        sessionManager: SessionManager,
        accountCredentialsDao: AccountCredentialsDao
    ): MainRepository {
        return MainRepository(
            sessionManager = sessionManager,
            accountCredentialsDao = accountCredentialsDao
        )
    }
}