package com.jereksel.libresubstratum.domain.db.themeinfo.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import io.reactivex.Flowable

@Dao
interface RoomThemePackDao {

    @Query("SELECT * FROM themepack")
    fun getAllThemePacks(): List<RoomThemePack>

    @Insert
    fun insertThemePack(themePack: RoomThemePack): Long

/*    @Query("SELECT * FROM themepack")
    fun getAllPeople(): List<RoomThemePack>

    @Insert
    fun insert(person: RoomThemePack)*/

}