package com.jereksel.libresubstratum.domain

import com.jereksel.libresubstratumlib.ThemePack
import java.io.File

interface IThemeReader {
    fun readThemePack(appId: String): ThemePack
    fun isThemeEncrypted(appId: String): Boolean
}