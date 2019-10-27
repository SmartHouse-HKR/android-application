package se.hkr.smarthouse.repository.auth

import se.hkr.smarthouse.api.auth.SmarthouseAuthService
import se.hkr.smarthouse.persistence.AccountCredentialsDao
import se.hkr.smarthouse.session.SessionManager
import javax.inject.Inject

class AuthRepository
@Inject
constructor(
    val accountCredentialsDao: AccountCredentialsDao,
    val smarthouseAuthService: SmarthouseAuthService,
    val sessionManager: SessionManager
)