/*
 * Copyright (C) 2017 Andrzej Ressel (jereksel@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.jereksel.libresubstratum.domain

import android.content.Context
import android.os.Build
import com.google.common.io.Files.createTempDir
import com.jereksel.libresubstratum.data.KeyPair
import com.jereksel.libresubstratum.data.KeyPair.Companion.EMPTYKEY
import com.jereksel.libresubstratum.extensions.getLogger
import dalvik.system.DexClassLoader
import org.apache.maven.artifact.versioning.ComparableVersion
import org.zeroturnaround.zip.ZipUtil
import java.io.File

class KeyFinder(
        val context: Context,
        val packageManager: IPackageManager
): IKeyFinder {

    val log = getLogger()

    val JNI_TEMPLATE = ComparableVersion("11.0.6")

    override fun getKey(appId: String): KeyPair? {

        val themeInfo = packageManager.getInstalledTheme(appId)

        if (!themeInfo.encrypted) {
            return EMPTYKEY
        }

        val templateVersion = ComparableVersion(themeInfo.pluginVersion)

        if (templateVersion == JNI_TEMPLATE) {
            //11.0.6 can be both proguarded or native
            return getProguardedKeyPair(appId) ?: getNativeKeyPair(appId)
        }

        if (templateVersion > JNI_TEMPLATE) {
            return getNativeKeyPair(appId)
        }

        return getProguardedKeyPair(appId)
    }

    private fun getProguardedKeyPair(appId: String): KeyPair? {

        val location = context.packageManager.getApplicationInfo(appId,0).sourceDir

        val temp1 = createTempDir()
        val temp2 = createTempDir()

        val classLoader = DexClassLoader(location, temp1.absolutePath, temp2.absolutePath, ClassLoader.getSystemClassLoader())

        try {

            val clz = classLoader.loadClass("$appId.a")

            val key = clz.getField("a").get(null) as ByteArray
            val iv = clz.getField("b").get(null) as ByteArray

            if (key.size == 16 && iv.size == 16) {
                return KeyPair(key, iv)
            } else {
                log.error("Wrong array sizes: {} {}", key.size, iv.size)
            }

        } catch (e: Exception) {
            log.error("Cannot get keys ", e)
        }

        return null

    }

    private fun getNativeKeyPair(appId: String): KeyPair? {

        val apkLocation = context.packageManager.getApplicationInfo(appId, 0).sourceDir

        val abis = Build.SUPPORTED_32_BIT_ABIS

        val so = abis.asSequence()
                .filter { ZipUtil.containsEntry(File(apkLocation), "lib/$it/libLoadingProcess.so") }
                .map {
                    val temp = File.createTempFile("LLP", "so")
                    ZipUtil.unpackEntry(File(apkLocation), "lib/$it/libLoadingProcess.so", temp)
                    temp
                }
                .firstOrNull()

        if (so == null) {
            log.error("Cannot extract native library from $appId")
            return null
        }

        val (key, iv) = KeyFinderNative.getKeyAndIV(so.absolutePath) ?: return null

        val kp = KeyPair(key, iv)

        log.debug("KeyPair from native {}", kp)

        return kp
    }

}