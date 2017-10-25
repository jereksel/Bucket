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
import java.io.InputStream
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@Suppress("IllegalIdentifier")
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class,
        sdk = intArrayOf(Build.VERSION_CODES.LOLLIPOP)
)
class EncryptedReaderTest {

    lateinit var assetManager: AssetManager
    lateinit var transformer: (InputStream) -> (InputStream)

    @Before
    fun setup() {
        assetManager = Robolectric.buildActivity(Activity::class.java).create().get().assets

        val key = ByteArray(16, { it.toByte() })
        val iv = ByteArray(16, { it.toByte() })


        transformer = {
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    SecretKeySpec(key.clone(), "AES"),
                    IvParameterSpec(iv.clone())
            )
            CipherInputStream(it, cipher)
        }
    }

    @Config(assetDir = "../../src/test/resources/assetsEncrypted/VerySimpleTheme")
    @Test
    fun `VerySimpleTheme test`() {
        val themePack = Reader.read(assetManager, transformer)

        assertEquals(listOf("android", "com.android.settings", "com.android.systemui"), themePack.themes.map { it.application })
    }

    @Config(assetDir = "../../src/test/resources/assetsEncrypted/Type1Test")
    @Test
    fun `simple type1 test`() {
        val themePack = Reader.read(assetManager, transformer)
        val expected = listOf(Type1Extension("Initial color", true), Type1Extension("Green", false), Type1Extension("Red", false))

        assertNull(themePack.type3)
        assertNull(themePack.themes[0].type2)
        assertEquals(expected, themePack.themes[0].type1[0].extension)
    }

    @Config(assetDir = "../../src/test/resources/assetsEncrypted/Type2Test")
    @Test
    fun `simple type2 test`() {
        val themePack = Reader.read(assetManager, transformer)
        val expected = Type2Data(listOf(Type2Extension("Light", true), Type2Extension("Black", false), Type2Extension("Dark", false)))

        assertNull(themePack.type3)
        assertEquals(expected, themePack.themes[0].type2)
    }

    @Config(assetDir = "../../src/test/resources/assetsEncrypted/Type3Test")
    @Test
    fun `simple type3 test`() {
        val themePack = Reader.read(assetManager, transformer)
        val expected = Type3Data(listOf(Type3Extension("Light", true), Type3Extension("Black", false), Type3Extension("Dark", false)))

        assertEquals(expected, themePack.type3)
    }

}