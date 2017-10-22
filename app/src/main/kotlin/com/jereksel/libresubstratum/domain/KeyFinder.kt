package com.jereksel.libresubstratum.domain

import android.content.Context
import android.content.pm.PackageManager
import com.google.common.io.Files.createTempDir
import com.jereksel.libresubstratum.data.KeyPair
import com.jereksel.libresubstratum.extensions.getLogger
import dalvik.system.DexClassLoader

class KeyFinder(
        val context: Context,
        val packageManager: IPackageManager
): IKeyFinder {

    val log = getLogger()

    override fun getKey(appId: String): KeyPair? {

        val themeInfo = packageManager.getInstalledTheme(appId)

        if (!themeInfo.encrypted) {
            return null
        }

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

            if (key.size == 16 && iv.size == 16) {
                return KeyPair(key, iv)
            } else {
                log.warn("Wrong array sizes: {} {}", key.size, iv.size)
            }

        } catch (e: Exception) {
            log.error("Cannot get keys ", e)
        }

        return null

    }

}