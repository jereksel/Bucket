package com.jereksel.libresubstratum.domain.db.themeinfo.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.support.annotation.Nullable
import io.reactivex.Flowable

@Dao
interface RoomThemePackDao {

    @Query("SELECT * FROM themepack")
    fun getAllThemePacks(): List<RoomThemeFull>

    @Query("SELECT * FROM themepack WHERE appId LIKE :id")
    fun getThemePack(id: String): RoomThemeFull?

    @Insert
    fun insertThemePack(themePack: RoomThemePack): Long

    @Insert
    fun insertTheme(theme: RoomTheme): Long
/*
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
    */
/*    @Query("SELECT * FROM themepack")
    fun getAllPeople(): List<RoomThemePack>

    @Insert
    fun insert(person: RoomThemePack)*/

}