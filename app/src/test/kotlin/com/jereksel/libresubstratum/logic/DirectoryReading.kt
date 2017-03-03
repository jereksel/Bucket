package com.jereksel.libresubstratum.logic

import com.jereksel.libresubstratum.domain.ThemeReader
import junit.framework.Assert.assertEquals
import org.junit.Test
import java.io.File


class DirectoryReading {

    val resources : File = File(javaClass.classLoader.getResource("resource.json").path).parentFile

    @Test
    fun `simple theme pack test`() {
        val themeLocation = ThemeReader().readThemePack(File(resources, "VerySimpleTheme"))
        assertEquals(themeLocation.themes.map { it.application }, listOf("android", "com.android.settings", "com.android.systemui"))
    }

    @Test
    fun `simple type3 test`() {
        val theme3 = ThemeReader().readType3Data(File(File(resources, "Type3Test"), "overlays"))
    }

}