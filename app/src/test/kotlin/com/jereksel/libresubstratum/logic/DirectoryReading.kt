package com.jereksel.libresubstratum.logic

import com.jereksel.libresubstratum.domain.ThemeReader
import junit.framework.Assert.assertEquals
import org.junit.Test
import java.io.File


class DirectoryReading {

    val resources : File = File(javaClass.classLoader.getResource("resource.json").path).parentFile

    val themeReader = ThemeReader()

    @Test
    fun `simple theme pack test`() {
        val themeLocation = themeReader.readThemePack(File(resources, "VerySimpleTheme"))
        assertEquals(listOf("android", "com.android.settings", "com.android.systemui"), themeLocation.themes.map { it.application })
    }

    @Test
    fun `simple type3 test`() {
        val theme3 = themeReader.readType3Data(File(File(resources, "Type3Test"), "overlays"))
        assertEquals(listOf("Light", "Black", "Dark"), theme3.extensions.map { it.name })
    }

    @Test
    fun `type 1 test`() {
        val theme1 = themeReader.readType1Data(File(resources, "Type1Test", "overlays", "android"))
        assertEquals(listOf("a"), theme1.map { it.suffix })
    }

    fun File(init: File, vararg sub: String) : File {

        return sub.fold(init) {
            acc, elem ->
            java.io.File(acc, elem)
        }

    }

}