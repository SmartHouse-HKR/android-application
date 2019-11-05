package se.hkr.smarthouse.ui.auth.state

sealed class AuthStateEvent {
    data class LoginAttemptEvent(
        val username: String,
        val password: String,
        val hostUrl: String
    ) : AuthStateEvent()
}