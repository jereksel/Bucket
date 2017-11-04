package com.jereksel.libresubstratum.domain

import com.jereksel.libresubstratumlib.ThemePack

interface ThemePackDatabase {
    fun addThemePack(appId: String, checksum: ByteArray, themePack: ThemePack)
    fun getThemePack(appId: String): Pair<ThemePack, ByteArray>?
    fun removeThemePack(appId: String)
}