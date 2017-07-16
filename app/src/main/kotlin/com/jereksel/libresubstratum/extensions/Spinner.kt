package com.jereksel.libresubstratum.extensions

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