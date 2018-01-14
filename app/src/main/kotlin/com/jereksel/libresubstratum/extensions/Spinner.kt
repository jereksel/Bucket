/*
 * Copyright (C) 2017 Andrzej Ressel (jereksel@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.jereksel.libresubstratum.extensions

import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner

var Spinner.list : List<Any>
set(list) {
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
fun Adapter.getAllStrings() = (0 until count).map { this.getItem(it) }.map { it.toString() }

fun Spinner.selectListener(fn: (Int) -> Unit) {

    var user = false

    this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) = Unit

        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            if (user) {
                fn(position)
            } else {
                user = true
            }
        }
    }
}
