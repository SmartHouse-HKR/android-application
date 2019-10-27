package se.hkr.smarthouse.ui.auth

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerFragment
import se.hkr.smarthouse.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

abstract class BaseAuthFragment : DaggerFragment() {
    companion object {
        const val TAG: String = "AppDebug"
    }

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
    lateinit var viewModel: AuthViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.let { context ->
            ViewModelProvider(context, providerFactory).get(AuthViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }
}