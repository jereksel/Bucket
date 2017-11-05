package com.jereksel.libresubstratum.domain.db.themeinfo.room

import android.arch.persistence.room.Room
import android.content.Context
import com.jereksel.libresubstratum.domain.ThemePackDatabase
import com.jereksel.libresubstratumlib.*

/**
 * Not used, but leave it for now - who knows, maybe it'll be useful in future (we could do two layer caching with DB and Guava cache)
 */
class RoomThemePackDatabase(
        context: Context
): ThemePackDatabase {

    val lock = java.lang.Object()

    var db = Room.databaseBuilder(context, RoomThemeInfoDatabase::class.java, "themepack").allowMainThreadQueries().build()!!

    override fun addThemePack(appId: String, checksum: ByteArray, themePack: ThemePack) = synchronized(lock) {
        val roomThemePack = RoomThemePack()
        roomThemePack.appId = appId
        roomThemePack.checksum = checksum
//        println(roomThemePack.id)
        val themePackId = db.abstractThemeInfo().insertThemePack(roomThemePack)

        themePack.themes.forEach {
            val theme = RoomTheme()
            theme.targetId = it.application
            theme.themePackId = themePackId
            val themeId = db.abstractThemeInfo().insertTheme(theme)

            val type1a = it.type1.find { it.suffix == "a" }?.extension ?: listOf()
            val type1b = it.type1.find { it.suffix == "b" }?.extension ?: listOf()
            val type1c = it.type1.find { it.suffix == "c" }?.extension ?: listOf()

            val type2 = it.type2?.extensions ?: listOf()

            type1a.forEach {
                val roomType1 = RoomType1aExtension()
                roomType1.def = it.default
                roomType1.name = it.name
                roomType1.themeId = themeId
                db.abstractThemeInfo().insertType1aExtensions(roomType1)
            }

            type1b.forEach {
                val roomType1 = RoomType1bExtension()
                roomType1.def = it.default
                roomType1.name = it.name
                roomType1.themeId = themeId
                db.abstractThemeInfo().insertType1bExtensions(roomType1)
            }

            type1c.forEach {
                val roomType1 = RoomType1cExtension()
                roomType1.def = it.default
                roomType1.name = it.name
                roomType1.themeId = themeId
                db.abstractThemeInfo().insertType1cExtensions(roomType1)
            }

            type2.forEach {
                val roomType2 = RoomType2Extension()
                roomType2.def = it.default
                roomType2.name = it.name
                roomType2.themeId = themeId
                db.abstractThemeInfo().insertType2Extensions(roomType2)
            }

        }

        (themePack.type3 ?: Type3Data(listOf())).extensions.forEach {
            val type3 = RoomType3Extension()
            type3.def = it.default
            type3.name = it.name
            type3.themePackId = themePackId
            db.abstractThemeInfo().insertType3Extensions(type3)
        }

    }

    override fun getThemePack(appId: String): Pair<ThemePack, ByteArray>? = synchronized(lock) {
        val themePack = db.abstractThemeInfo().getThemePack(appId) ?: return null

        val themes = themePack.themeList.map {
            val themeId = it.id

            val theme = db.abstractThemeInfo().getThemeInfo(it.id)!!

            val type1a = Type1Data(theme.type1aExtension.map { Type1Extension(it.name, it.def) }.sortedWith(compareBy({ !it.default }, { it.name })), "a")
            val type1b = Type1Data(theme.type1bExtension.map { Type1Extension(it.name, it.def) }.sortedWith(compareBy({ !it.default }, { it.name })), "b")
            val type1c = Type1Data(theme.type1cExtension.map { Type1Extension(it.name, it.def) }.sortedWith(compareBy({ !it.default }, { it.name })), "c")

            val type2 = Type2Data(theme.type2Extension.map { Type2Extension(it.name, it.def) }.sortedWith(compareBy({ !it.default }, { it.name })))

            val type1s = listOf(type1a, type1b, type1c).filter { it.extension.isNotEmpty() }

            Theme(it.targetId, type1s, if(type2.extensions.isNotEmpty()) type2 else null)

        }

        val type3Extensions =
                themePack.type3Extension
                        .map { Type3Extension(it.name, it.def) }
                        .sortedWith(compareBy({ !it.default }, { it.name }))

        return Pair(ThemePack(themes, if(type3Extensions.isEmpty()) null else Type3Data(type3Extensions)), themePack.themePack.checksum)
    }

    override fun removeThemePack(appId: String) {
        db.abstractThemeInfo().deleteThemePack(appId)
    }

}