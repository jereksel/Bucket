package com.jereksel.libresubstratum.data

import io.kotlintest.specs.FunSpec
import org.junit.Assert
import org.junit.Assert.assertArrayEquals
import java.util.*
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class KeyPairTest: FunSpec() {

    init {

        val data = byteArrayOf(1,2,3,4,5,6,7,8,9,10)

        test("When KeyPair is empty, stream is not changed") {

            val key = KeyPair.EMPTYKEY

            val transformerFun = key.getTransformer()

            val output = transformerFun(data.inputStream()).readBytes()

            assertArrayEquals(data, output)

        }

        test("When KeyPair is not empty stream decrypts") {

            val keyArr = ByteArray(16)
            val iv = ByteArray(16)

            Random().nextBytes(keyArr)
            Random().nextBytes(iv)

            val key = KeyPair(keyArr, iv)

            val encrypted = {
                val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
                cipher.init(
                        Cipher.ENCRYPT_MODE,
                        SecretKeySpec(keyArr, "AES"),
                        IvParameterSpec(iv.clone())
                )
                CipherInputStream(data.inputStream(), cipher).readBytes()
            }()

            val decrypted = key.getTransformer()(encrypted.inputStream()).readBytes()

            assertArrayEquals(data, decrypted)

        }


    }

}