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
import se.hkr.smarthouse.models.*

class DeviceListAdapter(
    private val clickInteraction: ClickInteraction? = null,
    private val switchInteraction: SwitchInteraction? = null,
    private val sliderInteraction: SliderInteraction? = null
) : RecyclerView.Adapter<DeviceListAdapter.BaseViewHolder<*>>() {
    companion object {
        const val TAG = "AppDebug"
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Device>() {
        override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
            return oldItem.topic == newItem.topic
        }

        override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
            if (oldItem::class != newItem::class) {
                return false
            }
            return when (oldItem) {
                is InteractiveOnOff -> newItem as InteractiveOnOff == oldItem
                is ObservableOnOff -> newItem as ObservableOnOff == oldItem
                is InteractiveTemperature -> newItem as InteractiveTemperature == oldItem
                is ObservableTemperature -> newItem as ObservableTemperature == oldItem
                is InteractiveRgb -> newItem as InteractiveRgb == oldItem
            }
        }
    }
    private val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            InteractiveOnOff.IDENTIFIER -> {
                InteractiveOnOffViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.device_list_item_interactive_on_off, parent, false
                    ),
                    clickInteraction,
                    switchInteraction
                )
            }
            ObservableOnOff.IDENTIFIER -> {
                ObservableOnOffViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.device_list_item_observable_on_off, parent, false
                    )
                )
            }
            InteractiveTemperature.IDENTIFIER -> {
                InteractiveTemperatureViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.device_list_item_interactive_temperature, parent, false
                    ),
                    clickInteraction,
                    sliderInteraction
                )
            }
            /*ObservableTemperature.IDENTIFIER -> {

            }*/
            InteractiveRgb.IDENTIFIER -> {
                InteractiveRgbViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.device_list_item_interactive_rgb, parent, false
                    ),
                    clickInteraction
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
                holder.bind(element as InteractiveOnOff)
            }
            is ObservableOnOffViewHolder -> {
                holder.bind(element as ObservableOnOff)
            }
            is InteractiveTemperatureViewHolder -> {
            }
            /*is ObservableTemperatureViewHolder -> {

            }*/
            is InteractiveRgbViewHolder -> {
                holder.bind(element as InteractiveRgb)
            }
            is EmptyViewHolder -> {
                holder.bind(element)
            }
            else -> throw IllegalArgumentException("Illegal bind view holder")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (differ.currentList[position]) {
            is InteractiveOnOff -> InteractiveOnOff.IDENTIFIER
            is ObservableOnOff -> ObservableOnOff.IDENTIFIER
            is InteractiveTemperature -> InteractiveTemperature.IDENTIFIER
            is ObservableTemperature -> ObservableTemperature.IDENTIFIER
            is InteractiveRgb -> InteractiveRgb.IDENTIFIER
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Device>) {
        differ.submitList(list)
    }

    inner class InteractiveOnOffViewHolder
    constructor(
        itemView: View,
        private val clickInteraction: ClickInteraction?,
        private val switchInteraction: SwitchInteraction?
    ) : BaseViewHolder<Device>(itemView) {
        override fun bind(item: Device) = with(itemView) {
            val currentItem = item as InteractiveOnOff
            Log.d(TAG, "Binding interactive on/off: $currentItem")
            itemView.setOnClickListener {
                clickInteraction?.onItemSelected(adapterPosition, currentItem)
            }
            itemView.deviceSwitch.setOnClickListener {
                //TODO check if we need to update the value here on in the viewState
                switchInteraction?.onSwitchClicked(deviceSwitch.isChecked, currentItem)
            }
            itemView.deviceName.text = currentItem.getName()
            itemView.deviceTopic.text = currentItem.topic
            itemView.deviceSwitch.isChecked = currentItem.state
        }
    }

    inner class ObservableOnOffViewHolder
    constructor(
        itemView: View
    ) : BaseViewHolder<Device>(itemView) {
        override fun bind(item: Device) = with(itemView) {
            val currentItem = item as ObservableOnOff
            Log.d(TAG, "Binding observable on/off: $currentItem")
            itemView.deviceName.text = currentItem.getName()
            itemView.deviceTopic.text = currentItem.topic
            itemView.deviceSwitch.isChecked = currentItem.state
        }
    }

    inner class InteractiveTemperatureViewHolder
    constructor(
        itemView: View,
        private val clickInteraction: ClickInteraction?,
        private val sliderInteraction: SliderInteraction?
    ) : BaseViewHolder<Device>(itemView) {
        override fun bind(item: Device) = with(itemView) {
            val currentItem = item as InteractiveTemperature
            Log.d(TAG, "Binding slider: $currentItem")
            deviceSlider.setOnChangeListener { slider, value ->
                Log.d(TAG, "Slider: $slider, changed the value to $value")
                sliderInteraction?.onSliderUpdated(value.toInt(), currentItem)
            }
            itemView.setOnClickListener {
                clickInteraction?.onItemSelected(adapterPosition, currentItem)
            }

            itemView.deviceName.text = currentItem.getName()
            itemView.deviceTopic.text = currentItem.topic
            itemView.deviceSlider.value = currentItem.temperature
        }
    }

    inner class InteractiveRgbViewHolder
    constructor(
        itemView: View,
        private val clickInteraction: ClickInteraction?
    ) : BaseViewHolder<Device>(itemView) {
        override fun bind(item: Device) = with(itemView) {
            val currentItem = item as InteractiveRgb
            Log.d(TAG, "Binding rgb: $currentItem")
            itemView.deviceSliderRed.setOnChangeListener { _, value ->
                currentItem.red = value.toInt()
                itemView.textViewRedValue.text = value.toInt().toString()
            }
            itemView.deviceSliderGreen.setOnChangeListener { _, value ->
                currentItem.green = value.toInt()
                itemView.textViewGreenValue.text = value.toInt().toString()
            }
            itemView.deviceSliderBlue.setOnChangeListener { _, value ->
                currentItem.blue = value.toInt()
                itemView.textViewBlueValue.text = value.toInt().toString()
            }
            itemView.deviceSliderRed.setOnTouchListener { _, event ->
                if (event?.action == MotionEvent.ACTION_UP) {
                    //todo send event back to fragment
                }
                false
            }
            itemView.deviceSliderGreen.setOnChangeListener { _, value ->
                currentItem.green = value.toInt()
            }
            itemView.deviceSliderBlue.setOnChangeListener { _, value ->
                currentItem.blue = value.toInt()
            }
            itemView.setOnClickListener {
                clickInteraction?.onItemSelected(adapterPosition, currentItem)
            }

            itemView.deviceName.text = currentItem.getName()
            itemView.deviceTopic.text = currentItem.topic
            itemView.deviceSliderRed.value = currentItem.red.toFloat()
            itemView.deviceSliderGreen.value = currentItem.green.toFloat()
            itemView.deviceSliderBlue.value = currentItem.blue.toFloat()
        }
    }

    inner class EmptyViewHolder
    constructor(
        itemView: View
    ) : BaseViewHolder<Device>(itemView) {
        override fun bind(item: Device) {
        }
    }

    abstract class BaseViewHolder<T>(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }

    interface ClickInteraction {
        fun onItemSelected(position: Int, item: Device)
    }

    interface SwitchInteraction {
        fun onSwitchClicked(state: Boolean, item: Device)
    }

    interface SliderInteraction {
        fun onSliderUpdated(value: Int, item: Device)
    }
}
