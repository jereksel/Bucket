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

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface RoomThemePackDao {

    @Query("SELECT * FROM themepack")
    fun getAllThemePacks(): List<RoomThemePackFull>

    @Query("SELECT * FROM themepack WHERE appId LIKE :appId")
    fun getThemePack(appId: String): RoomThemePackFull?

    @Query("SELECT * FROM theme WHERE id LIKE :id")
    fun getThemeInfo(id: Long): RoomThemeFull?

    @Insert
    fun insertThemePack(themePack: RoomThemePack): Long

    @Insert
    fun insertTheme(theme: RoomTheme): Long

    @Insert
    fun insertType1aExtensions(ext: RoomType1aExtension): Long

    @Insert
    fun insertType1bExtensions(ext: RoomType1bExtension): Long

    @Insert
    fun insertType1cExtensions(ext: RoomType1cExtension): Long

    @Insert
    fun insertType2Extensions(ext: RoomType2Extension): Long

    @Insert
    fun insertType3Extensions(ext: RoomType3Extension): Long

    @Query("DELETE FROM themepack WHERE appId LIKE :appId")
    fun deleteThemePack(appId: String)

/*    @Query("SELECT * FROM themepack")
    fun getAllPeople(): List<RoomThemePack>

    @Insert
    fun insert(person: RoomThemePack)*/

}