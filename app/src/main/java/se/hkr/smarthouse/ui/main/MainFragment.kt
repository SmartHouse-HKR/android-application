package se.hkr.smarthouse.ui.main

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.RadioButton
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.layout_device_filter.view.*
import se.hkr.smarthouse.R
import se.hkr.smarthouse.mqtt.MqttConnection
import se.hkr.smarthouse.ui.main.state.MainStateEvent
import se.hkr.smarthouse.util.Filters
import se.hkr.smarthouse.util.TopSpacingItemDecoration
import se.hkr.smarthouse.util.displayToast

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
        setHasOptionsMenu(true)
        subscribeObservers()
        initializeRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.filter_menu, menu)
        inflater.inflate(R.menu.device_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_filter -> {
                showFilterOptions()
                return true
            }
            R.id.menu_devices -> {
                populateDevices()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { mainViewState ->
            Log.d(TAG, "MainFragment: new viewState $mainViewState")
            mainViewState.deviceFields?.let { devicesState ->
                devicesState.deviceList?.let { devicesList ->
                    Log.d(TAG, "MainFragment: updating device with: $devicesList")
                    recyclerAdapter.apply {
                        submitList(devicesList, devicesState.filter ?: Filters.any)
                    }
                }
            }
        })
    }

    private fun initializeRecyclerView() {
        recyclerAdapter = DeviceListAdapter(this@MainFragment)
        val topSpacingDecoration =
            TopSpacingItemDecoration(25, 25, 70, 70)
        mainFragmentRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainFragment.context)
            adapter = recyclerAdapter
            addItemDecoration(topSpacingDecoration)
        }
    }

    private fun showFilterOptions() {
        activity?.let {
            val dialog = MaterialDialog(it)
                .noAutoDismiss()
                .customView(viewRes = R.layout.layout_device_filter)
            dialog.getCustomView().apply {
                when (viewModel.getFilter()) {
                    Filters.regexLights -> filter_group.check(R.id.filter_lights)
                    Filters.regexHeaters -> filter_group.check(R.id.filter_heaters)
                    Filters.regexFans -> filter_group.check(R.id.filter_fans)
                    Filters.regexAlarms -> filter_group.check(R.id.filter_alarms)
                    Filters.regexBluetooth -> filter_group.check(R.id.filter_bluetooth)
                    Filters.regexEtc -> filter_group.check(R.id.filter_etc)
                }
            }
            dialog.show {
                title(R.string.filter_options)
                negativeButton(R.string.cancel) { dismiss() }
                neutralButton(R.string.clear_filters) {
                    viewModel.setDeviceFilter(Filters.any)
                    dismiss()
                }
                positiveButton(R.string.apply) {
                    val selectedFilter =
                        view.findViewById<RadioButton>(view.filter_group.checkedRadioButtonId)
                    if (selectedFilter == null) {
                        context.displayToast(
                            "You must chose a filter, clear filters or cancel!", true
                        )
                    } else {
                        val filter = when (selectedFilter.text) {
                            getString(R.string.filter_lights) -> Filters.regexLights
                            getString(R.string.filter_heaters) -> Filters.regexHeaters
                            getString(R.string.filter_fans) -> Filters.regexFans
                            getString(R.string.filter_alarms) -> Filters.regexAlarms
                            getString(R.string.filter_bluetooth) -> Filters.regexBluetooth
                            getString(R.string.filter_etc) -> Filters.regexEtc
                            else -> Filters.any
                        }
                        viewModel.setDeviceFilter(filter)
                        dismiss()
                    }
                }
            }
        }
    }

    private fun populateDevices() {
        // Just put all the devices on the broker, this should not be done by the phone anyway.
        activity?.let {
            MaterialDialog(it).show {
                icon(R.drawable.ic_warning_accent_24dp)
                title(text = "Populate all devices")
                message(text = "This may take some seconds, the screen will freeze!")
                positiveButton(text = "OK") {
                    MqttConnection.publish("smarthouse/indoor_light/state", "on")
                    MqttConnection.publish("smarthouse/outdoor_light/state", "on")
                    MqttConnection.publish("smarthouse/outdoor_light/trigger", "true")
                    MqttConnection.publish("smarthouse/heater_1/state", "on")
                    MqttConnection.publish("smarthouse/heater_2/state", "on")
                    MqttConnection.publish("smarthouse/heater_1/value", "23")
                    MqttConnection.publish("smarthouse/heater_2/value", "24")
                    MqttConnection.publish("smarthouse/fan/speed", "50")
                    MqttConnection.publish("smarthouse/outdoor_temperature/value", "15")
                    MqttConnection.publish("smarthouse/indoor_temperature/value", "25")
                    MqttConnection.publish("smarthouse/voltage/value", "4.40")
                    MqttConnection.publish("smarthouse/voltage/overview", "Information")
                    MqttConnection.publish("smarthouse/power/state", "on")
                    MqttConnection.publish("smarthouse/burglar_alarm/state", "on")
                    MqttConnection.publish("smarthouse/burglar_alarm/trigger", "false")
                    MqttConnection.publish("smarthouse/fire_alarm/state", "on")
                    MqttConnection.publish("smarthouse/fire_alarm/trigger", "false")
                    MqttConnection.publish("smarthouse/water_leak/trigger", "false")
                    MqttConnection.publish("smarthouse/oven/state", "off")
                    MqttConnection.publish("smarthouse/window_alarm/state", "on")
                    MqttConnection.publish("smarthouse/window_alarm/trigger", "false")
                    MqttConnection.publish("smarthouse/microwave/manual_start", "x0500t0010")
                    MqttConnection.publish("smarthouse/microwave/preset_start", "fish")
                    MqttConnection.publish("smarthouse/microwave/error", "no error")
                    MqttConnection.publish("smarthouse/bt_fan/state", "yes")
                    MqttConnection.publish("smarthouse/bt_fan/swing", "false")
                    MqttConnection.publish("smarthouse/bt_fan/speed", "higher")
                    MqttConnection.publish("smarthouse/bt_light/state", "on")
                    MqttConnection.publish("smarthouse/bt_lamp/state", "on")
                }
                negativeButton(text = "CANCEL")
            }
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
