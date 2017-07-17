package com.jereksel.libresubstratum.data

import com.jereksel.libresubstratumlib.Type3Extension

data class Type3ExtensionToString(val type3: Type3Extension) {
    override fun toString() = type3.name
}

