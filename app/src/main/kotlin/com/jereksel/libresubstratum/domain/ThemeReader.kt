package com.jereksel.libresubstratum.domain

import java.io.File

typealias ThemeReaderImpl = com.jereksel.libresubstratumlib.ThemeReader

class ThemeReader: IThemeReader {
    override fun readThemePack(location: String) = ThemeReaderImpl().readThemePack(location)
    override fun readThemePack(location: File) = ThemeReaderImpl().readThemePack(location)
}