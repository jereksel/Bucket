package com.jereksel.libresubstratum.domain

import android.content.Context
import com.jereksel.libresubstratum.data.KeyPair
import com.jereksel.libresubstratumlib.ThemePack

class ThemeReader(
        val context: Context,
        val packageManager: IPackageManager,
        val keyFinder: IKeyFinder
): IThemeReader {
    private val themeReaderImpl = com.jereksel.libresubstratumlib.ThemeReader()
    private val themeReaderAssetManager = com.jereksel.themereaderassetmanager.Reader

    override fun readThemePack(appId: String): ThemePack {
        val keyPair = keyFinder.getKey(appId) ?: KeyPair.EMPTYKEY
        val assets = context.packageManager.getResourcesForApplication(appId).assets
        return themeReaderAssetManager.read(assets, keyPair.getTransformer())
    }

    override fun isThemeEncrypted(appId: String): Boolean {
        val location = packageManager.getAppLocation(appId)
        return themeReaderImpl.checkIfEncrypted(location)
    }

}