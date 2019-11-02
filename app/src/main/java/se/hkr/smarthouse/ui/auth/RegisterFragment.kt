package se.hkr.smarthouse.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_register.input_email
import kotlinx.android.synthetic.main.fragment_register.input_password
import kotlinx.android.synthetic.main.fragment_register.input_password_confirm
import kotlinx.android.synthetic.main.fragment_register.input_username
import kotlinx.android.synthetic.main.fragment_register.register_button
import se.hkr.smarthouse.R
import se.hkr.smarthouse.ui.auth.state.AuthStateEvent
import se.hkr.smarthouse.ui.auth.state.RegistrationFields

class RegisterFragment : BaseAuthFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        register_button.setOnClickListener {
            register()
        }
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { authViewState ->
            authViewState.registrationFields?.let { registrationFields ->
                registrationFields.registration_email?.let { emailText ->
                    input_email.setText(emailText)
                }
                registrationFields.registration_username?.let { usernameText ->
                    input_username.setText(usernameText)
                }
                registrationFields.registration_password?.let { passwordText ->
                    input_password.setText(passwordText)
                }
                registrationFields.registration_confirm_password?.let { confirmPasswordText ->
                    input_password_confirm.setText(confirmPasswordText)
                }
            }
        })
    }

    private fun register() {
        viewModel.setStateEvent(
            AuthStateEvent.RegisterAttemptEvent(
                input_email.text.toString(),
                input_username.text.toString(),
                input_password.text.toString(),
                input_password_confirm.text.toString()
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setRegistrationFields(
            RegistrationFields(
                input_email.text.toString(),
                input_username.text.toString(),
                input_password.text.toString(),
                input_password_confirm.text.toString()
            )
        )
    }
}

