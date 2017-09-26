package com.jereksel.libresubstratum.domain

import com.jereksel.libresubstratum.data.NavigationBarOverlay
import com.jereksel.libresubstratumlib.ThemePack
import java.io.File

interface IThemeReader {
    fun readThemePack(location: File): ThemePack
    fun isThemeEncrypted(location: File): Boolean
    fun getNavigationBar(location: File, type: String): NavigationBarOverlay?
}