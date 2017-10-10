package com.jereksel.libresubstratum.domain

import android.content.Context
import com.google.common.io.Files.createTempDir
import com.jereksel.libresubstratum.data.KeyPair
import com.jereksel.libresubstratum.extensions.getLogger
import dalvik.system.DexClassLoader
import kotlinx.android.parcel.Parcelize

class KeyFinder(
        val context: Context
): IKeyFinder {

    val log = getLogger()

    override fun getKey(appId: String): KeyPair? {

        val location = context.packageManager.getApplicationInfo(appId,0).sourceDir

        val temp1 = createTempDir()
        val temp2 = createTempDir()

        val classLoader = DexClassLoader(location, temp1.absolutePath, temp2.absolutePath, ClassLoader.getSystemClassLoader())

        val key = getProguardedKeyPair(appId, classLoader)

        temp1.deleteRecursively()
        temp2.deleteRecursively()

        return key
    }

    private fun getProguardedKeyPair(appId: String, classLoader: DexClassLoader): KeyPair? {

        try {

            val clz = classLoader.loadClass("$appId.a")

            val key = clz.getField("a").get(null) as ByteArray
            val iv = clz.getField("b").get(null) as ByteArray

            return KeyPair(key, iv)

        } catch (e: Exception) {
            log.error("Cannot get keys", e)
        }

        return null

    }

}