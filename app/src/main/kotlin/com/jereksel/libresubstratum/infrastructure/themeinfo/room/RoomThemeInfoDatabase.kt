/*
 * Copyright (C) 2017 Andrzej Ressel (jereksel@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.jereksel.libresubstratum.infrastructure.themeinfo.room

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