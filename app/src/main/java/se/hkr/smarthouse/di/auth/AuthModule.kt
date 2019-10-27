package se.hkr.smarthouse.di.auth

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import se.hkr.smarthouse.api.auth.SmarthouseAuthService
import se.hkr.smarthouse.persistence.AccountCredentialsDao
import se.hkr.smarthouse.repository.auth.AuthRepository
import se.hkr.smarthouse.session.SessionManager

@Module
class AuthModule {
    @AuthScope
    @Provides
    fun provideFakeApiService(): SmarthouseAuthService {
        return Retrofit
            .Builder()
            .baseUrl("https://open-api.xyz")
            .build()
            .create(SmarthouseAuthService::class.java)
    }

    @AuthScope
    @Provides
    fun provideAuthRepository(
        sessionManager: SessionManager,
        accountCredentialsDao: AccountCredentialsDao,
        smarthouseAuthService: SmarthouseAuthService
    ): AuthRepository {
        return AuthRepository(
            sessionManager = sessionManager,
            accountCredentialsDao = accountCredentialsDao,
            smarthouseAuthService = smarthouseAuthService
        )
    }
}