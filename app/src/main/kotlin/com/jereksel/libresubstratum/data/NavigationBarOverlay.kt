package com.jereksel.libresubstratum.data

import java.util.*

data class NavigationBarOverlay(
        val id: String,
        val left: ByteArray,
        val center: ByteArray,
        val right: ByteArray
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NavigationBarOverlay

        if (id != other.id) return false
        if (!Arrays.equals(left, other.left)) return false
        if (!Arrays.equals(center, other.center)) return false
        if (!Arrays.equals(right, other.right)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + Arrays.hashCode(left)
        result = 31 * result + Arrays.hashCode(center)
        result = 31 * result + Arrays.hashCode(right)
        return result
    }
}