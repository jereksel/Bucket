package com.jereksel.libresubstratum.domain.db.themeinfo.room

import com.jereksel.libresubstratum.BaseRobolectricTest
import com.jereksel.libresubstratum.domain.ThemePackDatabase
import com.jereksel.libresubstratumlib.*
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.robolectric.RuntimeEnvironment

class RoomThemeInfoDBTest: BaseRobolectricTest() {

    lateinit var db: ThemePackDatabase

    @Before
    fun setUp() {
        db = RoomThemePackDatabase(RuntimeEnvironment.application)
    }

    @Test
    fun `Basic theme pack test`() {
        val themePack = ThemePack(listOf(Theme("app1"), Theme("app2")))
        db.addThemePack("asd", themePack)
        assertEquals(themePack, db.getThemePack("asd"))
    }

    @Test
    fun `Type1 theme pack test`() {
        val type1a = Type1Data(listOf(Type1Extension("yellow", true), Type1Extension("blue", false), Type1Extension("teal", false)), "a")
        val type1b = Type1Data(listOf(Type1Extension("red", true), Type1Extension("pink", false), Type1Extension("zzz", false)), "b")
        val type1c = Type1Data(listOf(Type1Extension("white", true), Type1Extension("black", false), Type1Extension("ping", false)), "c")
        val themePack = ThemePack(listOf(Theme("app1", listOf(type1a, type1c)), Theme("app2", listOf(type1b)), Theme("app3")))
        db.addThemePack("asd", themePack)
        assertEquals(themePack, db.getThemePack("asd"))
    }

    @Test
    fun `Type2 theme pack test`() {
        val type2a = Type2Data(listOf(Type2Extension("yellow", true), Type2Extension("blue", false), Type2Extension("purple", false)))
        val type2b = Type2Data(listOf(Type2Extension("red", true), Type2Extension("green", false), Type2Extension("pink", false)))
        val themePack = ThemePack(listOf(Theme("app1", type2 = type2a), Theme("app2", type2 = type2b), Theme("app3")))
        db.addThemePack("asd", themePack)
        assertEquals(themePack, db.getThemePack("asd"))
    }

    @Test
    fun `Type3 theme pack test`() {
        val type3 = Type3Data(listOf(Type3Extension("yellow", true), Type3Extension("blue", false), Type3Extension("purple", false)))
        val themePack = ThemePack(listOf(Theme("app1"), Theme("app2"), Theme("app3")), type3)
        db.addThemePack("asd", themePack)
        assertEquals(themePack, db.getThemePack("asd"))
    }

}