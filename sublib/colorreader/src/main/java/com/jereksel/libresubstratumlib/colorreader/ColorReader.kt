package com.jereksel.libresubstratumlib.colorreader

import java.io.File
import java.util.regex.Pattern

object ColorReader {

    private val COLOR_PATTERN = Pattern.compile(":color/(.*):.*?d=(\\S*)")

    fun getColorsValues(aapt: File, apk: File): List<Color> {

        val cmd = listOf(aapt.absolutePath, "d", "resources", apk.absolutePath)
        val proc = ProcessBuilder(cmd).start()

        val statusCode = proc.waitFor()
        val output = proc.inputStream.bufferedReader().use { it.readText() }
        val error = proc.errorStream.bufferedReader().use { it.readText() }

        if (statusCode != 0) {
            throw RuntimeException(error)
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
                .toList()
    }

    fun getColorValue(aapt:File, apk: File, color: String) = getColorsValues(aapt, apk).first { it.name == color }.value

}