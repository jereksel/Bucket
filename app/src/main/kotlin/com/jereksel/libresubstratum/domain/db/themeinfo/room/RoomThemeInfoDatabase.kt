package com.jereksel.libresubstratum.domain.db.themeinfo.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = arrayOf(
        RoomTheme::class,
        RoomThemePack::class,
        RoomType1aExtension::class,
        RoomType1bExtension::class,
        RoomType1cExtension::class,
        RoomType2Extension::class,
        RoomType3Extension::class
), version = 1)
abstract class RoomThemeInfoDatabase: RoomDatabase() {
    abstract fun abstractThemeInfo(): RoomThemePackDao
}