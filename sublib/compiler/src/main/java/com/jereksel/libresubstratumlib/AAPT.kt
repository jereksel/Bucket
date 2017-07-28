package com.jereksel.libresubstratumlib

import com.jereksel.libresubstratumlib.AAPT.compileTheme
import com.jereksel.libresubstratumlib.AndroidManifestGenerator.generateManifest
import java.io.File
import java.nio.file.Files
import java.util.regex.Pattern

object AAPT {

    private val COLOR_PATTERN = Pattern.compile(":color/(.*):.*?d=(\\S*)")

    fun getColorsValues(apk: File): Sequence<Color> {

        val cmd = listOf("aapt", "d", "resources", apk.absolutePath)
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
                        println(matcher.groupCount())
                        Color(matcher.group(1), matcher.group(2))
                    } else {
                        null
                    }
                }
    }

    fun getColorValue(apk: File, color: String): String {

        return getColorsValues(apk).first { it.name == color }.value

//        val cmd = listOf("aapt", "d", "resources", apk.absolutePath)
//
//        val proc = ProcessBuilder(cmd).start()
//
//        proc.waitFor()
//        val statusCode = proc.exitValue()
//        val output = proc.inputStream.bufferedReader().use { it.readText() }
//        val error = proc.errorStream.bufferedReader().use { it.readText() }
//
//        if (statusCode != 0) {
//            throw InvalidInvocationException(error)
//        }
//
//        println(output)

//        return output.lineSequence()


    }

    private fun aapt0(argsList: List<String> = listOf(), argsMap: Map<String, String> = mapOf()): String {

        val args = argsMap.map { listOf(it.key, it.value) }.fold(listOf<String>(), {l1, l2 -> l1 + l2 })

        val proc = ProcessBuilder(listOf("aapt", "package") + argsList + args)
                .directory(File("D://"))
//                .redirectOutput(ProcessBuilder.Redirect.PIPE)
//                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

        proc.waitFor()
        val statusCode = proc.exitValue()
        val output = proc.inputStream.bufferedReader().use { it.readText() }
        val error = proc.errorStream.bufferedReader().use { it.readText() }

        if (statusCode != 0) {
            throw InvalidInvocationException(error)
        }

        return output
    }

    fun compileTheme(appId: String, dir: File, tempDir: File): String {

        val manifest = generateManifest(appId)
        val manifestFile = File(tempDir, "AndroidManifest.xml")

        manifestFile.createNewFile()
        manifestFile.writeText(manifest)

        val res = File(dir, "res")

        File(tempDir, "gen").mkdir()

//        val command = listOf("aapt", "package", "-m", "-J", "gen", "-M", "AndroidManifest.xml", "-S", res.absolutePath)
        val command = listOf("aapt", "package", "-f", "-M", "AndroidManifest.xml", "-S", res.absolutePath, "-F", "Theme.apk.unaligned")

        println(command.joinToString(separator = " "))

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

        return output

        //aapt package -m -J gen/ -M ./AndroidManifest.xml -S res1/ -S res2 ... -I android.jar

//        val temp = Files.createTempDirectory(null)

//        aapt0(argsMap = )


//        "aapt d resources"


    }

}

//fun main(args: Array<String>) {
//        val temp = Files.createTempDirectory(null)
//
//
//}