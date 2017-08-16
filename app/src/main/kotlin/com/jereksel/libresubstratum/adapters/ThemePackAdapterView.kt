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
    fun setInstalled(installed: Boolean)
}