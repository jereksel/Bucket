package com.jereksel.libresubstratum.domain

import android.app.Application
import android.os.Environment
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


    //FROM GUAVA

    private val TEMP_DIR_ATTEMPTS = 10000

    fun createTempDir(): File {
        val baseDir = File(System.getProperty("java.io.tmpdir"))
        val baseName = System.currentTimeMillis().toString() + "-"

        for (counter in 0..TEMP_DIR_ATTEMPTS - 1) {
            val tempDir = File(baseDir, baseName + counter)
            if (tempDir.mkdir()) {
                return tempDir
            }
        }
        throw IllegalStateException(
                "Failed to create directory within "
                        + TEMP_DIR_ATTEMPTS
                        + " attempts (tried "
                        + baseName
                        + "0 to "
                        + baseName
                        + (TEMP_DIR_ATTEMPTS - 1)
                        + ')')
    }

    //END FROM GUAVA

    @Throws(InvalidInvocationException::class)
    override fun compileTheme(themeDate: ThemeToCompile, dir: File): File {
//        File.createTempFile("", null)
        val temp = createTempDir()
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

