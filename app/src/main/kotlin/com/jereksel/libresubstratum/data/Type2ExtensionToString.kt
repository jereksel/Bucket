package com.jereksel.libresubstratum.data

import com.jereksel.libresubstratumlib.Type2Extension

data class Type2ExtensionToString(val type2: Type2Extension){
    override fun toString() = type2.name
}
