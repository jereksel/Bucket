package com.jereksel.libresubstratum.domain

import com.jereksel.libresubstratumlib.ThemePack
import java.io.File

interface IThemeReader {
    fun readThemePack(location: File): ThemePack
    fun isThemeEncrypted(location: File): Boolean
}