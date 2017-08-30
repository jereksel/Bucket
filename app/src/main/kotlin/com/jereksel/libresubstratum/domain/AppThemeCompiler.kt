package com.jereksel.libresubstratum.domain

import android.app.Application
import android.os.Environment
import com.google.common.io.Files
import com.jereksel.libresubstratumlib.AAPT
import com.jereksel.libresubstratumlib.InvalidInvocationException
import com.jereksel.libresubstratumlib.ThemeToCompile
import java.io.File
import kellinwood.security.zipsigner.ZipSigner

class AppThemeCompiler(
        val app: Application,
        val packageManager: IPackageManager
) : ThemeCompiler {

    val aapt = File(app.cacheDir, "appt")

    init {

//        val stream = app.assets.open("aapt")
        if (aapt.exists()) {
            aapt.delete()
        }

        app.assets.open("aapt").use { stream ->
            aapt.outputStream().use { file ->
                stream.copyTo(file)
            }
        }

//        val file = aapt.outputStream()
//        stream.copyTo(aapt.outputStream())
        aapt.setExecutable(true)

    }

    @Throws(InvalidInvocationException::class)
    override fun compileTheme(themeDate: ThemeToCompile, dir: File): File {
//        File.createTempFile("", null)
        val temp = Files.createTempDir()
//        val finalApk = File.createTempFile("compiled", ".apk")
        val apkDir = File(Environment.getExternalStorageDirectory(), "libresubs")
        apkDir.mkdir()
        val finalApk = File.createTempFile("compiled", ".apk", apkDir)
//        File.cre
//        val finalApk = File(Environment.getExternalStorageDirectory()

//        val pass = "libre1".toCharArray()

        val targetApk = themeDate.targetAppId

        val loc = packageManager.getAppLocation(targetApk).absolutePath

        val file = AAPT(aapt.absolutePath).compileTheme(themeDate, dir, temp, listOf("/system/framework/framework-res.apk", loc))

        //TODO: Replace with AOSP signer after AS 3.0 drops
        val zipSigner = ZipSigner()
        zipSigner.keymode = "testkey"
        zipSigner.signZip(file, finalApk)

//        file.copyTo(finalApk)

//        temp.deleteRecursively()

        return finalApk
    }

    private fun ZipSigner.signZip(file: File, finalApk: File) = signZip(file.absolutePath, finalApk.absolutePath)
}

