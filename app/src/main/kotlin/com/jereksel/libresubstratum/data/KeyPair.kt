package com.jereksel.libresubstratum.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class KeyPair(
        val key: ByteArray,
        val iv: ByteArray
): Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KeyPair

        if (!Arrays.equals(key, other.key)) return false
        if (!Arrays.equals(iv, other.iv)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(key)
        result = 31 * result + Arrays.hashCode(iv)
        return result
    }

    override fun toString(): String =
            "KeyPair(key=${Arrays.toString(key)}, iv=${Arrays.toString(iv)})"

}