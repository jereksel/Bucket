package com.jereksel.libresubstratum.domain.db.themeinfo.room

import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.jereksel.libresubstratum.domain.ThemePackDatabase
import com.jereksel.libresubstratumlib.Theme
import com.jereksel.libresubstratumlib.ThemePack

class RoomThemePackDatabase(
        context: Context
): ThemePackDatabase {

    var db = Room.databaseBuilder(context, RoomThemeInfoDatabase::class.java, "themepack").allowMainThreadQueries().build()!!

    override fun addThemePack(appId: String, themePack: ThemePack) {
        val roomThemePack = RoomThemePack()
        roomThemePack.appId = appId
//        println(roomThemePack.id)
        val themePackId = db.abstractThemeInfo().insertThemePack(roomThemePack)

        themePack.themes.forEach {
            val theme = RoomTheme()
            theme.targetId = it.application
            theme.themePackId = themePackId
            db.abstractThemeInfo().insertTheme(theme)
        }
//        println(roomThemePack.id)
    }

    override fun getThemePack(appId: String): ThemePack? {
        val themePack = db.abstractThemeInfo().getThemePack(appId) ?: return null
        return ThemePack(themePack.themeList.map { Theme(it.targetId) })
    }

}