package com.jereksel.libresubstratum.domain

import java.io.File

class ThemeReader: IThemeReader {
    private val themeReaderImpl = com.jereksel.libresubstratumlib.ThemeReader()
    private val themeReaderExtractlessImpl = com.jereksel.libresubstratumlib.ThemeReaderExtractless()

    override fun readThemePack(location: File) = themeReaderExtractlessImpl.readThemePack(location)

    override fun isThemeEncrypted(location: File) = themeReaderImpl.checkIfEncrypted(location)

}