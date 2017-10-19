package com.jereksel.libresubstratum.domain.db.themeinfo.room

import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.jereksel.libresubstratum.domain.ThemePackDatabase
import com.jereksel.libresubstratumlib.ThemePack

class RoomThemePackDatabase(
        context: Context
): ThemePackDatabase {

    var db = Room.databaseBuilder(context, RoomThemeInfoDatabase::class.java, "themepack").allowMainThreadQueries().build()!!

    override fun addThemePack(appId: String, themePack: ThemePack) {
        val roomThemePack = RoomThemePack()
        roomThemePack.themeId = appId
//        println(roomThemePack.id)
        db.abstractThemeInfo().insertThemePack(roomThemePack)
        println(roomThemePack.id)
    }

}