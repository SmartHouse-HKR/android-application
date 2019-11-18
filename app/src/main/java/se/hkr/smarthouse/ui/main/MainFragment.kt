package se.hkr.smarthouse.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import se.hkr.smarthouse.R
import se.hkr.smarthouse.models.*
import se.hkr.smarthouse.ui.main.state.MainStateEvent
import se.hkr.smarthouse.util.TopSpacingItemDecoration

class MainFragment : BaseMainFragment() {
    private val switchStateListener: CompoundButton.OnCheckedChangeListener =
        CompoundButton.OnCheckedChangeListener { _, isChecked ->
            publishTopic(isChecked)
        }
    private lateinit var recyclerAdapter: DeviceListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        initializeRecyclerView()
        state_switch.setOnCheckedChangeListener(switchStateListener)
        subscribe_button.setOnClickListener {
            subscribeTopic()
        }
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { mainViewState ->
            try {
                Log.d(TAG, "MainFragment: new viewState $mainViewState")
                mainViewState.lampState?.let { lampState ->
                    GlobalScope.launch(Main) {
                        state_switch.setOnCheckedChangeListener(null)
                        state_switch.isChecked = lampState.state
                        state_switch.setOnCheckedChangeListener(switchStateListener)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception on viewState observing on fragment", e)
            }
        })
    }

    private fun initializeRecyclerView() {
        recyclerAdapter = DeviceListAdapter()
        val topSpacingDecoration = TopSpacingItemDecoration(topPadding = 30)
        mainFragmentRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainFragment.context)
            adapter = recyclerAdapter
            addItemDecoration(topSpacingDecoration)
        }
        //Temporary
        recyclerAdapter.submitList(
            listOf(
                InteractiveOnOff("livingRoomBluetoothCeilingLight"),
                ObservableOnOff("outsideTwilightSensor"),
                InteractiveTemperature("livingRoomHeater"),
                ObservableTemperature("outsideTempSensor"),
                InteractiveRgb("rgbLight", 20, 40, 50)
            )
        )
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
