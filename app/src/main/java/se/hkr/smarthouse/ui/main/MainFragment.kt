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
import se.hkr.smarthouse.ui.main.state.MainStateEvent
import se.hkr.smarthouse.util.TopSpacingItemDecoration

class MainFragment : BaseMainFragment(), DeviceListAdapter.Interaction {
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
                    Log.d(TAG, "MainFragment: updating device with: $devicesList")
                    recyclerAdapter.apply {
                        submitList(devicesList)
                    }
                }
            }
        })
    }

    private fun initializeRecyclerView() {
        recyclerAdapter = DeviceListAdapter(this@MainFragment)
        val topSpacingDecoration = TopSpacingItemDecoration(topPadding = 30)
        mainFragmentRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainFragment.context)
            adapter = recyclerAdapter
            addItemDecoration(topSpacingDecoration)
        }
    }

    private fun publishTopic(
        topic: String,
        message: String
    ) {
        Log.d(TAG, "Will publish on topic: $topic the message: $message")
        viewModel.setStateEvent(
            MainStateEvent.PublishAttemptEvent(
                topic,
                message
            )
        )
    }

    override fun onDeviceStateChanged(topic: String, message: String) {
        Log.d(TAG, "Topic: $topic, sending message $message")
        publishTopic(topic, message)
    }
}
