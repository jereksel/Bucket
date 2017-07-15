package com.jereksel.libresubstratum.data

import com.jereksel.libresubstratumlib.Type1Extension

class Type1ExtensionToString(val type1: Type1Extension){
    override fun toString(): String {
        return type1.name.removeSuffix(".xml")
    }
}
