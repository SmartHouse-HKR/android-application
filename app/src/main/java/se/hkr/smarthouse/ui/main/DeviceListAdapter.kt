package se.hkr.smarthouse.ui.main

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.triggertrap.seekarc.SeekArc
import kotlinx.android.synthetic.main.device_list_item_alarm.view.*
import kotlinx.android.synthetic.main.device_list_item_bluetooth_fan.view.*
import kotlinx.android.synthetic.main.device_list_item_fan.view.*
import kotlinx.android.synthetic.main.device_list_item_heater.view.*
import kotlinx.android.synthetic.main.device_list_item_light.view.*
import kotlinx.android.synthetic.main.device_list_item_microwave.view.*
import kotlinx.android.synthetic.main.device_list_item_oven.view.*
import kotlinx.android.synthetic.main.device_list_item_temperature.view.*
import kotlinx.android.synthetic.main.device_list_item_trigger.view.*
import kotlinx.android.synthetic.main.layout_time_picker.view.*
import se.hkr.smarthouse.R
import se.hkr.smarthouse.models.Device
import se.hkr.smarthouse.models.getSimpleName
import se.hkr.smarthouse.util.Filters
import se.hkr.smarthouse.util.isEtc
import kotlinx.android.synthetic.main.device_list_item_alarm.view.text_device_name as alarm_text_device_name
import kotlinx.android.synthetic.main.device_list_item_bluetooth_fan.view.text_device_name as bluetooth_fan_text_device_name
import kotlinx.android.synthetic.main.device_list_item_fan.view.text_device_name as fan_text_device_name
import kotlinx.android.synthetic.main.device_list_item_heater.view.text_device_name as heater_text_device_name
import kotlinx.android.synthetic.main.device_list_item_light.view.text_device_name as light_text_device_name
import kotlinx.android.synthetic.main.device_list_item_microwave.view.text_device_name as microwave_text_device_name
import kotlinx.android.synthetic.main.device_list_item_oven.view.text_device_name as oven_text_device_name
import kotlinx.android.synthetic.main.device_list_item_temperature.view.text_device_name as temperature_text_device_name
import kotlinx.android.synthetic.main.device_list_item_trigger.view.text_device_name as trigger_text_device_name
import kotlinx.android.synthetic.main.device_list_item_unknown.view.text_device_name as unknown_text_device_name
import kotlinx.android.synthetic.main.device_list_item_voltage.view.text_device_name as voltage_text_device_name

