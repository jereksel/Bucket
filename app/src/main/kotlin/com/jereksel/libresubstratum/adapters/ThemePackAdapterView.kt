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

package com.jereksel.libresubstratum.adapters

import android.graphics.drawable.Drawable
import com.jereksel.libresubstratum.data.Type1ExtensionToString
import com.jereksel.libresubstratum.data.Type2ExtensionToString

interface ThemePackAdapterView {
    fun setAppId(id: String)
    fun setAppName(name: String)
    fun setAppIcon(icon: Drawable?)
    fun setCheckbox(checked: Boolean)
    fun type1aSpinner(list: List<Type1ExtensionToString>, position: Int)
    fun type1bSpinner(list: List<Type1ExtensionToString>, position: Int)
    fun type1cSpinner(list: List<Type1ExtensionToString>, position: Int)
    fun type2Spinner(list: List<Type2ExtensionToString>, position: Int)
    fun setInstalled(version1: String?, version2: String?)
    fun setEnabled(enabled: Boolean)
    fun setCompiling(compiling: Boolean)
    fun reset()
}