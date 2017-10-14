package com.jereksel.libresubstratum.data

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

//https://youtrack.jetbrains.com/issue/KT-19899#tab=Changes
//@Parcelize
data class KeyPair(
        val key: ByteArray,
        val iv: ByteArray
): Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.createByteArray(),
            parcel.createByteArray())

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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByteArray(key)
        parcel.writeByteArray(iv)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<KeyPair> {
        override fun createFromParcel(parcel: Parcel): KeyPair {
            return KeyPair(parcel)
        }

        override fun newArray(size: Int): Array<KeyPair?> {
            return arrayOfNulls(size)
        }
    }


}