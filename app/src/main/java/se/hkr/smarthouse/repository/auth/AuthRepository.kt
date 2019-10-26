package se.hkr.smarthouse.repository.auth

import se.hkr.smarthouse.api.auth.SmarthouseAuthService
import se.hkr.smarthouse.persistence.LoginCredentialsDao
import se.hkr.smarthouse.session.SessionManager

class AuthRepository
constructor(
    val loginCredentialsDao: LoginCredentialsDao,
    val smarthouseAuthService: SmarthouseAuthService,
    val sessionManager: SessionManager
) {
}