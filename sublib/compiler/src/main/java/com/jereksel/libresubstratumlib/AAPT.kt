package com.jereksel.libresubstratumlib

import com.jereksel.libresubstratumlib.AndroidManifestGenerator.generateManifest
import org.slf4j.LoggerFactory
import java.io.File
import java.util.regex.Pattern

class AAPT(val aaptPath: String) {

    private val logger = LoggerFactory.getLogger(javaClass.name)

    private val COLOR_PATTERN = Pattern.compile(":color/(.*):.*?d=(\\S*)")

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

    fun compileTheme(themeDate: ThemeToCompile, dir: File, tempDir: File): File {
        if (!dir.exists()) {
            throw IllegalArgumentException("$dir doesn't exist")
        }

        if (!tempDir.exists()) {
            throw IllegalArgumentException("$tempDir doesn't exist")
        }

        if (themeDate.type1.any { !it.excension.default }) {
            //There will we file replacing required - let's copy theme source dir
        }

        val manifest = generateManifest(themeDate.appId)
        val manifestFile = File(tempDir, "AndroidManifest.xml")

        manifestFile.createNewFile()
        manifestFile.writeText(manifest)

        val res = File(dir, "res")

        File(tempDir, "gen").mkdir()

        val command = mutableListOf(aaptPath, "package", "--auto-add-overlay", "-f", "-M", "AndroidManifest.xml", "-F", "Theme.apk")

        if (themeDate.type3 != null && !themeDate.type3.default) {
            command.addAll(listOf("-S", File(File(dir.absolutePath, "type3_${themeDate.type3.name}"), "res").absolutePath))
        }

        if (themeDate.type2 != null && !themeDate.type2.default) {
            command.addAll(listOf("-S", File(File(dir.absolutePath, "type2_${themeDate.type2.name}"), "res").absolutePath))
        }

        command.addAll(listOf("-S", res.absolutePath))

        logger.debug("Invoking: {}", command.joinToString(separator = " "))

        val proc = ProcessBuilder(command)
                .directory(tempDir)
                .start()

        proc.waitFor()

        val statusCode = proc.exitValue()
        val output = proc.inputStream.bufferedReader().use { it.readText() }
        val error = proc.errorStream.bufferedReader().use { it.readText() }

        if (statusCode != 0) {
            throw InvalidInvocationException(error)
        }

        return File(tempDir, "Theme.apk")
    }

    fun compileTheme(appId: String, dir: File, tempDir: File) = compileTheme(ThemeToCompile(appId), dir, tempDir)
}
