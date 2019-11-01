package se.hkr.smarthouse.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_login.input_email
import kotlinx.android.synthetic.main.fragment_login.input_password
import kotlinx.android.synthetic.main.fragment_login.login_button
import se.hkr.smarthouse.R
import se.hkr.smarthouse.models.AccountCredentials
import se.hkr.smarthouse.ui.auth.state.LoginFields

class LoginFragment : BaseAuthFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "LoginFragment: ${viewModel.hashCode()}")
        subscribeObservers()
        login_button.setOnClickListener {
            Log.d(TAG, "Login button pressed")
            // Temporary test
            viewModel.setAccountCredentials(
                AccountCredentials(
                    pk = 1,
                    email = "asdf",
                    password = "123"
                )
            )
        }
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { authViewState ->
            authViewState.loginFields?.let { loginFields ->
                loginFields.login_email?.let { emailText ->
                    input_email.setText(emailText)
                }
                loginFields.login_password?.let { passwordText ->
                    input_password.setText(passwordText)
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Update ViewModel on fragment destroy to remember fields
        viewModel.setLoginFields(
            LoginFields(
                login_email = input_email.text.toString(),
                login_password = input_password.text.toString()
            )
        )
    }
}
