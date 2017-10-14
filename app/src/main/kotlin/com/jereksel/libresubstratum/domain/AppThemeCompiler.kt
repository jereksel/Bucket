package com.jereksel.libresubstratum.domain

import android.app.Application
import android.os.Environment
import com.google.common.io.Files
import com.jereksel.libresubstratum.extensions.getFile
import com.jereksel.libresubstratum.extensions.getLogger
import com.jereksel.libresubstratumlib.InvalidInvocationException
import com.jereksel.libresubstratumlib.ThemeToCompile
import com.jereksel.libresubstratumlib.assetmanager.AaptCompiler
import java.io.File
import kellinwood.security.zipsigner.ZipSigner
import java.util.concurrent.TimeUnit

class AppThemeCompiler(
        val app: Application,
        val packageManager: IPackageManager
) : ThemeCompiler {

    val log = getLogger()

    val aapt = getFile(File(app.applicationInfo.dataDir), "lib", "libaaptcomplete.so")

    init {
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

        val (file, compilationTime) = timeOfExec {
            AaptCompiler(aapt.absolutePath).compileTheme(assetManager, themeDate, temp, listOf("/system/framework/framework-res.apk", loc))
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
