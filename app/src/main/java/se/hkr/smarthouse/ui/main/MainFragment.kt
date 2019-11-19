package se.hkr.smarthouse.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_main.*
import se.hkr.smarthouse.R
import se.hkr.smarthouse.mqtt.Topics
import se.hkr.smarthouse.ui.main.state.MainStateEvent
import se.hkr.smarthouse.util.TopSpacingItemDecoration

class MainFragment : BaseMainFragment() {
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
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { mainViewState ->
            Log.d(TAG, "MainFragment: new viewState $mainViewState")
            mainViewState.deviceFields?.let { devicesState ->
                devicesState.deviceList?.let { devicesList ->
                    Log.d(TAG, "MainFragment: updating list with: $devicesList")
                    recyclerAdapter.apply {
                        submitList(devicesList)
                    }
                }
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
        // Initialize list with all possible hardcoded topics. Potentially make this dynamic later.
        viewModel.setStateEvent(
            MainStateEvent.UpdateDeviceListEvent(
                list = Topics.allTopicsList
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
