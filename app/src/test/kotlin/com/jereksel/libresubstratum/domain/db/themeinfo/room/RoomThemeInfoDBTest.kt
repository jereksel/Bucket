package com.jereksel.libresubstratum.domain.db.themeinfo.room

import com.jereksel.libresubstratum.BaseRobolectricTest
import com.jereksel.libresubstratum.domain.ThemePackDatabase
import com.jereksel.libresubstratumlib.ThemePack
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
    fun test1() {
        db.addThemePack("asd", ThemePack())
    }

}