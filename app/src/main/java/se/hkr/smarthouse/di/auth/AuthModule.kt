package se.hkr.smarthouse.di.auth

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import se.hkr.smarthouse.api.auth.OpenApiAuthService
import se.hkr.smarthouse.persistence.AccountPropertiesDao
import se.hkr.smarthouse.persistence.AuthTokenDao
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
        authTokenDao: AuthTokenDao,
        accountPropertiesDao: AccountPropertiesDao,
        openApiAuthService: OpenApiAuthService
    ): AuthRepository {
        return AuthRepository(
            sessionManager = sessionManager,
            authTokenDao = authTokenDao,
            accountPropertiesDao = accountPropertiesDao,
            openApiAuthService = openApiAuthService
        )
    }
}