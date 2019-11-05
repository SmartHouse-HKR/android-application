package se.hkr.smarthouse.ui.auth.state

import se.hkr.smarthouse.models.AccountCredentials

data class AuthViewState(
    var loginFields: LoginFields? = LoginFields(),
    var accountCredentials: AccountCredentials? = null
)

data class LoginFields(
    var login_username: String? = null,
    var login_password: String? = null,
    var login_host_url: String? = null
) {
    class LoginError {
        companion object {
            fun mustFillAllFields(): String {
                return "You can't login without an username and password."
            }

            fun none(): String {
                return "None."
            }
        }
    }

    fun isValidForLogin(): String {
        if (login_username.isNullOrEmpty()
            || login_password.isNullOrEmpty()
            || login_host_url.isNullOrEmpty()
        ) {
            return LoginError.mustFillAllFields()
        }
        return LoginError.none()
    }

    override fun toString(): String {
        return "LoginFields(login_username=$login_username, login_password=$login_password, login_host_url=$login_host_url)"
    }
}