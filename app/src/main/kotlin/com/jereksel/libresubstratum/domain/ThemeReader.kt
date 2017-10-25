package com.jereksel.libresubstratum.domain

import com.jereksel.libresubstratumlib.ThemePack

class ThemeReader(
        val packageManager: IPackageManager
): IThemeReader {
    private val themeReaderImpl = com.jereksel.libresubstratumlib.ThemeReader()
    private val themeReaderExtractlessImpl = com.jereksel.libresubstratumlib.ThemeReaderExtractless()

    override fun readThemePack(appId: String): ThemePack {
        val location = packageManager.getAppLocation(appId)
        return themeReaderExtractlessImpl.readThemePack(location)
    }

    override fun isThemeEncrypted(appId: String): Boolean {
        val location = packageManager.getAppLocation(appId)
        return themeReaderImpl.checkIfEncrypted(location)
    }

}