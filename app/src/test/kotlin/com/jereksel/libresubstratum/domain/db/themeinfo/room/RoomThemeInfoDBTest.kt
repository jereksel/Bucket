package com.jereksel.libresubstratum.domain.db.themeinfo.room

import com.jereksel.libresubstratum.BaseRobolectricTest
import com.jereksel.libresubstratum.domain.ThemePackDatabase
import com.jereksel.libresubstratumlib.Theme
import com.jereksel.libresubstratumlib.ThemePack
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

}