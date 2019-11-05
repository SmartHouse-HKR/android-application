package se.hkr.smarthouse.util

import androidx.lifecycle.MutableLiveData

// https://stackoverflow.com/questions/47941537/notify-observer-when-item-is-added-to-list-of-livedata
fun <T> MutableLiveData<List<T>>.add(item: T) {
    val updatedItems = this.value as ArrayList
    updatedItems.add(item)
    this.value = updatedItems
}

// for mutable list
operator fun <T> MutableLiveData<MutableList<T>>.plusAssign(item: T) {
    val value = this.value ?: mutableListOf()
    value.add(item)
    this.value = value
}
/*
// for immutable list
operator fun <T> MutableLiveData<List<T>>.plusAssign(item: T) {
    val value = this.value ?: emptyList()
    this.value = value + listOf(item)
}
*/
