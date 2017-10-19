package com.jereksel.libresubstratum.domain

import com.jereksel.libresubstratumlib.ThemePack

interface ThemePackDatabase {
    fun addThemePack(appId: String, themePack: ThemePack)
}