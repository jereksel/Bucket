package com.jereksel.themereaderassetmanager

import android.app.Activity
import android.content.res.AssetManager
import android.os.Build
import com.jereksel.libresubstratumlib.*
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@Suppress("IllegalIdentifier")
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class,
        sdk = intArrayOf(Build.VERSION_CODES.LOLLIPOP)
)
class ReaderTest {

    lateinit var assetManager: AssetManager

    @Before
    fun setup() {
        assetManager = Robolectric.buildActivity(Activity::class.java).create().get().assets
    }

    @Config(assetDir = "../../src/test/resources/assets/VerySimpleTheme")
    @Test
    fun `VerySimpleTheme test`() {
        val themePack = Reader.read(assetManager)

        assertEquals(listOf("android", "com.android.settings", "com.android.systemui"), themePack.themes.map { it.application })
    }

    @Config(assetDir = "../../src/test/resources/assets/Type1Test")
    @Test
    fun `simple type1 test`() {
        val themePack = Reader.read(assetManager)
        val expected = listOf(Type1Extension("Initial color", true), Type1Extension("Green", false), Type1Extension("Red", false))

        assertNull(themePack.type3)
        assertNull(themePack.themes[0].type2)
        assertEquals(expected, themePack.themes[0].type1[0].extension)
    }

    @Config(assetDir = "../../src/test/resources/assets/Type2Test")
    @Test
    fun `simple type2 test`() {
        val themePack = Reader.read(assetManager)
        val expected = Type2Data(listOf(Type2Extension("Light", true), Type2Extension("Black", false), Type2Extension("Dark", false)))

        assertNull(themePack.type3)
        assertEquals(expected, themePack.themes[0].type2)
    }

    @Config(assetDir = "../../src/test/resources/assets/Type3Test")
    @Test
    fun `simple type3 test`() {
        val themePack = Reader.read(assetManager)
        val expected = Type3Data(listOf(Type3Extension("Light", true), Type3Extension("Black", false), Type3Extension("Dark", false)))

        assertEquals(expected, themePack.type3)
    }

    /*
    fun `simple type2 test`() {
        when:
        def theme2 = themeReader.readThemePack(new File(resources, "Type2Test.zip"))

        then:
        ["Light", "Black", "Dark"] == theme2.themes[0].type2.extensions.collect { it.name }
    }

    fun `simple type2 empty theme test`() {
        when:
        def theme2 = themeReader.readThemePack(new File(resources, "VerySimpleTheme.zip"))

        then:
        null == theme2.themes[0].type2
    }

    fun `Type3 test`() {
        when:
        def themePack = themeReader.readThemePack(new File(resources, "Type3Test.zip"))

        then:
        ["Light", "Black", "Dark"] == themePack.type3.extensions.collect { it.name }


    @Test
    fun test1() {


    }*/

}