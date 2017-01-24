package com.jereksel.libresubstratum

import kotlin.reflect.KProperty

class ResettableLazy<V>(val initializer: () -> V) {

    private val lock = Object()
    @Volatile private var value: V? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): V {
        if (value == null) {
            synchronized(lock) {
                if (value == null) {
                    value = initializer()
                }
            }
        }
        return value!!
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        synchronized(lock) {
            this.value = value
        }
    }

}