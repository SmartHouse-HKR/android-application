package se.hkr.smarthouse.di.auth

import dagger.Module
import dagger.Provides
import se.hkr.smarthouse.persistence.AccountCredentialsDao
import se.hkr.smarthouse.repository.auth.AuthRepository
import se.hkr.smarthouse.session.SessionManager

@Module
class AuthModule {
    @AuthScope
    @Provides
    fun provideAuthRepository(
        sessionManager: SessionManager,
        accountCredentialsDao: AccountCredentialsDao
    ): AuthRepository {
        return AuthRepository(
            sessionManager = sessionManager,
            accountCredentialsDao = accountCredentialsDao
        )
    }
}