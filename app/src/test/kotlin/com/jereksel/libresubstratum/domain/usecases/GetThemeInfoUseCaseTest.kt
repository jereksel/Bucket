package com.jereksel.libresubstratum.domain.usecases

import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.IThemeReader
import com.jereksel.libresubstratum.domain.ThemePackDatabase
import com.jereksel.libresubstratumlib.ThemePack
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.specs.FunSpec
import junit.framework.Assert.*
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.codec.digest.MessageDigestAlgorithms.MD5
import org.junit.Assert.assertArrayEquals
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.io.File

class GetThemeInfoUseCaseTest: FunSpec() {

    @Mock
    lateinit var packageManager: IPackageManager
//    @Mock
    lateinit var themePackDatabase: HashMapThemePackDatabase
    @Mock
    lateinit var themeReader: IThemeReader

    lateinit var usecase: GetThemeInfoUseCase

    val resources = File(File(javaClass.classLoader.getResource("a").path).parentFile, "usecases/getthemeinfo")

    val appId = "app"

    override fun beforeEach() {
        MockitoAnnotations.initMocks(this)
        themePackDatabase = HashMapThemePackDatabase()
        usecase = GetThemeInfoUseCase(packageManager, themePackDatabase, themeReader)
    }

    init {
        test("When app doesn't contains manifest pack is returned without saving") {

            val themePack = ThemePack()
            val apk = File(resources, "nomanifest.apk")

            whenever(themeReader.readThemePack(appId)).thenReturn(themePack)
            whenever(packageManager.getAppLocation(appId)).thenReturn(apk)

            val pack = usecase.getThemeInfo(appId)

            assertSame(themePack, pack)
            assertEquals(0, themePackDatabase.map.size)
        }
        test("When app is not in database and has checksum its saved in database") {

            val themePack = ThemePack()
            val checksum = DigestUtils(MD5).digest(File(resources, "withmanifest.MANIFEST.MF"))

            val apk = File(resources, "withmanifest.apk")

            whenever(themeReader.readThemePack(appId)).thenReturn(themePack)
            whenever(packageManager.getAppLocation(appId)).thenReturn(apk)

            val pack = usecase.getThemeInfo(appId)

            assertSame(themePack, pack)
            assertEquals(1, themePackDatabase.map.size)
            assertSame(themePack, themePackDatabase.map[appId]!!.first)
            assertArrayEquals(checksum, themePackDatabase.map[appId]!!.second)
        }
        test("When app is in database and checksum differs its replaced") {

            val themePack = ThemePack()
            val themePack2 = ThemePack()

            //Just to be sure
            assertNotSame(themePack, themePack2)

            themePackDatabase.map.put(appId, Pair(themePack2, byteArrayOf(1)))

            val checksum = DigestUtils(MD5).digest(File(resources, "withmanifest.MANIFEST.MF"))

            val apk = File(resources, "withmanifest.apk")

            whenever(themeReader.readThemePack(appId)).thenReturn(themePack)
            whenever(packageManager.getAppLocation(appId)).thenReturn(apk)

            val pack = usecase.getThemeInfo(appId)

            assertSame(themePack, pack)
            assertNotSame(themePack2, pack)
            assertEquals(1, themePackDatabase.map.size)
            assertSame(themePack, themePackDatabase.map[appId]!!.first)
            assertArrayEquals(checksum, themePackDatabase.map[appId]!!.second)

        }
        test("When app is in database and checksum it the same its returned without reading theme") {

            val themePack = ThemePack()

            val checksum = DigestUtils(MD5).digest(File(resources, "withmanifest.MANIFEST.MF"))

            themePackDatabase.map.put(appId, Pair(themePack, checksum))

            val apk = File(resources, "withmanifest.apk")

            whenever(packageManager.getAppLocation(appId)).thenReturn(apk)

            val pack = usecase.getThemeInfo(appId)

            assertSame(themePack, pack)
            assertEquals(1, themePackDatabase.map.size)
            assertSame(themePack, themePackDatabase.map[appId]!!.first)
            assertArrayEquals(checksum, themePackDatabase.map[appId]!!.second)
            verifyNoMoreInteractions(themeReader)

        }

    }


    class HashMapThemePackDatabase: ThemePackDatabase {

        val map = mutableMapOf<String, Pair<ThemePack, ByteArray>>()

        override fun addThemePack(appId: String, checksum: ByteArray, themePack: ThemePack) {
            map.put(appId, Pair(themePack, checksum))
        }

        override fun getThemePack(appId: String) = map[appId]

        override fun removeThemePack(appId: String) {
            map.remove(appId)
        }

    }

}