package se.hkr.smarthouse.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_launcher.focusable_view
import kotlinx.android.synthetic.main.fragment_launcher.forgot_password
import kotlinx.android.synthetic.main.fragment_launcher.login
import kotlinx.android.synthetic.main.fragment_launcher.register
import se.hkr.smarthouse.R

class LauncherFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_launcher, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        login.setOnClickListener {
            navLogin()
        }
        register.setOnClickListener {
            navRegistration()
        }
        forgot_password.setOnClickListener {
            navForgotPassword()
        }
        // Request focus to avoid bug with something else getting the focus
        focusable_view.requestFocus()
    }

    private fun navLogin() {
        findNavController().navigate(R.id.action_launcherFragment_to_loginFragment)
    }

    private fun navRegistration() {
        findNavController().navigate(R.id.action_launcherFragment_to_registerFragment)
    }

    private fun navForgotPassword() {
        findNavController().navigate(R.id.action_launcherFragment_to_forgotPasswordFragment)
    }
}
