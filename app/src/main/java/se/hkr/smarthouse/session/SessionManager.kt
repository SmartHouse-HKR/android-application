package se.hkr.smarthouse.session

import android.app.Application
import se.hkr.smarthouse.persistence.LoginCredentialsDao

class SessionManager
constructor(
    val loginCredentials: LoginCredentialsDao,
    val application: Application
) {
}