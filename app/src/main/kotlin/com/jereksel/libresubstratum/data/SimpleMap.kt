package com.jereksel.libresubstratum.data

interface SimpleMap<K, V> {
    fun get(key: K): V?
    fun contains(key: K): Boolean
}