class DeviceListAdapter(
    private val interaction: Interaction
) : RecyclerView.Adapter<DeviceListAdapter.BaseViewHolder<*>>() {
    companion object {
        const val TAG = "AppDebug"
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Device>() {
        override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
            return oldItem.topic.split("/").take(2) == newItem.topic.split("/").take(2)
        }

        override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
            try {
                return when (oldItem) {
                    is Device.UnknownDevice -> {
                        return true
                    }
                    is Device.Light -> {
                        newItem as Device.Light
                        newItem.state == oldItem.state
                    }
                    is Device.Temperature -> {
                        newItem as Device.Temperature
                        newItem.temperature == oldItem.temperature
                    }
                    is Device.Voltage -> {
                        newItem as Device.Voltage
                        newItem.voltage == oldItem.voltage
                    }
                    is Device.Oven -> {
                        newItem as Device.Oven
                        newItem.state == oldItem.state
                    }
                    is Device.Fan -> {
                        newItem as Device.Fan
                        newItem.speed == oldItem.speed
                    }
                    is Device.Heater -> {
                        newItem as Device.Heater
                        newItem.state == oldItem.state && newItem.value == oldItem.value
                    }
                    is Device.Alarm -> {
                        newItem as Device.Alarm
                        newItem.active == oldItem.active && newItem.triggered == oldItem.triggered
                    }
                    is Device.Trigger -> {
                        newItem as Device.Trigger
                        newItem.triggered == oldItem.triggered
                    }
                    is Device.Microwave -> {
                        newItem as Device.Microwave
                        newItem.error == oldItem.error
                                && newItem.manualStart == oldItem.manualStart
                                && newItem.presetStart == oldItem.presetStart
                    }
                    is Device.BluetoothFan -> {
                        newItem as Device.BluetoothFan
                        newItem.speed == oldItem.speed
                                && newItem.state == oldItem.state
                                && newItem.swing == oldItem.swing
                    }
                }
            } catch (e: Exception) {
                // If typecast failed, means they are different class anyway, therefore different.
                return false
            }
        }
    }

    private val differ = object : AsyncListDiffer<Device>(this, diffCallback) {
        // Have to override because if the device send to submitList is == to the old device, which
        // is true since we are also updating the local variable whenever there is a change. We get
        // around it by sending a copy of the device which does not return true on the == operation
        // and therefore continues on the method and triggers the notifyDataSetChanged()
        override fun submitList(newList: List<Device>?, commitCallback: Runnable?) {
            super.submitList(newList?.toList(), commitCallback)
        }

        override fun submitList(newList: List<Device>?) {
            super.submitList(newList?.toList())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            Device.Light.IDENTIFIER -> {
                LightViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.device_list_item_light, parent, false
                    ), interaction
                )
            }
            Device.Temperature.IDENTIFIER -> {
                TemperatureViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.device_list_item_temperature, parent, false
                    )
                )
            }
            Device.Voltage.IDENTIFIER -> {
                VoltageViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.device_list_item_voltage, parent, false
                    ), interaction
                )
            }
            Device.Oven.IDENTIFIER -> {
                OvenViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.device_list_item_oven, parent, false
                    ), interaction
                )
            }
            Device.Fan.IDENTIFIER -> {
                FanViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.device_list_item_fan, parent, false
                    ), interaction
                )
            }
            Device.Heater.IDENTIFIER -> {
                HeaterViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.device_list_item_heater, parent, false
                    ), interaction
                )
            }
            Device.Alarm.IDENTIFIER -> {
                AlarmViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.device_list_item_alarm, parent, false
                    ), interaction
                )
            }
            Device.Trigger.IDENTIFIER -> {
                TriggerViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.device_list_item_trigger, parent, false
                    )
                )
            }
            Device.Microwave.IDENTIFIER -> {
                MicrowaveViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.device_list_item_microwave, parent, false
                    ), interaction
                )
            }
            Device.BluetoothFan.IDENTIFIER -> {
                BluetoothFanViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.device_list_item_bluetooth_fan, parent, false
                    ), interaction
                )
            }
            else -> {
                UnknownDeviceViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.device_list_item_unknown, parent, false
                    ), interaction
                )
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val device = differ.currentList[position]
        when (holder) {
            is UnknownDeviceViewHolder -> holder.bind(device)
            is LightViewHolder -> holder.bind(device)
            is TemperatureViewHolder -> holder.bind(device)
            is VoltageViewHolder -> holder.bind(device)
            is OvenViewHolder -> holder.bind(device)
            is FanViewHolder -> holder.bind(device)
            is HeaterViewHolder -> holder.bind(device)
            is AlarmViewHolder -> holder.bind(device)
            is TriggerViewHolder -> holder.bind(device)
            is MicrowaveViewHolder -> holder.bind(device)
            is BluetoothFanViewHolder -> holder.bind(device)
            else -> throw IllegalArgumentException("Illegal bind view holder")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (differ.currentList[position]) {
            is Device.UnknownDevice -> Device.UnknownDevice.IDENTIFIER
            is Device.Light -> Device.Light.IDENTIFIER
            is Device.Temperature -> Device.Temperature.IDENTIFIER
            is Device.Voltage -> Device.Voltage.IDENTIFIER
            is Device.Oven -> Device.Oven.IDENTIFIER
            is Device.Fan -> Device.Fan.IDENTIFIER
            is Device.Heater -> Device.Heater.IDENTIFIER
            is Device.Alarm -> Device.Alarm.IDENTIFIER
            is Device.Trigger -> Device.Trigger.IDENTIFIER
            is Device.Microwave -> Device.Microwave.IDENTIFIER
            is Device.BluetoothFan -> Device.BluetoothFan.IDENTIFIER
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Device>, filter: Regex = Filters.any) {
        when (filter) {
            Filters.any -> differ.submitList(list)
            Filters.regexEtc -> differ.submitList(list.filter { it.topic.isEtc() })
            else -> differ.submitList(list.filter { it.topic.matches(filter) })
        }
    }

    inner class UnknownDeviceViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction
    ) : BaseViewHolder<Device>(itemView) {
        override fun bind(item: Device) = with(item as Device.UnknownDevice) {
            Log.d(TAG, "Binding UnknownDevice viewHolder: $item")
            itemView.unknown_text_device_name.text = item.getSimpleName()
        }
    }

    inner class LightViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction
    ) : BaseViewHolder<Device>(itemView) {
        override fun bind(item: Device) = with(item as Device.Light) {
            Log.d(TAG, "Binding Light viewHolder: $item")
            itemView.light_text_device_name.text = item.getSimpleName()
            itemView.switch_light_state.isChecked = item.state
            itemView.switch_light_state.setOnClickListener {
                interaction.onDeviceStateChanged(
                    "${item.topic}/state",
                    if (itemView.switch_light_state.isChecked) "on" else "off"
                )
            }
        }
    }

    inner class TemperatureViewHolder
    constructor(
        itemView: View
    ) : BaseViewHolder<Device>(itemView) {
        override fun bind(item: Device) = with(item as Device.Temperature) {
            Log.d(TAG, "Binding Temperature viewHolder: $item")
            itemView.temperature_text_device_name.text = item.getSimpleName()
            itemView.text_temperature_value.text = item.temperature
        }
    }

    inner class VoltageViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction
    ) : BaseViewHolder<Device>(itemView) {
        override fun bind(item: Device) = with(item as Device.Voltage) {
            Log.d(TAG, "Binding Voltage viewHolder: $item")

            itemView.voltage_text_device_name.text = item.getSimpleName()
            //TODO rest
        }
    }

    inner class OvenViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction
    ) : BaseViewHolder<Device>(itemView) {
        override fun bind(item: Device) = with(item as Device.Oven) {
            Log.d(TAG, "Binding Oven viewHolder: $item")
            itemView.oven_text_device_name.text = item.getSimpleName()
            itemView.switch_oven_state.isChecked = item.state
            itemView.switch_oven_state.setOnClickListener {
                interaction.onDeviceStateChanged(
                    "${item.topic}/state",
                    if (itemView.switch_oven_state.isChecked) "on" else "off"
                )
            }
        }
    }

    inner class FanViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction
    ) : BaseViewHolder<Device>(itemView) {
        override fun bind(item: Device) = with(item as Device.Fan) {
            Log.d(TAG, "Binding Fan viewHolder: $item")
            itemView.fan_text_device_name.text = item.getSimpleName()
            val itemsArray = itemView.context.resources.getStringArray(R.array.fan_speed_array)
            // This assumes that .speed will always be 0/50/75/100, otherwise just shows 0%
            itemView.spinner_fan_speed.adapter = ArrayAdapter<String>(
                itemView.context, R.layout.spinner_item, itemsArray
            )
            itemView.spinner_fan_speed.setSelection(
                itemsArray.indexOfFirst { arrayItem ->
                    arrayItem.removeSuffix("%") == item.speed
                }, false
            )
            itemView.spinner_fan_speed.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        pos: Int,
                        id: Long
                    ) {
                        Log.d(TAG, "itemSelected pos: ${itemsArray[pos]}")
                        interaction.onDeviceStateChanged(
                            "${item.topic}/speed",
                            itemsArray[pos].removeSuffix("%")
                        )
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
        }
    }

    inner class HeaterViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction
    ) : BaseViewHolder<Device>(itemView) {
        override fun bind(item: Device) = with(item as Device.Heater) {
            Log.d(TAG, "Binding Heater viewHolder: $item")
            itemView.heater_text_device_name.text = item.getSimpleName()
            initializeSeekArc(item)
            itemView.text_heater_value.text = item.value ?: "0"
            item.state?.let {
                itemView.constraint_layout_heater_value.background =
                    if (it) {
                        itemView.context.getDrawable(R.drawable.circle_green)
                    } else {
                        itemView.context.getDrawable(R.drawable.circle_red)
                    }
            }
            itemView.constraint_layout_heater_value.setOnClickListener {
                interaction.onDeviceStateChanged(
                    "${item.topic}/state",
                    if (item.state == null || item.state == false) {
                        "on"
                    } else {
                        "off"
                    }
                )
            }
        }

        private fun initializeSeekArc(item: Device.Heater) {
            itemView.seek_arc.apply {
                if (item.state == null || item.state == false) {
                    arcColor = itemView.context.getColor(R.color.colorAccentRed)
                    progressColor = itemView.context.getColor(R.color.colorAccentRed)
                } else {
                    arcColor = itemView.context.getColor(R.color.colorAccentGreen)
                    progressColor = itemView.context.getColor(R.color.colorAccentGreen)
                }
                progress = try {
                    item.value?.toFloat()?.toInt() ?: 0
                } catch (e: Exception) {
                    // When temp is a decimal, since seek arc library does not support it.
                    0
                }
                setOnSeekArcChangeListener(object : SeekArc.OnSeekArcChangeListener {
                    override fun onProgressChanged(p0: SeekArc?, p1: Int, p2: Boolean) {
                        itemView.text_heater_value.text =
                            String.format("%d", p1)
                    }

                    override fun onStartTrackingTouch(p0: SeekArc?) {
                    }

                    override fun onStopTrackingTouch(p0: SeekArc?) {
                        interaction.onDeviceStateChanged(
                            "${item.topic}/value",
                            itemView.text_heater_value.text.toString()
                        )
                    }
                })
            }
        }
    }

    inner class AlarmViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction
    ) : BaseViewHolder<Device>(itemView) {
        override fun bind(item: Device) = with(item as Device.Alarm) {
            Log.d(TAG, "Binding Alarm viewHolder: $item")
            itemView.alarm_text_device_name.text = item.getSimpleName()
            itemView.switch_alarm_activate.isChecked = item.active ?: false
            itemView.switch_alarm_trigger.isChecked = item.triggered ?: false
            itemView.switch_alarm_activate.setOnClickListener {
                interaction.onDeviceStateChanged(
                    "${item.topic}/state",
                    if (itemView.switch_alarm_activate.isChecked) "on" else "off"
                )
            }
        }
    }

    inner class TriggerViewHolder
    constructor(
        itemView: View
    ) : BaseViewHolder<Device>(itemView) {
        override fun bind(item: Device) = with(item as Device.Trigger) {
            Log.d(TAG, "Binding Trigger viewHolder: $item")
            itemView.trigger_text_device_name.text = item.getSimpleName()
            itemView.switch_trigger_trigger.isChecked = item.triggered ?: false
        }
    }

    inner class MicrowaveViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction
    ) : BaseViewHolder<Device>(itemView) {
        override fun bind(item: Device) = with(item as Device.Microwave) {
            Log.d(TAG, "Binding Microwave viewHolder: $item")
            itemView.microwave_text_device_name.text = item.getSimpleName()
            val wattItemsArray =
                itemView.context.resources.getStringArray(R.array.microwave_watt_array)
            val presetItemsArray =
                itemView.context.resources.getStringArray(R.array.microwave_preset_array)
            itemView.spinner_microwave_watt.adapter = ArrayAdapter<String>(
                itemView.context, R.layout.spinner_item, wattItemsArray
            )
            itemView.spinner_microwave_preset.adapter = ArrayAdapter<String>(
                itemView.context, R.layout.spinner_item, presetItemsArray
            )
            item.manualStart?.let {
                val split = it.chunked(5)
                val watt = split.component1()
                val time = split.component2()
                itemView.spinner_microwave_watt.setSelection(
                    wattItemsArray.indexOfFirst { arrayItem ->
                        arrayItem.removeSuffix("w") == watt.substring(2)
                    }, false
                )
                itemView.text_microwave_time.text = formatTime(time.removePrefix("t"))
            } ?: itemView.spinner_microwave_watt.setSelection(0, false)
            itemView.text_microwave_time.setOnClickListener {
                showTimePickerDialog()
            }
            itemView.button_microwave_manual.setOnClickListener {
                interaction.onDeviceStateChanged(
                    "${item.topic}/manual_start",
                    "w0"
                            + wattItemsArray[itemView.spinner_microwave_watt.selectedItemPosition]
                        .removeSuffix("w")
                            + "t"
                            + itemView.text_microwave_time.text.toString().removeRange(2, 3)
                )
            }
            itemView.button_microwave_preset.setOnClickListener {
                interaction.onDeviceStateChanged(
                    "${item.topic}/preset_start",
                    itemView.spinner_microwave_preset.selectedItem.toString()
                )
            }
        }

        private fun showTimePickerDialog() {
            itemView.context?.let {
                val dialog = MaterialDialog(it).customView(R.layout.layout_time_picker)
                dialog.getCustomView().apply {
                    picker_seconds.minValue = 0
                    picker_seconds.maxValue = 59
                    picker_minutes.minValue = 0
                    picker_minutes.maxValue = 10
                    picker_seconds.value =
                        itemView.text_microwave_time.text.toString().takeLast(2).toInt()
                    picker_minutes.value =
                        itemView.text_microwave_time.text.toString().take(2).toInt()
                }
                dialog.show {
                    title(R.string.time_picker)
                    positiveButton(R.string.apply) {
                        val secondsPicked = view.picker_seconds.value
                        val minutesPicked = view.picker_minutes.value
                        itemView.text_microwave_time.text =
                            formatTime(
                                String.format("%02d", minutesPicked) +
                                        String.format("%02d", secondsPicked)
                            )
                    }
                    negativeButton(R.string.cancel) {
                    }
                    neutralButton(R.string.clear_time) {
                        itemView.text_microwave_time.text = formatTime("0000")
                    }
                }
            }
        }

        private fun formatTime(string: String): String {
            val minutes = string.substring(0, 2)
            val seconds = string.substring(2, 4)
            return "$minutes:$seconds"
        }

    }

    inner class BluetoothFanViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction
    ) : BaseViewHolder<Device>(itemView) {
        override fun bind(item: Device) = with(item as Device.BluetoothFan) {
            Log.d(TAG, "Binding BluetoothFan viewHolder: $item")
            itemView.bluetooth_fan_text_device_name.text = item.getSimpleName()
            itemView.switch_bluetooth_fan_state.isChecked = item.state ?: false
            itemView.switch_bluetooth_fan_swing.isChecked = item.swing ?: false
            itemView.switch_bluetooth_fan_speed.isChecked = item.speed ?: false
            itemView.switch_bluetooth_fan_state.setOnClickListener {
                interaction.onDeviceStateChanged(
                    "${item.topic}/state",
                    if (itemView.switch_bluetooth_fan_state.isChecked) "on" else "off"
                )
            }
            itemView.switch_bluetooth_fan_swing.setOnClickListener {
                interaction.onDeviceStateChanged(
                    "${item.topic}/swing",
                    if (itemView.switch_bluetooth_fan_swing.isChecked) "true" else "false"
                )
            }
            itemView.switch_bluetooth_fan_speed.setOnClickListener {
                interaction.onDeviceStateChanged(
                    "${item.topic}/speed",
                    if (itemView.switch_bluetooth_fan_speed.isChecked) "higher" else "lower"
                )
            }
        }
    }

    abstract class BaseViewHolder<T>(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }

    interface Interaction {
        fun onDeviceStateChanged(topic: String, message: String)
    }
}
