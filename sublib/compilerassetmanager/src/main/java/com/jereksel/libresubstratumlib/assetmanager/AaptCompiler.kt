package com.jereksel.libresubstratumlib.assetmanager

import android.content.res.AssetManager
import com.jereksel.libresubstratumlib.AndroidManifestGenerator
import com.jereksel.libresubstratumlib.InvalidInvocationException
import com.jereksel.libresubstratumlib.ThemeToCompile
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

class AaptCompiler(
        val aaptPath: String,
        testing: Boolean = false
) {

    private val logger = LoggerFactory.getLogger(javaClass.name)

    val generator = AndroidManifestGenerator(testing)

    fun compileTheme(assetManager: AssetManager, location: String, themeDate: ThemeToCompile, tempDir: File, additionalApks: List<String> = listOf()): File {

        val apkLocation = File(tempDir, "overlay.apk")
        val compilationDir = File(tempDir, "compilation")
        compilationDir.mkdirs()

        val manifest = generator.generateManifest(themeDate)
        val manifestFile = File(tempDir, "AndroidManifest.xml")

        manifestFile.createNewFile()
        manifestFile.writeText(manifest)

        val amLoc = "overlays/$location"

        val list = assetManager.list(amLoc).toSet()

        val mainRes = File(compilationDir, "res")

        if (list.contains("res")) {
            mainRes.mkdirs()
            assetManager.extract("$amLoc/res", mainRes)
        }

        themeDate.type1
                .filterNot { it.extension.default }
                .forEach {
                    val amLocation = "$amLoc/type1${it.suffix}_${it.extension.name}.xml"
                    val source = File(tempDir, "type1${it.suffix}_${it.extension.name}.xml")
                    assetManager.extract(amLocation, source)
                    val dest = File(mainRes, "values", "type1${it.suffix}.xml")
                    source.copyTo(dest, overwrite = true)
                }

        val command = mutableListOf(aaptPath, "package", "--auto-add-overlay", "-f", "-M", manifestFile.absolutePath, "-F", apkLocation.absolutePath)

        additionalApks.forEach {
            command.addAll(listOf("-I", it))
        }

        val type2 = themeDate.type2
        val type3 = themeDate.type3

        if (type2 != null && !type2.default) {
            val file = File(tempDir, "type2")
            val amLocation = "$amLoc/type2_${type2.name}"
            assetManager.extract(amLocation, file)
            if (File(file, "res").exists()) {
                command.addAll(listOf("-S", File(file, "res").absolutePath))
            } else if (file.exists()) {
                command.addAll(listOf("-S", file.absolutePath))
            }
        }

        if (type3 != null && !type3.default) {
            val file = File(tempDir, "type3")
            val amLocation = "$amLoc/type3_${type3.name}"
            assetManager.extract(amLocation, file)
            if (file.exists()) {
                if (File(file, "res").exists()) {
                    command.addAll(listOf("-S", File(file, "res").absolutePath))
                } else if (file.exists()) {
                    command.addAll(listOf("-S", file.absolutePath))
                }
            }
        }

        if (mainRes.exists()) {
            command.addAll(listOf("-S", mainRes.absolutePath))
        }

        logger.debug("Invoking: {}", command.joinToString(separator = " "))

        logger.debug("AndroidManifest:\n{}", manifest)

        val proc = ProcessBuilder(command)
                .start()

        proc.waitFor()

        val statusCode = proc.exitValue()
        val output = proc.inputStream.bufferedReader().use { it.readText() }
        val error = proc.errorStream.bufferedReader().use { it.readText() }

        if (statusCode != 0) {
            throw InvalidInvocationException(error)
        }

        return apkLocation
    }

    private fun AssetManager.extract(location: String, dest: File, transform: (InputStream) -> (InputStream) = { it }) {

        val children = this.list(location)

        if (children.isEmpty()) {
            //This is file
            dest.parentFile.mkdirs()
            if (dest.exists()) {
                dest.delete()
            }

            val `is`: InputStream

            try {
                `is` = open(location)
            } catch (e: FileNotFoundException) {
                return
            }

            dest.createNewFile()

            `is`.use { inputStream ->
                dest.outputStream().use { fileOutputStream ->
                    transform(inputStream).copyTo(fileOutputStream)
                }
            }
        } else {
            children.forEach { extract("$location/$it", File(dest, it), transform) }
        }

    }

    fun File(file: File, vararg subDirs: String) = subDirs.fold(file) { total, next -> java.io.File(total, next) }

}