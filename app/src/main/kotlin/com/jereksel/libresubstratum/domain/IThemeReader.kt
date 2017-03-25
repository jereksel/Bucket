package com.jereksel.libresubstratum.domain

import com.jereksel.libresubstratum.data.ThemePack
import java.io.File

interface IThemeReader {
    fun readThemePack(location: String): ThemePack
    fun readThemePack(location: File): ThemePack
}