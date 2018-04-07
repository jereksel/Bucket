package com.jereksel.libresubstratum.utils

import com.jereksel.libresubstratum.utils.ZipUtils.extractZip
import com.google.common.io.Files
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class ZipUtilsTest {

    val resources : File = File(javaClass.classLoader.getResource("Themeapp.apk").path).parentFile

    @Test
    fun `Zip extraction test`() {

        val zipLocation = File(resources, "Themeapp.apk")
        val tempFolder = Files.createTempDir()

        zipLocation.extractZip(tempFolder, "assets")

        val expectedFile = listOf(
                "android/type1a",
                "android/type1a_Green.xml",
                "android/type1a_Red.xml"
        )
                .map { "assets/overlays/" + it }
                .map { File(tempFolder, it) }
                .sorted()

        assertEquals(expectedFile, tempFolder.walk().filter { it.isFile }.toList().sorted())
        tempFolder.deleteRecursively()

    }

}