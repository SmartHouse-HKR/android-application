package se.hkr.smarthouse.session

import android.app.Application
import se.hkr.smarthouse.persistence.AccountCredentialsDao
import javax.inject.Inject

class SessionManager
@Inject
constructor(
    val accountCredentials: AccountCredentialsDao,
    val application: Application
)