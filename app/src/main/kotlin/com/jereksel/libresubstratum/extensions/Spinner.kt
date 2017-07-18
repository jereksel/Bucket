package com.jereksel.libresubstratum.extensions

import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.Spinner

var Spinner.list : List<Any>
inline set(list) {
    val spinnerArrayAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, list)
    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    adapter = spinnerArrayAdapter
}
get() {
    throw UnsupportedOperationException()
}

fun Spinner.getAllStrings() = this.adapter.getAllStrings()
fun <T> ArrayAdapter<T>.getAllItems() = (0..count-1).map { this.getItem(it) }
fun <T> ArrayAdapter<T>.getAllStrings() = getAllItems().map { it.toString() }
fun Adapter.getAllStrings() = (0..count-1).map { this.getItem(it) }.map { it.toString() }
