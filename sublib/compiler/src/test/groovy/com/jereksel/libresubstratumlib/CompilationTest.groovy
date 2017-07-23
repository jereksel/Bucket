package com.jereksel.libresubstratumlib

import org.junit.Test

import java.nio.file.Files

class CompilationTest {

    @Test
    def void test1() {
        def f = Files.createTempDirectory(null)

        def manifest = AndroidManifestGenerator.INSTANCE.generateManifest("com.jereksel.testtheme")

        println(f)
    }
}