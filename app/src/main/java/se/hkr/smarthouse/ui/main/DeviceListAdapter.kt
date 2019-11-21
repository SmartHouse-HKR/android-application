package se.hkr.smarthouse.ui.main

import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.device_list_item_header.view.*
import kotlinx.android.synthetic.main.device_list_item_interactive_on_off.view.*
import kotlinx.android.synthetic.main.device_list_item_interactive_rgb.view.*
import kotlinx.android.synthetic.main.device_list_item_interactive_temperature.view.*
import se.hkr.smarthouse.R
import se.hkr.smarthouse.models.Device

class DeviceListAdapter(
    private val interaction: Interaction
) : RecyclerView.Adapter<DeviceListAdapter.BaseViewHolder<*>>() {
    companion object {
        const val TAG = "AppDebug"
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Device>() {
        override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
            return oldItem.topic == newItem.topic
        }

        override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
            try {
                return when (oldItem) {
                    is Device.UnknownDevice -> {
                        return true
                    }
                    is Device.InteractiveOnOff -> {
                        newItem as Device.InteractiveOnOff
                        newItem.state == oldItem.state
                    }
                    is Device.ObservableOnOff -> {
                        newItem as Device.ObservableOnOff
                        newItem.state == oldItem.state
                    }
                    is Device.InteractiveTemperature -> {
                        newItem as Device.InteractiveTemperature
                        newItem.temperature == oldItem.temperature
                    }
                    is Device.ObservableTemperature -> {
                        newItem as Device.ObservableTemperature
                        newItem.temperature == oldItem.temperature
                    }
                    is Device.InteractiveRgb -> {
                        newItem as Device.InteractiveRgb
                        return (newItem.red == oldItem.red &&
                                newItem.green == oldItem.green &&
                                newItem.blue == oldItem.blue)
                    }
                }
            } catch (e: Exception) {
                // If typecast failed, means they are different class anyway, therefore different.
                return false
            }
        }
    }

    private val differ = object : AsyncListDiffer<Device>(this, diffCallback) {
        // Have to override because if the list send to submitList is == to the old list, which
        // is true since we are also updating the local variable whenever there is a change. We get
        // around it by sending a copy of the list which does not return true on the == operation
        // and therefore continues on the method and triggers the notifyDataSetChanged()
        override fun submitList(newList: List<Device>?, commitCallback: Runnable?) {
            super.submitList(newList?.toList(), commitCallback)
        }

        override fun submitList(newList: MutableList<Device>?) {
            super.submitList(newList?.toList())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            Device.UnknownDevice.IDENTIFIER -> {
                EmptyViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.device_list_item_empty, parent, false
                    )
                )
            }
            Device.InteractiveOnOff.IDENTIFIER -> {
                InteractiveOnOffViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.device_list_item_interactive_on_off, parent, false
                    ),
                    interaction
                )
            }
            Device.ObservableOnOff.IDENTIFIER -> {
                ObservableOnOffViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.device_list_item_observable_on_off, parent, false
                    )
                )
            }
            Device.InteractiveTemperature.IDENTIFIER -> {
                InteractiveTemperatureViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.device_list_item_interactive_temperature, parent, false
                    ),
                    interaction
                )
            }
            Device.ObservableTemperature.IDENTIFIER -> {
                ObservableTemperatureViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.device_list_item_observable_temperature, parent, false
                    )
                )
            }
            Device.InteractiveRgb.IDENTIFIER -> {
                InteractiveRgbViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.device_list_item_interactive_rgb, parent, false
                    ),
                    interaction
                )
            }
            else -> EmptyViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.device_list_item_empty, parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val element = differ.currentList[position]
        when (holder) {
            is InteractiveOnOffViewHolder -> {
                holder.bind(element as Device.InteractiveOnOff)
            }
            is ObservableOnOffViewHolder -> {
                holder.bind(element as Device.ObservableOnOff)
            }
            is InteractiveTemperatureViewHolder -> {
                holder.bind(element as Device.InteractiveTemperature)
            }
            is ObservableTemperatureViewHolder -> {
                holder.bind(element as Device.ObservableTemperature)
            }
            is InteractiveRgbViewHolder -> {
                holder.bind(element as Device.InteractiveRgb)
            }
            is EmptyViewHolder -> {
                holder.bind(element)
            }
            else -> throw IllegalArgumentException("Illegal bind view holder")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (differ.currentList[position]) {
            is Device.UnknownDevice -> Device.UnknownDevice.IDENTIFIER
            is Device.InteractiveOnOff -> Device.InteractiveOnOff.IDENTIFIER
            is Device.ObservableOnOff -> Device.ObservableOnOff.IDENTIFIER
            is Device.InteractiveTemperature -> Device.InteractiveTemperature.IDENTIFIER
            is Device.ObservableTemperature -> Device.ObservableTemperature.IDENTIFIER
            is Device.InteractiveRgb -> Device.InteractiveRgb.IDENTIFIER
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Device>) {
        differ.submitList(list) { Log.d(TAG, "adapter list submission done") }
    }

    inner class EmptyViewHolder
    constructor(
        itemView: View
    ) : BaseViewHolder<Device>(itemView) {
        override fun bind(item: Device) {
            itemView.deviceTopic.text = "Unknown: ${item.topic}"
        }
    }

    inner class InteractiveOnOffViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction
    ) : BaseViewHolder<Device>(itemView) {
        override fun bind(item: Device) = with(itemView) {
            val currentItem = item as Device.InteractiveOnOff
            Log.d(TAG, "Binding interactive on/off: $currentItem")
            itemView.deviceName.text = currentItem.getSimpleName()
            itemView.deviceTopic.text = currentItem.topic
            itemView.deviceSwitch.isChecked = currentItem.state
            itemView.deviceSwitch.setOnClickListener {
                currentItem.state = itemView.deviceSwitch.isChecked
                interaction.onDeviceStateChanged(currentItem)
            }
        }
    }

    inner class ObservableOnOffViewHolder
    constructor(
        itemView: View
    ) : BaseViewHolder<Device>(itemView) {
        override fun bind(item: Device) = with(itemView) {
            val currentItem = item as Device.ObservableOnOff
            Log.d(TAG, "Binding observable on/off: $currentItem")
            itemView.deviceName.text = currentItem.getSimpleName()
            itemView.deviceTopic.text = currentItem.topic
            itemView.deviceSwitch.isChecked = currentItem.state
        }
    }

    inner class InteractiveTemperatureViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction
    ) : BaseViewHolder<Device>(itemView) {
        override fun bind(item: Device) = with(itemView) {
            val currentItem = item as Device.InteractiveTemperature
            Log.d(TAG, "Binding interactive temperature: $currentItem")
            itemView.deviceName.text = currentItem.getSimpleName()
            itemView.deviceTopic.text = currentItem.topic
            itemView.deviceSlider.value = currentItem.temperature.toFloat()
            itemView.deviceSlider.setOnChangeListener { _, value ->
                val roundedFloat = String.format("%.2f", value)
                currentItem.temperature = roundedFloat
                itemView.textViewSliderValue.text = roundedFloat
            }
            itemView.deviceSlider.setOnTouchListener { _, event ->
                if (event?.action == MotionEvent.ACTION_UP) {
                    Log.d(TAG, "Slider released, sending new value ${currentItem.temperature}")
                    interaction.onDeviceStateChanged(currentItem)
                }
                false
            }
        }
    }

    inner class ObservableTemperatureViewHolder
    constructor(
        itemView: View
    ) : BaseViewHolder<Device>(itemView) {
        override fun bind(item: Device) = with(itemView) {
            val currentItem = item as Device.ObservableTemperature
            Log.d(TAG, "Binding observable temperature: $currentItem")
            itemView.deviceName.text = currentItem.getSimpleName()
            itemView.deviceTopic.text = currentItem.topic
            itemView.deviceSlider.value = currentItem.temperature.toFloat()
            itemView.deviceSlider.isClickable = false
        }
    }

    inner class InteractiveRgbViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction
    ) : BaseViewHolder<Device>(itemView) {
        override fun bind(item: Device) = with(itemView) {
            val currentItem = item as Device.InteractiveRgb
            Log.d(TAG, "Binding rgb: $currentItem")
            itemView.deviceName.text = currentItem.getSimpleName()
            itemView.deviceTopic.text = currentItem.topic
            itemView.deviceSliderRed.value = currentItem.red.toFloat()
            itemView.deviceSliderGreen.value = currentItem.green.toFloat()
            itemView.deviceSliderBlue.value = currentItem.blue.toFloat()
            itemView.deviceSliderRed.setOnChangeListener { _, value ->
                val intValue = value.toInt()
                currentItem.red = intValue
                itemView.textViewRedValue.text = intValue.toString()
            }
            itemView.deviceSliderGreen.setOnChangeListener { _, value ->
                val intValue = value.toInt()
                currentItem.green = intValue
                itemView.textViewGreenValue.text = intValue.toString()
            }
            itemView.deviceSliderBlue.setOnChangeListener { _, value ->
                val intValue = value.toInt()
                currentItem.blue = intValue
                itemView.textViewBlueValue.text = intValue.toString()
            }
            itemView.deviceSliderRed.setOnTouchListener { _, event ->
                if (event?.action == MotionEvent.ACTION_UP) {
                    Log.d(TAG, "Slider released, sending new value ${currentItem.red}")
                    interaction.onDeviceStateChanged(currentItem)
                }
                false
            }
            itemView.deviceSliderGreen.setOnTouchListener { _, event ->
                if (event?.action == MotionEvent.ACTION_UP) {
                    Log.d(TAG, "Slider released, sending new value ${currentItem.green}")
                    interaction.onDeviceStateChanged(currentItem)
                }
                false
            }
            itemView.deviceSliderBlue.setOnTouchListener { _, event ->
                if (event?.action == MotionEvent.ACTION_UP) {
                    Log.d(TAG, "Slider released, sending new value ${currentItem.blue}")
                    interaction.onDeviceStateChanged(currentItem)
                }
                false
            }
        }
    }

    abstract class BaseViewHolder<T>(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }

    interface Interaction {
        fun onDeviceStateChanged(item: Device)
    }
}
