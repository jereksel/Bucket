package com.jereksel.libresubstratumlib

import org.slf4j.LoggerFactory
import java.io.File
import java.util.regex.Pattern

class AAPT(val aaptPath: String, testing: Boolean = false) {

    private val logger = LoggerFactory.getLogger(javaClass.name)

    private val COLOR_PATTERN = Pattern.compile(":color/(.*):.*?d=(\\S*)")

    val generator = AndroidManifestGenerator(testing)

    fun getColorsValues(apk: File): Sequence<Color> {

        val cmd = listOf(aaptPath, "d", "resources", apk.absolutePath)
        val proc = ProcessBuilder(cmd).start()

        val statusCode = proc.waitFor()
        val output = proc.inputStream.bufferedReader().use { it.readText() }
        val error = proc.errorStream.bufferedReader().use { it.readText() }

        if (statusCode != 0) {
            throw InvalidInvocationException(error)
        }

        return output.lineSequence()
                .mapNotNull {
                    val matcher = COLOR_PATTERN.matcher(it)
                    if (matcher.find()) {
                        Color(matcher.group(1), matcher.group(2))
                    } else {
                        null
                    }
                }
    }

    fun getColorValue(apk: File, color: String) = getColorsValues(apk).first { it.name == color }.value

    fun compileTheme(themeDate: ThemeToCompile, dir: File, tempDir: File, additionalApks: List<String> = listOf()): File {
        if (!dir.exists()) {
            throw IllegalArgumentException("$dir doesn't exist")
        }

        if (!tempDir.exists()) {
            throw IllegalArgumentException("$tempDir doesn't exist")
        }

        val finalOverlayDir: File
        val finalTempDir: File

        if (themeDate.type1.any { !it.extension.default }) {
            //There will we file replacing required - let's copy theme source dir
            val newDir = File(tempDir, "overlay")
            val newTempDir = File(tempDir, "temp")
            newDir.mkdir()
            newTempDir.mkdir()

            dir.copyRecursively(newDir)

            themeDate.type1
                    .filterNot { it.extension.default }
                    .forEach {
                        val source = File(newDir, "type1${it.suffix}_${it.extension.name}.xml")
                        val dest = File(newDir, "res", "values", "type1${it.suffix}.xml")
                        source.copyTo(dest, overwrite = true)
                    }

            finalOverlayDir = newDir
            finalTempDir = newTempDir
        } else {
            finalOverlayDir = dir
            finalTempDir = tempDir
        }

        val manifest = generator.generateManifest(themeDate)
        val manifestFile = File(finalTempDir, "AndroidManifest.xml")

        manifestFile.createNewFile()
        manifestFile.writeText(manifest)

        val res = File(finalOverlayDir, "res")

        File(tempDir, "gen").mkdir()

        val command = mutableListOf(aaptPath, "package", "--auto-add-overlay", "-f", "-M", "AndroidManifest.xml", "-F", "Theme.apk")

        additionalApks.forEach {
            command.addAll(listOf("-I", it))
        }

        if (themeDate.type3 != null && !themeDate.type3.default) {
            val file = File(finalOverlayDir, "type3_${themeDate.type3.name}", "res")
            if (file.exists()) {
                command.addAll(listOf("-S", file.absolutePath))
            } else {
                command.addAll(listOf("-S", file.parentFile.absolutePath))
            }
        }

        if (themeDate.type2 != null && !themeDate.type2.default) {
            val file = File(finalOverlayDir, "type2_${themeDate.type2.name}", "res")
            if (file.exists()) {
                command.addAll(listOf("-S", file.absolutePath))
            } else {
                command.addAll(listOf("-S", file.parentFile.absolutePath))
            }
        }

        command.addAll(listOf("-S", res.absolutePath))

        logger.debug("Invoking: {}", command.joinToString(separator = " "))

        val proc = ProcessBuilder(command)
                .directory(finalTempDir)
                .start()

        proc.waitFor()

        val statusCode = proc.exitValue()
        val output = proc.inputStream.bufferedReader().use { it.readText() }
        val error = proc.errorStream.bufferedReader().use { it.readText() }

        if (statusCode != 0) {
            throw InvalidInvocationException(error)
        }

        return File(finalTempDir, "Theme.apk")
    }

//    fun compileTheme(appId: String, dir: File, tempDir: File) = compileTheme(ThemeToCompile(appId, "", appId), dir, tempDir)

    fun File(file: File, vararg subDirs: String) = subDirs.fold(file) { total, next -> java.io.File(total, next) }

}
