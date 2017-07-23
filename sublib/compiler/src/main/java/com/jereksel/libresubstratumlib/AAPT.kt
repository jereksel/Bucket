package com.jereksel.libresubstratumlib

import com.jereksel.libresubstratumlib.AAPT.compileTheme
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object AAPT {

    fun compileTheme(appId: String, dir: File, tempDir: File) {

        //aapt package -m -J gen/ -M ./AndroidManifest.xml -S res1/ -S res2 ... -I android.jar

//        val temp = Files.createTempDirectory(null)

//        aapt0(argsMap = )



    }

    private fun aapt0(argsList: List<String> = listOf(), argsMap: Map<String, String> = mapOf()): Triple<Int, String, String> {

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

        return Triple(statusCode, output, error)
    }

}

fun main(args: Array<String>) {
//        val temp = Files.createTempDirectory(null)

    compileTheme("com.jereksel.test", File("D:\\MojeProgramy\\LibreSubstratum\\sublib\\compiler\\src\\test\\resources\\basicTheme"), File("D:\\temp_compile"))

}