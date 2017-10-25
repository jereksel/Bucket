package com.jereksel.libresubstratum.domain.usecases

import android.content.Context
import com.jereksel.libresubstratumlib.ThemePack
import com.jereksel.themereaderassetmanager.Reader.read

class GetThemeInfoUseCase(
        val context: Context
): IGetThemeInfoUseCase {

    override fun getThemeInfo(appId: String): ThemePack {
        val assets = context.packageManager.getResourcesForApplication(appId).assets
        return read(assets)
    }
}