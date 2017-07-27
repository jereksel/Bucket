package com.jereksel.libresubstratumlib

import groovy.transform.CompileStatic
import org.junit.Test

import java.nio.file.Files

@CompileStatic
class CompilationTest {

    @Test
    def void test1() {
        def f = Files.createTempDirectory(null).toFile()

        println(f)

//        AAPT.INSTANCE.compileTheme()
        AAPT.INSTANCE.compileTheme("com.jereksel.test", new File("D:\\MojeProgramy\\LibreSubstratum\\sublib\\compiler\\src\\test\\resources\\basicTheme"), f)

//
//        def manifest = AndroidManifestGenerator.INSTANCE.generateManifest("com.jereksel.testtheme")
//
//        println(f)
    }
}