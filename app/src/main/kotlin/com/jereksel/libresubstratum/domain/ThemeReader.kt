package com.jereksel.libresubstratum.domain

import java.io.File

class ThemeReader: IThemeReader {
    private val themeReaderImpl = com.jereksel.libresubstratumlib.ThemeReader()

    override fun readThemePack(location: File) = themeReaderImpl.readThemePack(location)

    override fun isThemeEncrypted(location: File) = themeReaderImpl.checkIfEncrypted(location)

}