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

import android.app.Application
import android.os.Environment
import com.google.common.io.Files
import com.jereksel.aapt.AAPT.getAapt
import com.jereksel.libresubstratum.data.KeyPair
import com.jereksel.libresubstratum.extensions.getFile
import com.jereksel.libresubstratum.extensions.getLogger
import com.jereksel.libresubstratumlib.assetmanager.AaptCompiler
import com.jereksel.libresubstratumlib.compilercommon.InvalidInvocationException
import com.jereksel.libresubstratumlib.compilercommon.ThemeToCompile
import java.io.File
import kellinwood.security.zipsigner.ZipSigner
import java.io.InputStream
import java.util.concurrent.TimeUnit
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class AppThemeCompiler(
        val app: Application,
        val packageManager: IPackageManager,
        val keyFinder: IKeyFinder
) : ThemeCompiler {

    val log = getLogger()

    val aapt = getAapt(app)

    init {
        //So unpack is forced
        System.loadLibrary("aaptcomplete")
        log.debug("AAPT version: {}", ProcessBuilder().command(listOf(aapt.absolutePath, "v")).start().inputStream.bufferedReader().readText())
    }

    @Throws(InvalidInvocationException::class)
    override fun compileTheme(themeDate: ThemeToCompile): File {

        val temp = Files.createTempDir()

        val apkDir = File(Environment.getExternalStorageDirectory(), "libresubs")
        apkDir.mkdir()
        val finalApk = File.createTempFile("compiled", ".apk", apkDir)

        val targetApk = themeDate.fixedTargetApp

        val loc = packageManager.getAppLocation(targetApk).absolutePath

        val assetManager = app.packageManager.getResourcesForApplication(themeDate.targetThemeId).assets

        val key = keyFinder.getKey(themeDate.targetThemeId)

        val transform = (key ?: KeyPair.EMPTYKEY).getTransformer()

        val (file, compilationTime) = timeOfExec {
            AaptCompiler(aapt.absolutePath).compileTheme(assetManager, themeDate, temp, listOf("/system/framework/framework-res.apk", loc), transform)
        }

        val seconds = TimeUnit.MILLISECONDS.toSeconds(compilationTime)

        log.debug("Compilation of {} took {}s", themeDate.targetOverlayId, seconds)

        //TODO: Replace with AOSP signer after AS 3.0 drops
        val (_, timeOfSigning) = timeOfExec {
            val zipSigner = ZipSigner()
            zipSigner.keymode = "testkey"
            zipSigner.signZip(file, finalApk)
        }

        val seconds2 = TimeUnit.MILLISECONDS.toSeconds(timeOfSigning)

        log.debug("Signing of {} took {}s", themeDate.targetOverlayId, seconds2)

        temp.deleteRecursively()

        return finalApk
    }

    private fun ZipSigner.signZip(file: File, finalApk: File) = signZip(file.absolutePath, finalApk.absolutePath)

    private inline fun <T> timeOfExec(f: () -> T): Pair<T,Long> {
        val t1 = System.currentTimeMillis()
        val t = f()
        val t2 = System.currentTimeMillis()
        return Pair(t, t2 - t1)
    }
}
