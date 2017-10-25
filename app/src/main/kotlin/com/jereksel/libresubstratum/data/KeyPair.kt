package com.jereksel.libresubstratum.data

import java.io.InputStream
import java.util.*
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

data class KeyPair(
        val key: ByteArray,
        val iv: ByteArray
) {

    companion object {
        val EMPTYKEY = KeyPair(byteArrayOf(), byteArrayOf())
    }

    fun getTransformer() : (InputStream) -> (InputStream) {
        if (key.isEmpty() || iv.isEmpty()) {
            return { it }
        } else {
            return {
                val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
                cipher.init(
                        Cipher.DECRYPT_MODE,
                        SecretKeySpec(key.clone(), "AES"),
                        IvParameterSpec(iv.clone())
                )
                CipherInputStream(it, cipher)
            }
        }
    }

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