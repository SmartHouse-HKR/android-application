package se.hkr.smarthouse.ui.auth

import androidx.lifecycle.ViewModel
import se.hkr.smarthouse.repository.auth.AuthRepository
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
    val authRepository: AuthRepository
) : ViewModel()