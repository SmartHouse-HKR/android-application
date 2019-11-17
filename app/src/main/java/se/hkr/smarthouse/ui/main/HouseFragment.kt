package se.hkr.smarthouse.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_house.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import se.hkr.smarthouse.R
import se.hkr.smarthouse.ui.main.state.MainStateEvent

class HouseFragment : BaseMainFragment() {
    private val switchStateListener: CompoundButton.OnCheckedChangeListener =
        CompoundButton.OnCheckedChangeListener { _, isChecked ->
            publishTopic(isChecked)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_house, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        state_switch.setOnCheckedChangeListener(switchStateListener)
        subscribe_button.setOnClickListener {
            subscribeTopic()
        }
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { mainViewState ->
            try {
                Log.d(TAG, "HouseFragment: new viewState $mainViewState")
                mainViewState.lampState?.let { lampState ->
                    GlobalScope.launch(Main) {
                        state_switch.setOnCheckedChangeListener(null)
                        state_switch.isChecked = lampState.state
                        // TODO test if the removal/adding of the listener works as intended.
                        state_switch.setOnCheckedChangeListener(switchStateListener)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception on viewState observing on fragment", e)
            }
        })
    }

    private fun publishTopic(state: Boolean) {
        Log.d(TAG, "flipped to $state")
        viewModel.setStateEvent(
            MainStateEvent.PublishAttemptEvent(
                topic = input_topic.text.toString(),
                message = if (state) "true" else "false",
                qos = 2 // TODO add variable for QOS
            )
        )
    }

    private fun subscribeTopic() {
        // TODO do this with event, for now just hard code it
        //viewModel.setStateEvent(
        //    MainStateEvent.SubscribeAttemptEvent(
        //        input_topic.text.toString()
        //    )
        //)
        viewModel.subscribeTo(topic = input_topic.text.toString())
    }
}
