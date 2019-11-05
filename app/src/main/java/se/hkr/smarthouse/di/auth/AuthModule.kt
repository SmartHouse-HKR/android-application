package se.hkr.smarthouse.di.auth

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import se.hkr.smarthouse.api.auth.OpenApiAuthService
import se.hkr.smarthouse.persistence.AccountCredentialsDao
import se.hkr.smarthouse.repository.auth.AuthRepository
import se.hkr.smarthouse.session.SessionManager

@Module
class AuthModule {
    @AuthScope
    @Provides
    fun provideFakeApiService(retrofitBuilder: Retrofit.Builder): OpenApiAuthService {
        return retrofitBuilder
            .build()
            .create(OpenApiAuthService::class.java)
    }

    @AuthScope
    @Provides
    fun provideAuthRepository(
        sessionManager: SessionManager,
        accountCredentialsDao: AccountCredentialsDao,
        openApiAuthService: OpenApiAuthService
    ): AuthRepository {
        return AuthRepository(
            sessionManager = sessionManager,
            accountCredentialsDao = accountCredentialsDao,
            openApiAuthService = openApiAuthService
        )
    }
}