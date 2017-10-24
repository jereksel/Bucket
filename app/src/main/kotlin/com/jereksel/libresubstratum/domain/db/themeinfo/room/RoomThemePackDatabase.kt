package com.jereksel.libresubstratum.domain.db.themeinfo.room

import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.jereksel.libresubstratum.domain.ThemePackDatabase
import com.jereksel.libresubstratumlib.Theme
import com.jereksel.libresubstratumlib.ThemePack
import com.jereksel.libresubstratumlib.Type3Data
import com.jereksel.libresubstratumlib.Type3Extension

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

        (themePack.type3 ?: Type3Data(listOf())).extensions.forEach {
            val type3 = RoomType3Extension()
            type3.def = it.default
            type3.name = it.name
            type3.themePackId = themePackId
            db.abstractThemeInfo().insertType3Extensions(type3)
        }

    }

    override fun getThemePack(appId: String): ThemePack? {
        val themePack = db.abstractThemeInfo().getThemePack(appId) ?: return null

        val type3Extensions =
                themePack.type3Extension
                        .map { Type3Extension(it.name, it.def) }
                        .sortedWith(compareBy({ !it.default }, { it.name }))

        return ThemePack(themePack.themeList.map { Theme(it.targetId) }, if(type3Extensions.isEmpty()) null else Type3Data(type3Extensions))
    }

}