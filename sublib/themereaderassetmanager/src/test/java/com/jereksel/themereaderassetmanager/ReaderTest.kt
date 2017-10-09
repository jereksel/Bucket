package com.jereksel.themereaderassetmanager

import android.app.Activity
import android.content.res.AssetManager
import android.os.Build
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class,
        sdk = intArrayOf(Build.VERSION_CODES.LOLLIPOP)
//        manifest = "build/intermediates/manifests/aapt/debug/AndroidManifest.xml",
//        assetDir = "../../src/test/resources/assets"
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

        assertEquals(listOf("Initial color", "Green", "Red"), themePack.themes[0].type1[0].extension.map { it.name })
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